package com.mindnotix.texibee.activitys.settings.car

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.callback.AlertDialogClick
import com.mindnotix.texibee.data.model.firebasee.Vehicle
import com.mindnotix.texibee.data.model.firebasee.VehicleList
import com.mindnotix.texibee.databinding.ActivityCarListBinding
import com.mindnotix.texibee.databinding.CarListItemBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.mindnotix.texibee.views.alertdialog.AlertDialog
import org.json.JSONArray
import java.lang.reflect.Type
import java.util.*

class CarListActivity : BaseActivity(), AlertDialogClick {
    lateinit var binding: ActivityCarListBinding
    private var Selected_id = ""
    private lateinit var vehicle: Vehicle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_car_list)
        ToolbarSupport(getString(R.string.car), binding.toolbar, this)
        AddData()
        ClickEvents()
    }

    private fun ClickEvents() {
        binding.llNewCar.setOnClickListener {
            startActivity(
                Intent(this, NewCarActivity::class.java).putExtra(
                    Constant.IS_CAR_CREATE,
                    true
                )
            )
            overridePendingTransition(0, R.anim.fade)
        }
    }

    private fun AddData() {

        Firebase.firestore.collection(Constant.VEHICLE)
            .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        vehicle = querySnapshot.toObject(Vehicle::class.java)!!
                        if (vehicle.vehicle_array != "[]") {
                            val gson = Gson()
                            val listType: Type =
                                object : TypeToken<List<VehicleList?>?>() {}.type
                            val vehicleList: List<VehicleList> =
                                gson.fromJson(vehicle.vehicle_array, listType)

                            RvCarListAdapter(vehicleList)
                        }


                    }
                }

            }
    }


    @SuppressLint("SetTextI18n")
    fun RvCarListAdapter(data: List<VehicleList>) {
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.car_list_item)
        binding.RvCarList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvCarList.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: CarListItemBinding = viewDataBinding as CarListItemBinding
            binding.txtCarName.text = data[position].vehicle_name
            binding.txtCarModel.text = data[position].vehicle_model
            binding.txtCarNumber.text = data[position].vehicle_number
            Glide.with(this).load(data[position].vehicle_image).into(binding.imgcar)
            binding.icMenu.setOnClickListener {
                Selected_id = data[position].id
                openOptionMenu(binding.icMenu, position, data)
            }
        }
    }


    fun openOptionMenu(v: View, position: Int, data: List<VehicleList>) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater
            .inflate(R.menu.driver_list_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.edit_menu) {
                startActivity(
                    Intent(this, NewCarActivity::class.java).putExtra(
                        Constant.IS_CAR_CREATE,
                        false
                    )
                        .putExtra(Constant.VEHICLE_IMAGE, data[position].vehicle_image)
                        .putExtra(Constant.vehicle_name, data[position].vehicle_name)
                        .putExtra(Constant.vehicle_model, data[position].vehicle_model)
                        .putExtra(Constant.vehicle_numer, data[position].vehicle_number)
                        .putExtra(Constant.ID, data[position].id)
                )
                overridePendingTransition(0, R.anim.fade)

            }
            if (item.itemId == R.id.delete_menu) {
                AlertDialog(
                    this,
                    getString(R.string.delete_car),
                    getString(R.string.are_you_sure_you_want_to_delete_car_details),
                    getString(R.string.cancel),
                    getString(R.string.delete),
                    this,
                    R.drawable.ic_warning
                )
            }
            true
        }
        popup.show()
    }

    override fun OnCancelCLick() {
    }


    override fun OnActionClick() {
        ShowLoading(getString(R.string.DeletingCar))
        val jsonArray = JSONArray(vehicle.vehicle_array)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (Selected_id == jsonObject.getString(Constant.ID)) {
                jsonArray.remove(i)
                break
            }
        }
        val Edit_params: HashMap<String, String> = HashMap()
        Edit_params[Constant.vehicle_array] = jsonArray.toString()
        val dbCourses = FirebaseFirestore.getInstance().collection(Constant.VEHICLE)
        dbCourses.document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
            .update(Edit_params as Map<String, Any>)
            .addOnSuccessListener {
                HideLoading()
                // after the data addition is successful
                Toast.makeText(this, getString(R.string.CarDeleted), Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                HideLoading()// this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(this, "Fail  \n$e", Toast.LENGTH_SHORT)
                    .show()
            }

    }
}
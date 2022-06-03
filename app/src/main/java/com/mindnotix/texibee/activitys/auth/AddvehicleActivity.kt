package com.mindnotix.texibee.activitys.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.settings.price.AddPrincingActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.callback.VehicleAddedClick
import com.mindnotix.texibee.data.model.firebasee.Vehicle
import com.mindnotix.texibee.data.model.firebasee.VehicleList
import com.mindnotix.texibee.databinding.ActivityAddvehicleBinding
import com.mindnotix.texibee.databinding.CarListItemBinding
import com.mindnotix.texibee.fragment.AddVehicleFragment
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.COMEFROMLOGIN
import com.theartofdev.edmodo.cropper.CropImage
import java.lang.reflect.Type
import java.util.*


class AddvehicleActivity : AppCompatActivity(), VehicleAddedClick {
    private lateinit var binding: ActivityAddvehicleBinding
    private lateinit var vehicle: Vehicle
    private lateinit var vehicleFragment: AddVehicleFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_addvehicle)
        initivews()
        ClickEvents()
    }

    private fun ClickEvents() {
        // EstimatePriceFragment().show(supportFragmentManager, "TAG")
        binding.fabAdd.setOnClickListener {
            vehicleFragment = AddVehicleFragment(intent.getStringExtra("email").toString(), this)
            vehicleFragment.show(
                supportFragmentManager,
                "TAG"
            )
        }
        binding.btnAdd.setOnClickListener {
            startActivity(
                Intent(this, AddPrincingActivity::class.java).putExtra(COMEFROMLOGIN, true)
            )
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                vehicleFragment.vehicleFragment(result.uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun initivews() {

        Firebase.firestore.collection(Constant.VEHICLE)
            .document(intent.getStringExtra("email").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        vehicle = querySnapshot.toObject(Vehicle::class.java)!!
                        if (vehicle.vehicle_array != "[]") {
                            binding.txtEmptyVehicle.visibility = View.GONE
                            val gson = Gson()
                            val listType: Type =
                                object : TypeToken<List<VehicleList?>?>() {}.type
                            val vehicleList: ArrayList<VehicleList> =
                                gson.fromJson(vehicle.vehicle_array, listType)
                            RvCarListAdapter(vehicleList)
                            binding.btnAdd.visibility = View.VISIBLE
                        } else {
                            binding.txtEmptyVehicle.visibility = View.VISIBLE
                        }

                    } else {
                        binding.txtEmptyVehicle.visibility = View.VISIBLE
                    }


                }
            }

    }

    override fun VehicleAddedClick() {
        binding.btnAdd.visibility = View.VISIBLE
        initivews()
    }


    @SuppressLint("SetTextI18n")
    private fun RvCarListAdapter(data: ArrayList<VehicleList>) {
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.car_list_item)
        binding.RvVehicle.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvVehicle.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: CarListItemBinding = viewDataBinding as CarListItemBinding
            binding.txtCarName.text = data[position].vehicle_name
            binding.txtCarModel.text = data[position].vehicle_model
            binding.txtCarNumber.text = data[position].vehicle_number
            Glide.with(this).load(data[position].vehicle_image).into(binding.imgcar)

            binding.icMenu.visibility = View.GONE
        }
    }

}
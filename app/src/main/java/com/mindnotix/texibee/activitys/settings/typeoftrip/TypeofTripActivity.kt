package com.mindnotix.texibee.activitys.settings.typeoftrip

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.data.model.firebasee.TripType
import com.mindnotix.texibee.data.model.firebasee.TripTypeListModel
import com.mindnotix.texibee.databinding.ActivityTypeofTripBinding
import com.mindnotix.texibee.databinding.TripListItemBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.MnxPreferenceManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*

class TypeofTripActivity : BaseActivity() {
    lateinit var binding: ActivityTypeofTripBinding
    private val list: ArrayList<String> = ArrayList()
    private lateinit var tripType: TripType
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_typeof_trip)
        ToolbarSupport(getString(R.string.type_of_trips), binding.toolbar, this)
        ClickEvents()
        AddData()
    }


    private fun ClickEvents() {
        binding.llnewTrip.setOnClickListener {
            binding.llnewTrip.visibility = View.GONE
            binding.llCreateTrip.visibility = View.VISIBLE
            binding.nestedscroll.post { binding.nestedscroll.fullScroll(View.FOCUS_DOWN) }
        }
        binding.btnSave.setOnClickListener {
            HideKeyboard(this)
            binding.llnewTrip.visibility = View.VISIBLE
            binding.llCreateTrip.visibility = View.GONE
            if (binding.edtTripName.text.isNotEmpty()) {
                val dbTRips = FirebaseFirestore.getInstance().collection(Constant.TRIPTYPE)
                val jsonArray = JSONArray(tripType.trip_type_array)
                val addTripobj = JSONObject()
                addTripobj.put(Constant.ID, UUID.randomUUID().toString())
                addTripobj.put(
                    Constant.trip_type,
                    binding.edtTripName.text.toString()
                )
                jsonArray.put(addTripobj)
                val triptypemodel = TripType(
                    trip_type_array = jsonArray.toString()
                )
                dbTRips.document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString()).set(triptypemodel)
                    .addOnSuccessListener {
                        // after the data addition is successful
                        Toast.makeText(
                            this,
                            getString(R.string.TripAdded),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e -> // this method is called when the data addition process is failed.
                        // displaying a toast message when data addition is failed.
                        Toast.makeText(
                            this,
                            "Fail to add dbTRips \n$e",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }


                binding.edtTripName.setText("")
            }
            binding.nestedscroll.post { binding.nestedscroll.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private fun AddData() {
        Firebase.firestore.collection(Constant.TRIPTYPE)
            .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        tripType = querySnapshot.toObject(TripType::class.java)!!
                        val gson = Gson()
                        val listType: Type =
                            object : TypeToken<List<TripTypeListModel?>?>() {}.type
                        val triptypelist: ArrayList<TripTypeListModel> =
                            gson.fromJson(tripType.trip_type_array, listType)
                        RvDriverListAdapter(triptypelist)
                    }
                }
            }


    }


    @SuppressLint("SetTextI18n")
    private fun RvDriverListAdapter(data: ArrayList<TripTypeListModel>) {
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.trip_list_item)
        binding.RvTrip.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvTrip.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: TripListItemBinding = viewDataBinding as TripListItemBinding
            binding.txtTripname.text = data[position].trip_type
            binding.icMenu.setOnClickListener {
                openOptionMenu(binding.icMenu, position)
            }
        }
    }

    fun openOptionMenu(v: View, position: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater
            .inflate(R.menu.driver_list_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.edit_menu) {

            }
            true
        }
        popup.show()
    }
}
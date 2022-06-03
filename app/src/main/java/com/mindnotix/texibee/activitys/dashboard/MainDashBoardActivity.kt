package com.mindnotix.texibee.activitys.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.activitys.dashboard.gps.GpsMapActivity
import com.mindnotix.texibee.activitys.dashboard.manuatrip.ManualTripActivity
import com.mindnotix.texibee.activitys.dashboard.pricecals.PriceCalculatorActivity
import com.mindnotix.texibee.activitys.dashboard.triphistory.TripHistoryActivity
import com.mindnotix.texibee.activitys.settings.SettingsActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.data.model.DriverSelectModel
import com.mindnotix.texibee.data.model.firebasee.Trip
import com.mindnotix.texibee.data.model.firebasee.Vehicle
import com.mindnotix.texibee.data.model.firebasee.VehicleList
import com.mindnotix.texibee.databinding.ActivityMainDashBoardBinding
import com.mindnotix.texibee.databinding.DropdownItemBinding
import com.mindnotix.texibee.databinding.DropdownItemCarBinding
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.CURRENT_USER_MAKING_TRIP_OBJ
import com.mindnotix.texibee.utils.Constant.Companion.CURRENT_USER_TRIP_ARRAY
import com.mindnotix.texibee.utils.Constant.Companion.USER_MAIL
import com.mindnotix.texibee.utils.Constant.Companion.USER_NAME
import com.mindnotix.texibee.utils.Constant.Companion.car_detail
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.mindnotix.texibee.utils.NativeDialogClickListener
import org.json.JSONArray
import java.lang.reflect.Type
import java.util.*

class MainDashBoardActivity : BaseActivity() {
    var PERMISSIONS_MULTIPLE_REQUEST = 45
    lateinit var binding: ActivityMainDashBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_dash_board)
        AddDriverSelect()
        GetCarList()
        checkPermissions()
        CLickEvents()
        AddInitailTripData()
    }

    private fun AddInitailTripData() {


    }

    private fun GetCarList() {

        Firebase.firestore.collection(Constant.VEHICLE)
            .document(MnxPreferenceManager.getString(USER_MAIL, "").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        val vehicle = querySnapshot.toObject(Vehicle::class.java)!!
                        if (vehicle.vehicle_array != "[]") {
                            val gson = Gson()
                            val listType: Type =
                                object : TypeToken<List<VehicleList?>?>() {}.type
                            val vehicleList: List<VehicleList> =
                                gson.fromJson(vehicle.vehicle_array, listType)
                            RvRideSelectAdapter(vehicleList)
                        }

                    }


                }
            }

        Firebase.firestore.collection(Constant.TRIP)
            .document(MnxPreferenceManager.getString(USER_MAIL, "").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        val vehicle = querySnapshot.toObject(Trip::class.java)!!
                        if (vehicle.trip_array != "[]") {
                            CURRENT_USER_TRIP_ARRAY = JSONArray(vehicle.trip_array)
                        }
                    } else {
                        CURRENT_USER_TRIP_ARRAY = JSONArray()
                    }


                }
            }
    }

    private fun CLickEvents() {
        binding.rltvCarSelect.setOnClickListener {
            if (binding.cardRv.visibility == View.GONE) {
                Visiblecarspinner(binding.cardRv, binding.imgDownArrow, binding.rltvCarSelect)
            } else {
                HideCarspinner(binding.cardRv, binding.imgDownArrow, binding.rltvCarSelect)
            }
        }
        binding.rltvRideSelect.setOnClickListener {
            if (binding.cardRider.visibility == View.GONE) {
                Visiblecarspinner(binding.cardRider, binding.imgDownArrow2, binding.rltvRideSelect)
            } else {
                HideCarspinner(binding.cardRider, binding.imgDownArrow2, binding.rltvRideSelect)
            }
        }


        binding.llSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.llPriceCalculator.setOnClickListener {
            startActivity(Intent(this, PriceCalculatorActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.llGps.setOnClickListener {
            when {
                binding.txtdriver.text.isEmpty() -> {
                    Toast.makeText(this, "Please Select Driver", Toast.LENGTH_SHORT).show()
                }
                binding.txtRider.text.isEmpty() -> {
                    Toast.makeText(this, "Please Select Car", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    startActivity(Intent(this, GpsMapActivity::class.java))
                    overridePendingTransition(0, R.anim.fade)
                }
            }

        }
        binding.llMenual.setOnClickListener {

            when {
                binding.txtdriver.text.isEmpty() -> {
                    Toast.makeText(this, "Please Select Driver", Toast.LENGTH_SHORT).show()
                }
                binding.txtRider.text.isEmpty() -> {
                    Toast.makeText(this, "Please Select Car", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    startActivity(
                        Intent(this, ManualTripActivity::class.java).putExtra(
                            Constant.TOOLBAR_TITLE,
                            getString(R.string.manual_trip)
                        )
                    )
                }
            }

            overridePendingTransition(0, R.anim.fade)
        }
        binding.llTripHistory.setOnClickListener {
            startActivity(Intent(this, TripHistoryActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
    }

    private fun HideCarspinner(
        cardciew: CardView,
        imageView: ImageView,
        relativeLayout: RelativeLayout
    ) {
        imageView.rotation = 0F
        relativeLayout.setBackgroundResource(R.drawable.field_border)
        cardciew.visibility = View.GONE

    }

    private fun Visiblecarspinner(
        cardciew: CardView,
        imageView: ImageView,
        relativeLayout: RelativeLayout
    ) {
        cardciew.visibility = View.VISIBLE
        imageView.rotation = 180F
        relativeLayout.setBackgroundResource(R.drawable.fill_field_border)
    }

    private fun AddDriverSelect() {
        val list: ArrayList<DriverSelectModel> = ArrayList()
        list.add(DriverSelectModel(MnxPreferenceManager.getString(USER_NAME, "").toString(), " "))
        RvDriverSelectAdapter(list)

    }

    @SuppressLint("SetTextI18n")
    private fun RvDriverSelectAdapter(data: ArrayList<DriverSelectModel>) {
        Log.e("data", data.size.toString())
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.dropdown_item_car)
        binding.rvselectdriver.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvselectdriver.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: DropdownItemCarBinding = viewDataBinding as DropdownItemCarBinding
            binding.model = data[position]
            if (position == data.size - 1) {
                binding.line.visibility = View.GONE
            } else {
                binding.line.visibility = View.VISIBLE
            }
            binding.root.setOnClickListener {
                this.binding.cardRv.visibility = View.GONE
                this.binding.txtdriver.text = binding.tvTitle.text
                CURRENT_USER_MAKING_TRIP_OBJ.put(
                    Constant.driver_name,
                    binding.tvTitle.text.toString()
                )
                HideCarspinner(
                    this.binding.cardRv,
                    this.binding.imgDownArrow,
                    this.binding.rltvCarSelect
                )

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun RvRideSelectAdapter(data: List<VehicleList>) {
        Log.e("data", data.size.toString())
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.dropdown_item)
        binding.rvselectrider.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvselectrider.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: DropdownItemBinding = viewDataBinding as DropdownItemBinding
            binding.model = data[position]
            if (position == data.size - 1) {
                binding.line.visibility = View.GONE
            } else {
                binding.line.visibility = View.VISIBLE
            }
            binding.root.setOnClickListener {
                this.binding.txtRider.text = binding.tvTitle.text
                this.binding.txtMilage.text = data[position].vehicle_milage
                CURRENT_USER_MAKING_TRIP_OBJ.put(
                    car_detail,
                    data[position].vehicle_name + " " + data[position].vehicle_number
                )
                CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.trip_type, data[position].trip_type)
                HideCarspinner(
                    this.binding.cardRider,
                    this.binding.imgDownArrow2,
                    this.binding.rltvRideSelect
                )

            }
        }
    }

    fun checkPermissions(): Boolean {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) + ContextCompat
                    .checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    locationrpermission()
                    return false
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ),
                            PERMISSIONS_MULTIPLE_REQUEST
                        )
                        return false
                    }
                }
            } else {
                return true
            }
        } catch (e: Exception) {
            Log.e("Exception", "checkPermissions: " + e.message)
        }
        return false
    }

    private fun locationrpermission() {
        try {
            Constant.callNativeAlert(this,
                null,
                "",
                "This app requries Location permission to show local offer around you.So Kindly accept the location permission to continue",
                false,
                "Request Persmission",
                "",
                " ",
                object : NativeDialogClickListener {
                    override fun onPositive(dialog: DialogInterface?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ),
                                PERMISSIONS_MULTIPLE_REQUEST
                            )
                        }
                    }

                    override fun onNegative(dialog: DialogInterface?) {}
                    override fun onNeutral(dialog: DialogInterface?) {}
                })
        } catch (e: Exception) {
            Log.e("Exception", "locationrpermission: " + e.message)
        }
    }

}
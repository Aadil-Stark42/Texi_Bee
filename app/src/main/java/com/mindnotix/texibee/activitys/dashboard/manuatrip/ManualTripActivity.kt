package com.mindnotix.texibee.activitys.dashboard.manuatrip


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.activitys.dashboard.pricecals.LocationPickerActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.data.model.DriverSelectModel
import com.mindnotix.texibee.data.model.firebasee.Trip
import com.mindnotix.texibee.data.model.firebasee.Vehicle
import com.mindnotix.texibee.data.model.firebasee.VehicleList
import com.mindnotix.texibee.databinding.ActivityManualTripBinding
import com.mindnotix.texibee.databinding.DropdownItemBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.CURRENT_USER_MAKING_TRIP_OBJ
import com.mindnotix.texibee.utils.Constant.Companion.EMPTY
import com.mindnotix.texibee.utils.Constant.Companion.FAILED
import com.mindnotix.texibee.utils.Constant.Companion.PRIVATE
import com.mindnotix.texibee.utils.Constant.Companion.TEXI
import com.mindnotix.texibee.utils.Constant.Companion.TOOLBAR_TITLE
import com.mindnotix.texibee.utils.Constant.Companion.car_detail
import com.mindnotix.texibee.utils.Constant.Companion.driver_name
import com.mindnotix.texibee.utils.Constant.Companion.end_time
import com.mindnotix.texibee.utils.Constant.Companion.start_time
import com.mindnotix.texibee.utils.Constant.Companion.trip_type
import com.mindnotix.texibee.utils.MnxPreferenceManager
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class ManualTripActivity : BaseActivity() {
    lateinit var binding: ActivityManualTripBinding
    private val cal = Calendar.getInstance()
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private val year = cal.get(Calendar.YEAR)
    private val month = cal.get(Calendar.MONTH)
    private val day = cal.get(Calendar.DAY_OF_MONTH)
    private var IsStartLocation = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manual_trip)
        ToolbarSupport(intent.getStringExtra(TOOLBAR_TITLE).toString(), binding.toolbar, this)
        ClickEvents()
        initviews()
        GetCarList()
        TripGroupListner()
        LocationCLick()
    }

    private fun LocationCLick() {
        binding.edtStartLocation.setOnClickListener {
            IsStartLocation = true
            OpenLocationPicker()
        }
        binding.edtDestination.setOnClickListener {
            IsStartLocation = false
            OpenLocationPicker()
        }
    }

    private fun OpenLocationPicker() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)


    }

    private fun TripGroupListner() {
        binding.rdTexi.isChecked = true
        val rg = findViewById<View>(R.id.rdTripGrp) as RadioGroup
        rg.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    R.id.rdTexi -> {
                        CURRENT_USER_MAKING_TRIP_OBJ.put(trip_type, TEXI)
                    }
                    R.id.rdFailed -> {
                        CURRENT_USER_MAKING_TRIP_OBJ.put(trip_type, FAILED)
                    }
                    R.id.rdEmpty -> {
                        CURRENT_USER_MAKING_TRIP_OBJ.put(trip_type, EMPTY)
                    }
                    R.id.rdPrivate -> {
                        CURRENT_USER_MAKING_TRIP_OBJ.put(trip_type, PRIVATE)

                    }
                }
            }
        })
    }

    private fun initviews() {
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                getString(R.string.google_maps_key),
                Locale.US
            )
        }
        binding.txtDriverName.text = CURRENT_USER_MAKING_TRIP_OBJ.getString(driver_name)
        binding.txtCarDetails.text = CURRENT_USER_MAKING_TRIP_OBJ.getString(car_detail)
    }

    private fun GetCarList() {


        Firebase.firestore.collection(Constant.VEHICLE)
            .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
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
                            RvCarDetailsAdapter(vehicleList)
                        }

                    }


                }
            }


    }

    private fun AddDriverSelect() {
        val list: ArrayList<DriverSelectModel> = ArrayList()
        list.add(DriverSelectModel("WOK RM 8426", "Mercedes-Benz W212 E-class"))
        list.add(DriverSelectModel("B AB 123", "Mercedes-Benz W212 E-class"))
        /*    RvCarDetailsAdapter(list)*/
    }

    private fun ClickEvents() {
        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
        binding.btnSave.setOnClickListener {

            when {
                binding.edtDatepicker.text.isEmpty() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.SelectDate),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.edtStartTime.text.isEmpty() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.SelectStartTime),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.edtEndTime.text.isEmpty() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.SelectEndTime),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.edtFare.text.isEmpty() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.EnterFare),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    CURRENT_USER_MAKING_TRIP_OBJ.put(
                        Constant.date,
                        binding.edtDatepicker.text.toString()
                    )
                    CURRENT_USER_MAKING_TRIP_OBJ.put(
                        Constant.start_time,
                        binding.edtStartTime.text.toString()
                    )
                    CURRENT_USER_MAKING_TRIP_OBJ.put(
                        Constant.end_time,
                        binding.edtEndTime.text.toString()
                    )
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.trip_rate, binding.edtFare.text.toString())
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.payment_method, "Cash")
                    AddTotalTIme(
                        CURRENT_USER_MAKING_TRIP_OBJ.getString(start_time),
                        CURRENT_USER_MAKING_TRIP_OBJ.getString(
                            end_time
                        )
                    )

                    AddTrip()
                }
            }

        }

        binding.edtStartLocation.setOnClickListener {
            startActivity(Intent(this, LocationPickerActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.edtDestination.setOnClickListener {
            startActivity(Intent(this, LocationPickerActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.edtDatepicker.setOnClickListener {

            val dpd = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "dd/MM/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    binding.edtDatepicker.setText(sdf.format(cal.time))
                },
                year,
                month,
                day
            )

            dpd.show()
        }

        binding.rltvCarSelect.setOnClickListener {
            if (binding.cardRv.visibility == View.GONE) {
                Visiblecarspinner(binding.cardRv, binding.imgDownArrow, binding.rltvCarSelect)
            } else {
                HideCarspinner(binding.cardRv, binding.imgDownArrow, binding.rltvCarSelect)
            }
        }
        binding.edtStartTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                binding.edtStartTime.setText(SimpleDateFormat("HH:mmaa").format(cal.time))
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }


        binding.edtEndTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.edtEndTime.setText(SimpleDateFormat("HH:mmaa").format(cal.time))
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    @SuppressLint("LongLogTag")
    private fun AddTrip() {
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.ID, UUID.randomUUID().toString())
        Log.e("CURRENT_USER_MAKING_TRIP_OBJ", CURRENT_USER_MAKING_TRIP_OBJ.toString())
        Constant.CURRENT_USER_TRIP_ARRAY.put(CURRENT_USER_MAKING_TRIP_OBJ)
        val trip = Trip(trip_array = Constant.CURRENT_USER_TRIP_ARRAY.toString())
        Firebase.firestore.collection(Constant.TRIP)
            .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
            .set(trip)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    getString(R.string.TripSaved),
                    Toast.LENGTH_SHORT
                ).show()
                CURRENT_USER_MAKING_TRIP_OBJ = JSONObject()

            }
            .addOnFailureListener { e -> // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(this, "Fail to add addPrincing \n$e", Toast.LENGTH_SHORT)
                    .show()
            }

        finish()
    }

    private fun AddTotalTIme(start_time: String, end_time: String) {
        Log.e("start_time",start_time)
        Log.e("end_time",end_time)
        val simpleDateFormat = SimpleDateFormat("HH:mmaa")
        val startDate = simpleDateFormat.parse(start_time)
        val endDate = simpleDateFormat.parse(end_time)

        var difference = endDate.time - startDate.time
        if (difference < 0) {
            val dateMax = simpleDateFormat.parse("24:00:00")
            val dateMin = simpleDateFormat.parse("00:00:00")
            difference = dateMax.time - startDate.time + (endDate.time - dateMin.time)
        }
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()

        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
        val sec =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min).toInt() / 1000



        CURRENT_USER_MAKING_TRIP_OBJ.put(
            Constant.travel_minit,
            "$hours hr, $min mint, $sec sec"
        )
        /*   if (hours != 0) {

        } else {
            CURRENT_USER_MAKING_TRIP_OBJ.put(
                Constant.travel_minit,
                "$min mint, $sec sec"
            )
        }*/
        Log.i("log_tag", "Hours: $hours, Mins: $min, Secs: $sec")
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

    @SuppressLint("SetTextI18n")
    private fun RvCarDetailsAdapter(data: List<VehicleList>) {
        Log.e("data", data.size.toString())
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.dropdown_item)
        binding.RvCarDetails.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvCarDetails.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: DropdownItemBinding = viewDataBinding as DropdownItemBinding
            binding.model = data[position]
            if (position == data.size - 1) {
                binding.line.visibility = View.GONE
            } else {
                binding.line.visibility = View.VISIBLE
            }
            binding.root.setOnClickListener {
                this.binding.txtCarDetails.text = binding.tvTitle.text
                Constant.CURRENT_USER_MAKING_TRIP_OBJ.put(
                    Constant.car_detail,
                    data[position].vehicle_name + " " + data[position].vehicle_number
                )

                HideCarspinner(
                    this.binding.cardRv,
                    this.binding.imgDownArrow,
                    this.binding.rltvCarSelect
                )

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("TAG", "Place: ${place.name}, ${place.id}")
                        GetLatLong(place.name)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun GetLatLong(name: String?) {

        val coder = Geocoder(this)
        try {
            val adresses: ArrayList<Address> =
                coder.getFromLocationName(name, 1) as ArrayList<Address>

            if(adresses.isNotEmpty())
            {

                val longitude: Double = adresses[0].longitude
                val latitude: Double = adresses[0].latitude
                Log.e("latitude", latitude.toString())
                Log.e("longitude", longitude.toString())
                if (IsStartLocation) {
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_location, name)
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_latitude, latitude)
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_longitude, longitude)
                    binding.edtStartLocation.setText(name)
                } else {
                    binding.edtDestination.setText(name)
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.end_latitude, latitude)
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.end_longitude, longitude)
                    CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.destination_location, name)
                }
                CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_km, "0 Km")

                if (
                    CURRENT_USER_MAKING_TRIP_OBJ.has(Constant.start_latitude) &&
                    CURRENT_USER_MAKING_TRIP_OBJ.has(Constant.start_longitude) &&
                    CURRENT_USER_MAKING_TRIP_OBJ.has(Constant.end_latitude) &&
                    CURRENT_USER_MAKING_TRIP_OBJ.has(Constant.end_longitude)
                ) {

                    if (
                        CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_latitude).isNotEmpty() &&
                        CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_longitude).isNotEmpty() &&
                        CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.end_latitude).isNotEmpty() &&
                        CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.end_longitude).isNotEmpty()
                    ) {
                        Distance(
                            CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_latitude).toDouble(),
                            CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_longitude)
                                .toDouble(),
                            CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.end_latitude).toDouble(),
                            CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.end_longitude).toDouble()
                        )
                    }

                }

            }else{
                Toast.makeText(
                    this,
                    getString(R.string.SelectValidAddress),
                    Toast.LENGTH_SHORT
                ).show()
            }



        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("LongLogTag", "SetTextI18n")
    private fun Distance(
        start_lat: Double,
        start_long: Double,
        end_lat: Double,
        end_long: Double
    ) {
        var distance: Double
        val locationA =
            Location(CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_location))
        locationA.latitude = start_lat
        locationA.longitude = start_long

        val locationB =
            Location(CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.destination_location))
        locationB.latitude = end_lat
        locationB.longitude = end_long

        distance = (locationA.distanceTo(locationB) / 1000).toDouble()
        Log.e("distancedistancedistance", distance.toString())
        val nf: NumberFormat = DecimalFormat("##.##")

        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.destination_km, nf.format(distance) + " Km")
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.traveled_km, nf.format(distance) + " Km")
        var total =
            distance * MnxPreferenceManager.getString(Constant.PER_KM_CHARGE, "0.0")!!.toDouble()
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.trip_rate, nf.format(total) + " CHF")
        binding.edtDestinationKm.setText(nf.format(distance) + " Km")
        binding.edtTraveledKm.setText(nf.format(distance) + " Km")

    }
}
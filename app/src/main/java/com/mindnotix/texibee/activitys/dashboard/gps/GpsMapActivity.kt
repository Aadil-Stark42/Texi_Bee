package com.mindnotix.texibee.activitys.dashboard.gps


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
 import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.callback.AlertDialogClick
import com.mindnotix.texibee.databinding.ActivityGpsMapBinding
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.CURRENT_USER_MAKING_TRIP_OBJ
import com.mindnotix.texibee.utils.Constant.Companion.PER_KM_CHARGE
import com.mindnotix.texibee.utils.Constant.Companion.destination_location
import com.mindnotix.texibee.utils.Constant.Companion.end_latitude
import com.mindnotix.texibee.utils.Constant.Companion.end_longitude
import com.mindnotix.texibee.utils.Constant.Companion.start_latitude
import com.mindnotix.texibee.utils.Constant.Companion.start_location
import com.mindnotix.texibee.utils.Constant.Companion.start_longitude
import com.mindnotix.texibee.utils.Constant.Companion.traveled_km
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.mindnotix.texibee.utils.NativeDialogClickListener
import com.mindnotix.texibee.views.alertdialog.AlertDialog
import com.mindnotix.texibee.views.drawroutemap.DrawMarker
import com.mindnotix.texibee.views.drawroutemap.DrawRouteMaps
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class GpsMapActivity : BaseActivity(), OnMapReadyCallback, AlertDialogClick {
    private lateinit var googleMap: GoogleMap
    var PERMISSIONS_MULTIPLE_REQUEST = 45
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var fusedLocationClient: FusedLocationProviderClient? = null
    lateinit var binding: ActivityGpsMapBinding
    var handler: Handler = Handler()
    private var runnable: Runnable? = null

    var TrpTimeCounting = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gps_map)
        CLickEvents()
        GetMap()
    }

    @SuppressLint("LongLogTag")
    private fun CLickEvents() {
        binding.imghide.setOnClickListener {
            if (binding.llCalculation.visibility == View.GONE) {
                binding.llCalculation.visibility = View.VISIBLE
            } else {
                binding.llCalculation.visibility = View.GONE
            }
        }
        binding.imgback.setOnClickListener {
            finish()
        }

        binding.edtDestination.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)


            /*    startActivity(Intent(this, LocationPickerActivity::class.java))
                overridePendingTransition(0, R.anim.fade)*/

        }
        binding.btmStartNavigation.setOnClickListener {
            if (binding.edtDestination.text.isNotEmpty()) {
                GrtStartTime()
                ActionView()
                AddTextvalue()
                DrawLine()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.EnterDestination),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.btnCancel.setOnClickListener {
            AlertDialog(
                this,
                getString(R.string.cancel_trip_),
                getString(R.string.are_you_sure_trip_cancel),
                getString(R.string.nogoback),
                getString(R.string.cancel_trip),
                this,
                R.drawable.ic_warning
            )
        }
        binding.btnFinish.setOnClickListener {
            EndTime()
            Log.e("TrpTimeCounting", TrpTimeCounting.toString())
            startActivity(Intent(this, BookSummuryActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
            finish()
        }

        binding.imgFocusLocation.setOnClickListener {
            if (!checkPermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    locationrpermission()
                }
            } else {
                getLastLocation(true)
            }
        }
    }

    private fun DrawLine() {
        googleMap.clear()
        val origin = LatLng(CURRENT_USER_MAKING_TRIP_OBJ.getString(start_latitude).toDouble(), CURRENT_USER_MAKING_TRIP_OBJ.getString(start_longitude).toDouble())
        val destination = LatLng(CURRENT_USER_MAKING_TRIP_OBJ.getString(end_latitude).toDouble(), CURRENT_USER_MAKING_TRIP_OBJ.getString(end_longitude).toDouble())
         DrawRouteMaps.getInstance(this)
            .draw(origin, destination, googleMap)
        DrawMarker.getInstance(this).draw(googleMap, origin,
            R.drawable.ic_marker, CURRENT_USER_MAKING_TRIP_OBJ.getString(start_location))
        DrawMarker.getInstance(this)
            .draw(googleMap, destination, R.drawable.ic_marker, CURRENT_USER_MAKING_TRIP_OBJ.getString(destination_location))

        val bounds = LatLngBounds.Builder()
            .include(origin)
            .include(destination).build()
        val displaySize = Point()
        windowManager.defaultDisplay.getSize(displaySize)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30))
    }

    private fun AddTextvalue() {
        binding.txtKm.text = CURRENT_USER_MAKING_TRIP_OBJ.getString(traveled_km).replace(" ", "").lowercase()
        binding.txtAddress.text = CURRENT_USER_MAKING_TRIP_OBJ.getString(destination_location)
    }


    private fun GrtStartTime() {
        val sdf = SimpleDateFormat("hh:mmaa")
        val currentTime = sdf.format(Date())
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_time, currentTime.toString())
        handler.postDelayed(Runnable {
            TrpTimeCounting += 1
            Log.e("TrpTimeCounting", TrpTimeCounting.toString())
            handler.postDelayed(runnable!!, 1000)

        }
            .also { runnable = it }, 1000)

    }

    private fun EndTime() {
        handler.removeCallbacksAndMessages(null)
        val sdf = SimpleDateFormat("hh:mmaa")
        val endTime = sdf.format(Date())
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.end_time, endTime.toString())
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.payment_method, "Cash")
        AddTotalTime(TrpTimeCounting)
    }

    private fun AddTotalTime(totalSeconds: Int) {
        val MINUTES_IN_AN_HOUR = 60
        val SECONDS_IN_A_MINUTE = 60
        val seconds = totalSeconds % SECONDS_IN_A_MINUTE
        val totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE
        val minutes = totalMinutes % MINUTES_IN_AN_HOUR
        val hours = totalMinutes / MINUTES_IN_AN_HOUR

        CURRENT_USER_MAKING_TRIP_OBJ.put(
            Constant.travel_minit,
            "$hours hr, $minutes mint, $seconds sec"
        )
    /*     if (hours != 0) {

        } else {
            CURRENT_USER_MAKING_TRIP_OBJ.put(
                Constant.travel_minit,
                "$minutes mint, $seconds sec"
            )
        }
*/

    }

    private fun ActionView() {
        binding.llGpsStart.visibility = View.GONE
        binding.Rltvkm.visibility = View.GONE
        binding.imgback.visibility = View.GONE

        binding.llAction.visibility = View.VISIBLE
        binding.RltvDirection.visibility = View.VISIBLE
    }

    private fun GetMap() {
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                getString(R.string.google_maps_key),
                Locale.US
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(IsFocus: Boolean) {
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                task.result
                Log.e("task.result.latitude", task.result.latitude.toString())
                Log.e("task.result.longitude", task.result.longitude.toString())
                GetStartLocationName(task.result.latitude, task.result.longitude)
                FocusOnCurrentLocation(task.result.latitude, task.result.longitude, IsFocus)


            }

        }
    }

    private fun GetStartLocationName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_location, address)
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_latitude, latitude)
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_longitude, longitude)
        /*      val city = addresses[0].locality
              val state = addresses[0].adminArea
              val country = addresses[0].countryName
              val postalCode = addresses[0].postalCode
              val knownName = addresses[0].featureName // Only if available else return NULL
      */

    }

    private fun FocusOnCurrentLocation(latitude: Double, longitude: Double, IsFocus: Boolean) {

        val currentlocation = LatLng(latitude, longitude)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.clear()
        this.googleMap.addMarker(
            MarkerOptions().position(currentlocation).icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_YELLOW
                )
            )
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 17f))

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
 /*       googleMap.isMyLocationEnabled = true*/
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                locationrpermission()
            }
        } else {
            getLastLocation(true)
        }


/*
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
*/

    }

    override fun OnCancelCLick() {
    }

    override fun OnActionClick() {

        finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("TAG", "Place: ${place.name}, ${place.id}")
                        GetDestinationLatLong(place.name)
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

    private fun GetDestinationLatLong(name: String?) {

        val coder = Geocoder(this)
        try {
            val adresses: ArrayList<Address> =
                coder.getFromLocationName(name, 1) as ArrayList<Address>
            if(adresses.isNotEmpty())
            {
                binding.edtDestination.setText(name)
                CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.destination_location, name)
                CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.start_km, "0 Km")
                binding.btmStartNavigation.setTextColor(Color.parseColor("#FF000000"))
                binding.btmStartNavigation.background.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.view_main_color
                    ), PorterDuff.Mode.MULTIPLY
                )
                val longitude: Double = adresses[0].longitude
                val latitude: Double = adresses[0].latitude
                Log.e("latitude", latitude.toString())
                Log.e("longitude", longitude.toString())
                CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.end_latitude, latitude)
                CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.end_longitude, longitude)
                Distance(
                    CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_latitude).toDouble(),
                    CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_longitude)
                        .toDouble(),
                    CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.end_latitude).toDouble(),
                    CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.end_longitude).toDouble()
                )

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


    @SuppressLint("LongLogTag")
    private fun Distance(
        start_lat: Double,
        start_long: Double,
        end_lat: Double,
        end_long: Double
    ) {
        var distance: Double
        val locationA =
            Location(Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.start_location))
        locationA.latitude = start_lat
        locationA.longitude = start_long

        val locationB =
            Location(Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(Constant.destination_location))
        locationB.latitude = end_lat
        locationB.longitude = end_long

        distance = (locationA.distanceTo(locationB) / 1000).toDouble()
        Log.e("distancedistancedistance", distance.toString())
        val nf: NumberFormat = DecimalFormat("##.##")

        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.destination_km, nf.format(distance) + " Km")
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.traveled_km, nf.format(distance) + " Km")
        var total = distance * MnxPreferenceManager.getString(PER_KM_CHARGE, "0.0")!!.toDouble()
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.trip_rate, nf.format(total) + " CHF")
        getCurrentDate()

    }

    private fun getCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(Date())
        CURRENT_USER_MAKING_TRIP_OBJ.put(Constant.date, currentDate.toString())

    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}
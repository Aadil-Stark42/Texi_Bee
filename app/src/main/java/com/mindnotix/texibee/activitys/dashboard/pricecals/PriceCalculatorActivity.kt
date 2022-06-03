package com.mindnotix.texibee.activitys.dashboard.pricecals

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.databinding.ActivityPriceCalculatorBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.MnxPreferenceManager
import java.text.DecimalFormat
import java.text.NumberFormat

class PriceCalculatorActivity : BaseActivity() {
    lateinit var binding: ActivityPriceCalculatorBinding
    private var IsStartLocation = true
    private var IsAutoCalculation = false
    private var Started_latitude = 0.0
    private var Started_longitude = 0.0
    private var Destination_latitude = 0.0
    private var Destination_longitude = 0.0

    private var Started_address = ""
    private var Destination_address = ""

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_price_calculator)
        ToolbarSupport(getString(R.string.price_calculator), binding.toolbar, this)
        ClickEvents()
    }

    private fun ClickEvents() {
        binding.btnCalculate.setOnClickListener {
            if (IsAutoCalculation) {
                if (Started_address.isNotEmpty() && Destination_address.isNotEmpty()) {
                    val PerKmCost :Double = MnxPreferenceManager.getString(Constant.PER_KM_CHARGE, "").toDouble()
                    val distance = Distance(
                        Started_latitude,
                        Started_longitude,
                        Destination_latitude,
                        Destination_longitude
                    )
                    val total = PerKmCost*distance
                    Log.e("totaltotaltotaltotal", total.toString())
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.Selectstartordestinationlocation),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        }
        binding.edtStartLocation.setOnClickListener {
            IsStartLocation = true
            OpenLocationPicker()
        }
        binding.edtDestination.setOnClickListener {
            IsStartLocation = false
            OpenLocationPicker()
        }
        binding.btnCalculate.setOnClickListener {
            startActivity(Intent(this, MapPreviewActivity::class.java))
            overridePendingTransition(0, R.anim.fade)
        }
        binding.llAutometicCalc.setOnClickListener {
            binding.rdtAuto.isChecked = true
            binding.rdtManual.isChecked = false
        }
        binding.llManualCalc.setOnClickListener {
            binding.rdtAuto.isChecked = false
            binding.rdtManual.isChecked = true
        }
    }

    private fun OpenLocationPicker() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    Log.i("TAG", "Place: ${place.name}, ${place.id}")
                    if (IsStartLocation) {
                        Started_latitude = place.latLng.latitude
                        Started_longitude = place.latLng.longitude
                        Started_address = place.name
                        binding.edtStartLocation.setText(place.name)
                    } else {
                        Destination_latitude = place.latLng.latitude
                        Destination_longitude = place.latLng.longitude
                        Destination_address = place.name
                        binding.edtDestination.setText(place.name)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status = Autocomplete.getStatusFromIntent(data)
                    Log.i("TAG", status.statusMessage ?: "")

                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("LongLogTag", "SetTextI18n")
    private fun Distance(
        start_lat: Double,
        start_long: Double,
        end_lat: Double,
        end_long: Double
    ): Double {
        val distance: Double
        val locationA =
            Location(Started_address)
        locationA.latitude = start_lat
        locationA.longitude = start_long

        val locationB =
            Location(Destination_address)
        locationB.latitude = end_lat
        locationB.longitude = end_long

        distance = (locationA.distanceTo(locationB) / 1000).toDouble()
        val nf: NumberFormat = DecimalFormat("##.##")
        //nf.format(distance)
        Log.e("distancedistancedistance", nf.format(distance).toString())
        return distance
    }
}
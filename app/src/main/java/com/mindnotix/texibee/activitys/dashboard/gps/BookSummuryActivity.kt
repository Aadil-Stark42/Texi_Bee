package com.mindnotix.texibee.activitys.dashboard.gps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.dashboard.MainDashBoardActivity
import com.mindnotix.texibee.callback.AlertDialogClick
import com.mindnotix.texibee.data.model.firebasee.Trip
import com.mindnotix.texibee.databinding.ActivityBookSummuryBinding
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.CURRENT_USER_MAKING_TRIP_OBJ
import com.mindnotix.texibee.utils.Constant.Companion.CURRENT_USER_TRIP_ARRAY
import com.mindnotix.texibee.utils.Constant.Companion.ID
import com.mindnotix.texibee.utils.Constant.Companion.car_detail
import com.mindnotix.texibee.utils.Constant.Companion.date
import com.mindnotix.texibee.utils.Constant.Companion.destination_km
import com.mindnotix.texibee.utils.Constant.Companion.destination_location
import com.mindnotix.texibee.utils.Constant.Companion.driver_name
import com.mindnotix.texibee.utils.Constant.Companion.end_time
import com.mindnotix.texibee.utils.Constant.Companion.start_km
import com.mindnotix.texibee.utils.Constant.Companion.start_location
import com.mindnotix.texibee.utils.Constant.Companion.start_time
import com.mindnotix.texibee.utils.Constant.Companion.travel_minit
import com.mindnotix.texibee.utils.Constant.Companion.traveled_km
import com.mindnotix.texibee.utils.Constant.Companion.trip_rate
import com.mindnotix.texibee.utils.Constant.Companion.trip_type
import com.mindnotix.texibee.utils.MnxPreferenceManager
import com.mindnotix.texibee.views.alertdialog.AlertDialog
import org.json.JSONObject
import java.util.*

class BookSummuryActivity : AppCompatActivity(), AlertDialogClick {
    lateinit var binding: ActivityBookSummuryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_summury)
        ClickEvents()
        AddSummaryData()
    }

    private fun AddSummaryData() {
        binding.txtDriverName.text = Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(driver_name)
        binding.txtCarDetails.text = Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(car_detail)
        binding.txtTypeofTrip.text = Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(trip_type)
        binding.txtStartLocation.text =
            Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(start_location)
        binding.txtDestination.text =
            Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(destination_location)
        binding.txtStartKm.text = Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(start_km)
        binding.txtDestinationKm.text =
            Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(destination_km)
        binding.txtTravelKm.text = Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(traveled_km)
        binding.txtDateTime.text =
            Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(date) + ", " + Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(
                start_time
            ) + "-" + Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(end_time)
        binding.txtTravelTime.text = Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(travel_minit)
        binding.txtCHF.text = Constant.CURRENT_USER_MAKING_TRIP_OBJ.getString(trip_rate)

    }

    @SuppressLint("LongLogTag")
    private fun ClickEvents() {
        binding.btnDontsave.setOnClickListener {
            AlertDialog(
                this,
                getString(R.string.dont_save_trip),
                getString(R.string.are_you_sure_dont_save_trip),
                getString(R.string.GoBack),
                getString(R.string.dont_save),
                this,
                R.drawable.ic_warning
            )
        }

        binding.btnSave.setOnClickListener {

            CURRENT_USER_MAKING_TRIP_OBJ.put(ID, UUID.randomUUID().toString())
            Log.e("CURRENT_USER_MAKING_TRIP_OBJ", CURRENT_USER_MAKING_TRIP_OBJ.toString())
            CURRENT_USER_TRIP_ARRAY.put(CURRENT_USER_MAKING_TRIP_OBJ)
            val trip = Trip(trip_array = CURRENT_USER_TRIP_ARRAY.toString())
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
                    startActivity(
                        Intent(this, MainDashBoardActivity::class.java)
                    )
                    finish()
                }
                .addOnFailureListener { e -> // this method is called when the data addition process is failed.
                    // displaying a toast message when data addition is failed.
                    Toast.makeText(this, "Fail to add addPrincing \n$e", Toast.LENGTH_SHORT)
                        .show()
                }
        }

    }

    override fun OnCancelCLick() {

    }

    override fun OnActionClick() {
        startActivity(Intent(this, MainDashBoardActivity::class.java))
        finish()
    }

}
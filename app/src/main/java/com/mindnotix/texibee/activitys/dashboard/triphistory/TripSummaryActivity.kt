package com.mindnotix.texibee.activitys.dashboard.triphistory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.activitys.dashboard.manuatrip.ManualTripActivity
import com.mindnotix.texibee.data.model.firebasee.TripListModel
import com.mindnotix.texibee.databinding.ActivityTripSummaryBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant


class TripSummaryActivity : BaseActivity() {
    lateinit var binding: ActivityTripSummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_summary)
        ToolbarSupport(getString(R.string.trip_summary), binding.toolbar, this)
        binding.toolbar.llmenu.visibility = View.VISIBLE
        CLickEvents()
        AddSummary()
    }

    @SuppressLint("SetTextI18n")
    private fun AddSummary() {
        val gson = Gson()
        val trip: TripListModel =
            gson.fromJson(intent.getStringExtra("singletrip"), TripListModel::class.java)
        binding.txtDriverName.text = trip.driverName
        binding.txtCarDetails.text = trip.carDetail
        binding.txtTypeofTrip.text = trip.tripType
        binding.txtStartLocation.text = trip.startLocation
        binding.txtDestination.text = trip.destinationLocation
        binding.txtStartKm.text = trip.startKm
        binding.txtDestinationKm.text = trip.destinationKm
        binding.txtTravelKm.text = trip.traveledKm
        binding.txtDateTime.text = trip.date + ", " + trip.startTime + "-" + trip.endTime
        binding.txtTravelTime.text = trip.travelMinit
        binding.txtCHF.text = trip.tripRate

    }

    private fun CLickEvents() {
        binding.toolbar.icMenu.setOnClickListener {
            binding.rltvActionMenu.visibility = View.VISIBLE
        }
        binding.scrollView.setOnClickListener {
            binding.rltvActionMenu.visibility = View.GONE
        }

        binding.actionmenu.llEdit.setOnClickListener {
            binding.rltvActionMenu.visibility = View.GONE
            startActivity(
                Intent(
                    this,
                    ManualTripActivity::class.java
                ).putExtra(Constant.TOOLBAR_TITLE, getString(R.string.edit_trip_summary))
            )
            overridePendingTransition(0, R.anim.fade)
        }


    }
}
package com.mindnotix.texibee.activitys.dashboard.triphistory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.data.model.firebasee.Trip
import com.mindnotix.texibee.data.model.firebasee.TripListModel
import com.mindnotix.texibee.databinding.ActivityTripHistoryBinding
import com.mindnotix.texibee.databinding.TripHistoryListItemBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.Constant
import com.mindnotix.texibee.utils.Constant.Companion.PRIVATE
import com.mindnotix.texibee.utils.MnxPreferenceManager
import java.lang.reflect.Type

class TripHistoryActivity : BaseActivity() {
    lateinit var binding: ActivityTripHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_history)
        ToolbarSupport(getString(R.string.trip_history), binding.toolbar, this)
        FetchingTrip()
    }

    private fun FetchingTrip() {
        Firebase.firestore.collection(Constant.TRIP)
            .document(MnxPreferenceManager.getString(Constant.USER_MAIL, "").toString())
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    if (querySnapshot.exists()) {
                        val trip = querySnapshot.toObject(Trip::class.java)!!
                        if (trip.trip_array != "[]") {
                            val gson = Gson()
                            val listType: Type =
                                object : TypeToken<List<TripListModel?>?>() {}.type
                            val tripList: List<TripListModel> =
                                gson.fromJson(trip.trip_array, listType)

                            RvTripHistoryAdapter(tripList)
                        } else {
                            binding.txtEmptyTrip.visibility = View.VISIBLE
                        }
                    } else {
                        binding.txtEmptyTrip.visibility = View.VISIBLE
                    }


                }
            }
    }


    @SuppressLint("SetTextI18n")
    private fun RvTripHistoryAdapter(data: List<TripListModel>) {
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.trip_history_list_item)
        binding.RvLocationList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvLocationList.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: TripHistoryListItemBinding = viewDataBinding as TripHistoryListItemBinding

            when (data[position].tripType) {
                PRIVATE -> {
                    binding.txtTripStatus.text = "Private"
                    binding.txtTripStatus.setBackgroundResource(R.drawable.trip_history_status_red_button)
                    binding.txtTripStatus.setTextColor(resources.getColor(R.color.view_red_color))
                }
                Constant.EMPTY -> {
                    binding.txtTripStatus.text = "Free"
                    binding.txtTripStatus.setBackgroundResource(R.drawable.trip_history_status_green_button)
                    binding.txtTripStatus.setTextColor(resources.getColor(R.color.green))
                }
                Constant.FAILED -> {
                    binding.txtTripStatus.text = "Failed"
                    binding.txtTripStatus.setBackgroundResource(R.drawable.trip_history_status_red_button)
                    binding.txtTripStatus.setTextColor(resources.getColor(R.color.view_red_color))
                }
                Constant.TEXI -> {
                    binding.txtTripStatus.text = "Taxi"
                    binding.txtTripStatus.setBackgroundResource(R.drawable.trip_history_status_blue_button)
                    binding.txtTripStatus.setTextColor(resources.getColor(R.color.blue))
                }
            }
            binding.txtTotalKm.text = data[position].traveledKm
            binding.txtDate.text = data[position].date

            binding.txtStartLocation.text = data[position].startLocation
            binding.txtDestinationLocation.text = data[position].destinationLocation


            val strs = data[position].travelMinit!!.split(",").toTypedArray()
            if (!strs[0].contains("0")) {
                binding.txtTotalMinitus.text = strs[0]
            } else if (!strs[1].contains("0")) {
                binding.txtTotalMinitus.text = strs[1]
            } else {
                binding.txtTotalMinitus.text = strs[2]
            }

            binding.Cardclick.setOnClickListener {
                val gson = Gson()
                val singletrip = gson.toJson(data[position])

                startActivity(
                    Intent(this, TripSummaryActivity::class.java).putExtra(
                        "singletrip",
                        singletrip
                    )
                )
                overridePendingTransition(0, R.anim.fade)

            }
        }
    }


}
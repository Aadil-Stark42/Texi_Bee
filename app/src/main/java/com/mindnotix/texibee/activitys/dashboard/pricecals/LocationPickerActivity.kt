package com.mindnotix.texibee.activitys.dashboard.pricecals

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.adapters.MyMasterRecyclerAdapter
import com.mindnotix.texibee.databinding.ActivityLocationPickerBinding
import com.mindnotix.texibee.databinding.LocationListItemBinding
import java.util.*





class LocationPickerActivity : BaseActivity() {
    lateinit var binding: ActivityLocationPickerBinding
    lateinit var addressList: List<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_picker)
        AddData()
        ClickEvents()
        SearchLocation()

    }

    private fun SearchLocation() {

        binding.edtSearchLocation.onDone { GetPLacesList(binding.edtSearchLocation.text.toString())
        }
    }
    fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
                true
            }
            false
        }
    }
    private fun GetPLacesList(search: String) {
        if (search.isNotEmpty()) {
            val geocoder = Geocoder(this)
            addressList = geocoder.getFromLocationName(search, 20)
            addressList!!.forEach {
                Log.e("addressList", it.getAddressLine(0))
            }
        }

    }

    private fun ClickEvents() {
        binding.imgback.setOnClickListener {
            onBackPressed()
        }
    }

    private fun AddData() {
        val list: ArrayList<String> = ArrayList()
        list.add("Birkweg")
        list.add("Birur")
        list.add("Birmingham")
        list.add("Birkweg")
        list.add("Birur")
        list.add("Birmingham")
        list.add("Birkweg")
        list.add("Birur")
        list.add("Birmingham")
        RvLocationAdapter(list)
    }


    @SuppressLint("SetTextI18n")
    private fun RvLocationAdapter(data: ArrayList<String>) {
        val recyclerAdapter = MyMasterRecyclerAdapter(data, R.layout.location_list_item)
        binding.RvLocationList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.RvLocationList.adapter = recyclerAdapter
        recyclerAdapter.setOnBind { viewDataBinding, position ->
            val binding: LocationListItemBinding = viewDataBinding as LocationListItemBinding

            if (position == data.size - 1) {
                binding.line.visibility = View.GONE
            } else {
                binding.line.visibility = View.VISIBLE
            }
            binding.root.setOnClickListener { onBackPressed() }

        }
    }

}
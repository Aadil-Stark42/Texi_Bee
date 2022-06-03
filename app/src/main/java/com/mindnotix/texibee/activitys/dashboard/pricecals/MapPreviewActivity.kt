package com.mindnotix.texibee.activitys.dashboard.pricecals

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity
import com.mindnotix.texibee.databinding.ActivityMapPreviewBinding
import com.mindnotix.texibee.support.ToolbarSupport

class MapPreviewActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    lateinit var binding: ActivityMapPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map_preview)
        ToolbarSupport(getString(R.string.price_calculator), binding.toolbar, this)
        initviews()
        GetMap()
    }

    private fun GetMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun initviews() {
        binding.cardlocationpreview.setBackgroundResource(R.drawable.locationview_bg)
//        EstimatePriceFragment().show(supportFragmentManager, "TAG")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

    }
}
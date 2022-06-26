package com.fisecode.absentapp.views.absentspot


import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityAbsentSpotBinding
import com.fisecode.absentapp.databinding.BottomSheetAbsentSpotBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AbsentSpotActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAbsentSpotBinding
    private lateinit var bindingBottomSheet: BottomSheetAbsentSpotBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var mapAbsentSpot: SupportMapFragment? = null
    private var map: GoogleMap? = null
    private var currentLocation: Location? = null
    private var spotList: ArrayList<String> = ArrayList()
    private lateinit var locationCallback: LocationCallback
    var item = ""
    var latitude = -6.284665797423048
    var longitude = 106.75063133077147
    var latLang = LatLng(latitude, longitude)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsentSpotBinding.inflate(layoutInflater)
        bindingBottomSheet = binding.layoutBottomSheet
        setContentView(binding.root)

        setupMaps()
        init()
        onClick()
    }

    private fun setupMaps() {
        mapAbsentSpot = supportFragmentManager.findFragmentById(R.id.map_absent_spot) as? SupportMapFragment
        mapAbsentSpot?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.addMarker(
            MarkerOptions()
                .position(latLang)
                .title("Marker in Sydney")
        )
        map?.moveCamera(CameraUpdateFactory.newLatLng(latLang))
        map?.animateCamera(CameraUpdateFactory.zoomTo(20f))
    }

    private fun onClick() {
        binding.tbAbsentSpot.setNavigationOnClickListener {
            finish()
        }
    }

    private fun init() {
        //Top Navigation
        setSupportActionBar(binding.tbAbsentSpot)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Setup BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bindingBottomSheet.bottomSheetAbsentSpot)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        spotList = ArrayList()
        spotList.add("Office")
        spotList.add("Home")

        val spinner = bindingBottomSheet.autoCompleteTextView
        val adapter = ArrayAdapter(this, R.layout.dropdown_leave_type, spotList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
        spinner.setText(adapter.getItem(0).toString(),false)
        bindingBottomSheet.tvCurrentLocation.text = spinner.text
        spinner.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    val latLang = LatLng(latitude, longitude)
                    map?.moveCamera(CameraUpdateFactory.newLatLng(latLang))
                    map?.animateCamera(CameraUpdateFactory.zoomTo(20F))
                    item = p0?.getItemAtPosition(p2).toString()
                    bindingBottomSheet.tvCurrentLocation.text = item
                } else {
                    val latitude = -6.289068599951689
                    val longitude = 106.75762827796194
                    val newLatLng = LatLng(latitude, longitude)
                    map?.moveCamera(CameraUpdateFactory.newLatLng(newLatLng))
                    map?.animateCamera(CameraUpdateFactory.zoomTo(20F))
                    item = p0?.getItemAtPosition(p2).toString()
                    bindingBottomSheet.tvCurrentLocation.text = item
                }
            }
        }
    }
}
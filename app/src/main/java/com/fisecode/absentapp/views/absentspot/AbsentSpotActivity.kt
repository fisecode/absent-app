package com.fisecode.absentapp.views.absentspot


import android.Manifest
import android.app.AlertDialog
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityAbsentSpotBinding
import com.fisecode.absentapp.databinding.BottomSheetAbsentSpotBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.AbsentSpotResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.networking.RetrofitClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.gms.location.LocationCallback


class AbsentSpotActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_CODE_MAP_PERMISSIONS = 1000
        private const val REQUEST_CODE_LOCATION = 2000
        private val TAG = AbsentSpotActivity::class.java.simpleName
    }

    private val mapPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var binding: ActivityAbsentSpotBinding? = null
    private var bindingBottomSheet: BottomSheetAbsentSpotBinding? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    //Configuration Map
    private var mapAbsentSpot: SupportMapFragment? = null
    private var map: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private var locationRequest: LocationRequest? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var settingsClient: SettingsClient? = null
    private var currentLocation: Location? = null
    private var locationCallBack: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var spinner: AutoCompleteTextView? = null

    private var spotList: ArrayList<String> = ArrayList()
    private var latitude = -6.284665797423048
    private var longitude = 106.75063133077147

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsentSpotBinding.inflate(layoutInflater)
        bindingBottomSheet = binding?.layoutBottomSheet
        setContentView(binding?.root)
        init()
        setupMaps()
        onClick()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray

    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_MAP_PERMISSIONS -> {
                var isHasPermission = false
                val permissionNotGranted = StringBuilder()

                for (i in permissions.indices) {
                    isHasPermission = grantResults[i] == PackageManager.PERMISSION_GRANTED

                    if (!isHasPermission) {
                        permissionNotGranted.append("${permissions[i]}\n")
                    }
                }

                if (isHasPermission) {
                    setupMaps()
                } else {
                    val message =
                        permissionNotGranted.toString() + "\n" + getString(R.string.not_granted)
                    MyDialog.dynamicDialog(this, getString(R.string.required_permission), message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        bindingBottomSheet = null
        stopLocationUpdates()
    }

    private fun setupMaps() {
        mapAbsentSpot = supportFragmentManager.findFragmentById(R.id.map_absent_spot) as? SupportMapFragment
        mapAbsentSpot?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (checkPermission()) {
            val absentSpotName = HawkStorage.instance(this).getAbsentSpot()?.nameSpot
            if (absentSpotName == "Office" || absentSpotName.isNullOrBlank()){
                bindingBottomSheet?.autoCompleteTextView?.setText("Office")
                officeMap()
            }else{
                bindingBottomSheet?.autoCompleteTextView?.setText(absentSpotName)
                goToCurrentLocation()
            }
        } else {
            setRequestPermission()
        }

    }

    private fun onClick() {
        binding?.tbAbsentSpot?.setNavigationOnClickListener {
            finish()
        }
        binding?.fabGetCurrentLocation?.setOnClickListener {
            goToCurrentLocation()
        }
        bindingBottomSheet?.btnSubmitSpot?.setOnClickListener {
            val token = HawkStorage.instance(this).getToken()
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.are_you_sure))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    sendDataAbsentSpot(token)
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun init() {
        setSupportActionBar(binding?.tbAbsentSpot)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        spinner()

        val absentSpotStatus = HawkStorage.instance(this).getAbsentSpot()?.status
        if (absentSpotStatus == "Pending"){
            bindingBottomSheet?.btnSubmitSpot?.text = "Waiting for Approval"
            bindingBottomSheet?.btnSubmitSpot?.backgroundTintList = getColorStateList(R.color.grey)
            bindingBottomSheet?.btnSubmitSpot?.isEnabled = false
            bindingBottomSheet?.tiAbsentSpot?.isEnabled = false
        }else{
            bindingBottomSheet?.btnSubmitSpot?.text = getString(R.string.submit)
            bindingBottomSheet?.btnSubmitSpot?.backgroundTintList = getColorStateList(R.color.color_primary)
            bindingBottomSheet?.btnSubmitSpot?.isEnabled = true
        }

        //Setup BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bindingBottomSheet!!.bottomSheetAbsentSpot)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        //Setup Location
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        settingsClient = LocationServices.getSettingsClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        locationSettingsRequest = builder.build()

    }

    private fun spinner() {
        //Setup Spinner
        spotList = ArrayList()
        spotList.add("Office")
        spotList.add("Home")
        spinner = bindingBottomSheet?.autoCompleteTextView
        spinner?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                if (p2 == 0) {
                    officeMap()
                } else {
                    goToCurrentLocation()
                }
            }
    }

    private fun officeMap() {
        val adapter = ArrayAdapter(this, R.layout.dropdown_leave_type, spotList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.setAdapter(adapter)
        bindingBottomSheet?.tittleCurrentLocation?.text = getString(R.string.location)
        map?.clear()
        map?.isMyLocationEnabled = false
        binding?.fabGetCurrentLocation?.visibility = View.GONE
        fusedLocationProviderClient?.removeLocationUpdates(locationCallBack!!)
        map?.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .title("Office")
        )
        val cameraPosition = CameraPosition.builder()
            .target(LatLng(latitude, longitude))
            .zoom(20f)
            .bearing(0f)
            .tilt(45f)
            .build()
        map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        val address = getAddress(latitude, longitude)
        if (address != null && address.isNotEmpty()) {
            bindingBottomSheet?.tvCurrentLocation?.text = address
        }
    }

    private fun goToCurrentLocation() {
        map?.clear()
        bindingBottomSheet?.tvCurrentLocation?.text = getString(R.string.search_your_location)
        bindingBottomSheet?.tittleCurrentLocation?.text = getString(R.string.current_location)
        binding?.fabGetCurrentLocation?.visibility = View.VISIBLE
        val adapter = ArrayAdapter(this, R.layout.dropdown_leave_type, spotList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.setAdapter(adapter)
        if (checkPermission()) {
            if (isLocationEnabled()) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = false
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

                locationCallBack = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        currentLocation = locationResult.lastLocation

                        if (currentLocation != null) {
                            val latitude = currentLocation?.latitude
                            val longitude = currentLocation?.longitude

                            if (latitude != null && longitude != null) {
                                val latLng = LatLng(latitude, longitude)
                                val cameraPosition = CameraPosition.builder()
                                    .target(latLng)
                                    .zoom(20f)
                                    .bearing(0f)
                                    .tilt(45f)
                                    .build()
                                map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                                val address = getAddress(latitude, longitude)
                                if (address != null && address.isNotEmpty()) {
                                    bindingBottomSheet?.tvCurrentLocation?.text = address
                                }
                            }
                        }
                    }
                }
                fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest!!,
                    locationCallBack!!,
                    Looper.myLooper()
                )
            } else {
                goToTurnOnGps()
            }
        } else {
            setRequestPermission()
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val result: String
        this.let {
            val geocode = Geocoder(it, Locale.getDefault())
            val addresses = geocode.getFromLocation(latitude, longitude, 1)

            if (addresses.size > 0) {
                result = addresses[0].getAddressLine(0)
                return result
            }
        }
        return null
    }

    private fun isLocationEnabled(): Boolean {
        if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!! ||
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!
        ) {
            return true
        }
        return false
    }

    private fun goToTurnOnGps() {
        settingsClient?.checkLocationSettings(locationSettingsRequest!!)
            ?.addOnSuccessListener {
                goToCurrentLocation()
            }?.addOnFailureListener {
                when ((it as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvableApiException = it as ResolvableApiException
                            resolvableApiException.startResolutionForResult(
                                this,
                                REQUEST_CODE_LOCATION
                            )
                        } catch (ex: IntentSender.SendIntentException) {
                            ex.printStackTrace()
                            Log.e(TAG, "Error: ${ex.message}")
                        }
                    }
                }
            }
    }

    private fun checkPermission(): Boolean {
        var isHasPermission = false
        this.let {
            for (permission in mapPermissions) {
                isHasPermission = ActivityCompat.checkSelfPermission(
                    it,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        return isHasPermission
    }

    private fun setRequestPermission() {
        requestPermissions(mapPermissions, REQUEST_CODE_MAP_PERMISSIONS)
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallBack!!)
    }

    private fun sendDataAbsentSpot(token: String) {
        val params = HashMap<String, RequestBody>()
        MyDialog.showProgressDialog(this)
        val nameSpot = bindingBottomSheet?.autoCompleteTextView?.text.toString()
        val getLatitude: String
        val getLongitude: String
        if (nameSpot == "Office") {
            getLatitude = latitude.toString()
            getLongitude = longitude.toString()
        } else {
            getLatitude = currentLocation?.latitude.toString()
            getLongitude = currentLocation?.longitude.toString()
        }
        val address = bindingBottomSheet?.tvCurrentLocation?.text.toString()

        val mediaTypeText = MultipartBody.FORM

        val requestNameSpot = nameSpot.toRequestBody(mediaTypeText)
        val requestLatitude = getLatitude.toRequestBody(mediaTypeText)
        val requestLongitude = getLongitude.toRequestBody(mediaTypeText)
        val requestAddress = address.toRequestBody(mediaTypeText)

        params["name_spot"] = requestNameSpot
        params["latitude"] = requestLatitude
        params["longitude"] = requestLongitude
        params["address"] = requestAddress

        ApiServices.getAbsentServices()
            .absentSpot("Bearer $token", params)
            .enqueue(object : Callback<Wrapper<AbsentSpotResponse>> {
                override fun onResponse(
                    call: Call<Wrapper<AbsentSpotResponse>>,
                    response: Response<Wrapper<AbsentSpotResponse>>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful) {
                        val absentSpot = response.body()?.data?.absentSpot
                        val message = response.body()?.meta?.message
                        if (message != null) {
                            MyDialog.dynamicDialog(
                                this@AbsentSpotActivity,
                                getString(R.string.success),
                                message
                            )
                            bindingBottomSheet?.btnSubmitSpot?.text = "Waiting for Approval"
                            bindingBottomSheet?.btnSubmitSpot?.backgroundTintList = getColorStateList(R.color.grey)
                            bindingBottomSheet?.btnSubmitSpot?.isEnabled = false
                            bindingBottomSheet?.tiAbsentSpot?.isEnabled = false
                            HawkStorage.instance(this@AbsentSpotActivity).setAbsentSpot(absentSpot)
                        }
                    } else {
                        val errorConverter: Converter<ResponseBody, Wrapper<AbsentSpotResponse>> =
                            RetrofitClient
                                .getClient()
                                .responseBodyConverter(
                                    Wrapper::class.java,
                                    arrayOfNulls<Annotation>(0)
                                )
                        var errorResponse: Wrapper<AbsentSpotResponse>?
                        try {
                            response.errorBody()?.let {
                                errorResponse = errorConverter.convert(it)
                                MyDialog.dynamicDialog(
                                    this@AbsentSpotActivity,
                                    getString(R.string.failed),
                                    errorResponse?.meta?.message.toString()
                                )
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e(TAG, "Error: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<Wrapper<AbsentSpotResponse>>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

}
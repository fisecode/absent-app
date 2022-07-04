package com.fisecode.absentapp.views.absent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentAbsentBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.*
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.networking.RetrofitClient
import com.fisecode.absentapp.utils.Helpers
import com.fisecode.absentapp.views.absentspot.AbsentSpotActivity
import com.fisecode.absentapp.views.history.HistoryActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.*

class AbsentFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_MAP_PERMISSIONS = 1000
        private const val REQUEST_CODE_LOCATION = 2000
        private val TAG = AbsentFragment::class.java.simpleName
    }

    private val requestPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var binding: FragmentAbsentBinding? = null
    private var locationManager: LocationManager? = null
    private lateinit var locationRequest: LocationRequest
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var settingsClient: SettingsClient? = null
    private var currentLocation: Location? = null
    private lateinit var locationCallBack: LocationCallback
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var currentPhotoPath = ""
    private var isCheckIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAbsentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
        checkPermissionApp()
    }

    override fun onResume() {
        super.onResume()
        checkIfAlreadyPresent()
        getUserData()
        getAbsentSpot()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun init() {
        updateView()
        //Setup Location
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager =
            context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        settingsClient = LocationServices.getSettingsClient(requireActivity())
        locationRequest = LocationRequest.create().apply {
            interval = 1000 * 5
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()

    }

    private fun onClick() {
        binding?.btnHistory?.setOnClickListener {
            context?.startActivity<HistoryActivity>()
        }
        binding?.btnCheckIn?.setOnClickListener {
            MyDialog.showProgressDialog(context)
            Handler(Looper.getMainLooper()).postDelayed({
                getLastLocation()
            }, 2000)
        }
        binding?.btnCheckOut?.setOnClickListener {
            val token = HawkStorage.instance(context).getToken()
            if (isCheckIn){
                MyDialog.showProgressDialog(context)
                Handler(Looper.getMainLooper()).postDelayed({
                    getLastLocation()
                }, 2000)
            }
        }
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                val absentSpot = HawkStorage.instance(context).getAbsentSpot()
                if (absentSpot?.status == "Approved") {
                    locationCallBack = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            currentLocation = locationResult.lastLocation

                            if (currentLocation != null) {
                                val currentLat = currentLocation?.latitude
                                val currentLong = currentLocation?.longitude
                                val absentSpotLat = absentSpot.latitude?.toDouble()
                                val absentSpotLong = absentSpot.longitude?.toDouble()

                                val geofence = calculateDistance(
                                    currentLat!!,
                                    currentLong!!,
                                    absentSpotLat!!,
                                    absentSpotLong!!
                                ) * 1000

                                if (geofence < 100.0) {
                                    ImagePicker.with(this@AbsentFragment)
                                        .cameraOnly()  //Final image resolution will be less than 1080 x 1080(Optional)
                                        .createIntent { intent ->
                                            startForProfileImageResult.launch(intent)
                                        }
                                    Toast.makeText(
                                        context,
                                        getString(R.string.success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    MyDialog.dynamicDialog(
                                        context,
                                        getString(R.string.failed),
                                        getString(R.string.you_are_outside_the_absent_area)
                                    )
                                }


                            }
                            fusedLocationProviderClient?.removeLocationUpdates(this)
                            MyDialog.hideDialog()
                        }
                    }
                    fusedLocationProviderClient?.requestLocationUpdates(
                        locationRequest,
                        locationCallBack,
                        Looper.getMainLooper()
                    )
                } else {
                    MyDialog.hideDialog()
                    MyDialog.dynamicDialog(
                        context,
                        getString(R.string.failed),
                        "Your absence spot is waiting for approval."
                    )
                }

            } else {
                MyDialog.hideDialog()
                goToTurnOnGps()
            }

        } else {
            MyDialog.hideDialog()
            requestPermissionLauncher.launch(requestPermissions)
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    val token = HawkStorage.instance(context).getToken()
                    if (isCheckIn){
                        sendDataAbsent(fileUri, token, "out")
                    }else{
                        sendDataAbsent(fileUri, token, "in")
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    MyDialog.dynamicDialog(
                        context,
                        getString(R.string.failed),
                        "Check In Cancelled"
                    )
                }
            }
        }

    private fun sendDataAbsent(photoPath: Uri?, token: String, type: String) {
        val params = HashMap<String, RequestBody>()
        MyDialog.showProgressDialog(context)
        if (currentLocation != null) {
            val latitude = currentLocation?.latitude.toString()
            val longitude = currentLocation?.longitude.toString()
            val address = getAddress(latitude.toDouble(), longitude.toDouble()).toString()
            val absentSpot = HawkStorage.instance(context).getAbsentSpot()?.nameSpot.toString()
            val file = File(photoPath?.path)
            val uri = FileProvider.getUriForFile(
                context!!,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )
            val typeFile = context!!.contentResolver.getType(uri)

            val mediaTypeText = MultipartBody.FORM
            val mediaTypeFile = typeFile?.toMediaType()

            val requestLatitude = latitude.toRequestBody(mediaTypeText)
            val requestLongitude = longitude.toRequestBody(mediaTypeText)
            val requestAddress = address.toRequestBody(mediaTypeText)
            val requestAbsentSpot = absentSpot.toRequestBody(mediaTypeText)
            val requestType = type.toRequestBody(mediaTypeText)

            params["latitude"] = requestLatitude
            params["longitude"] = requestLongitude
            params["absent_spot"] = requestAbsentSpot
            params["address"] = requestAddress
            params["type"] = requestType

            val requestPhotoFile = file.asRequestBody(mediaTypeFile)
            val multipartBody =
                MultipartBody.Part.createFormData("photo", file.name, requestPhotoFile)

            ApiServices.getAbsentServices()
                .absent("Bearer $token", params, multipartBody)
                .enqueue(object : Callback<Wrapper<AbsentResponse>> {
                    override fun onResponse(
                        call: Call<Wrapper<AbsentResponse>>,
                        response: Response<Wrapper<AbsentResponse>>
                    ) {
                        MyDialog.hideDialog()
                        if (response.isSuccessful) {
                            val absentResponse = response.body()
                            if (type == "in") {
                                MyDialog.dynamicDialog(
                                    context,
                                    getString(R.string.check_in_success),
                                    absentResponse?.meta?.message.toString()
                                )
                            } else {
                                MyDialog.dynamicDialog(
                                    context,
                                    getString(R.string.check_out_seccess),
                                    absentResponse?.meta?.message.toString()
                                )
                            }
                            checkIfAlreadyPresent()
                        } else {
                            MyDialog.dynamicDialog(
                                context,
                                getString(R.string.alert),
                                getString(R.string.something_wrong))
                        }
                    }

                    override fun onFailure(call: Call<Wrapper<AbsentResponse>>, t: Throwable) {
                        MyDialog.hideDialog()
                        Log.e(TAG, "Error: ${t.message}")
                    }

                })

        }
    }

    private fun checkIfAlreadyPresent() {
        val token = HawkStorage.instance(context).getToken()
        val currentDate = Helpers.getCurrentDateForServer()

        ApiServices.getAbsentServices()
            .getHistoryAbsent("Bearer $token", currentDate, currentDate)
            .enqueue(object :Callback<Wrapper<AbsentHistoryResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<AbsentHistoryResponse>>,
                    response: Response<Wrapper<AbsentHistoryResponse>>
                ) {
                    if (response.isSuccessful){
                        val histories = response.body()?.data?.absent
                        if (histories != null && histories.isNotEmpty()){
                            if (histories[0].status == "Present"){
//                                HawkStorage.instance(context).setAbsent(histories)
                                isCheckIn = false
                                checkIsCheckIn()
                                binding?.btnCheckIn?.visibility = View.GONE
                                binding?.btnCheckOut?.visibility = View.GONE
                                binding?.tvPresentInfo?.visibility = View.VISIBLE
                            }else{
                                isCheckIn = true
                                checkIsCheckIn()
                                binding?.btnCheckIn?.visibility = View.VISIBLE
                                binding?.btnCheckOut?.visibility = View.VISIBLE
                                binding?.tvPresentInfo?.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Wrapper<AbsentHistoryResponse>>, t: Throwable) {
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

    private fun checkIsCheckIn() {
        if (isCheckIn){
            binding?.btnCheckIn?.isEnabled = false
            binding?.btnCheckIn?.backgroundTintList = getColorStateList(requireContext(), android.R.color.darker_gray)
            binding?.btnCheckOut?.backgroundTintList = getColorStateList(requireContext(), R.color.btn_active)
        }else{
            binding?.btnCheckOut?.isEnabled = false
            binding?.btnCheckOut?.backgroundTintList = getColorStateList(requireContext(), android.R.color.darker_gray)
            binding?.btnCheckIn?.backgroundTintList = getColorStateList(requireContext(), R.color.btn_active)
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val result: String
        context?.let {
            val geocode = Geocoder(it, Locale.getDefault())
            val address = geocode.getFromLocation(latitude, longitude, 1)

            if (address.size > 0) {
                result = address[0].getAddressLine(0)
                return result
            }
        }
        return null
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6372.8 // in kilometers

        val radiansLat1 = Math.toRadians(lat1)
        val radiansLat2 = Math.toRadians(lat2)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        return 2 * r * asin(
            sqrt(
                sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(radiansLat1) * cos(
                    radiansLat2
                )
            )
        )
    }

    private fun getUserData() {
        val token = HawkStorage.instance(context).getToken()
//        MyDialog.showProgressDialog(context)
        ApiServices.getAbsentServices()
            .getUser("Bearer $token")
            .enqueue(object : Callback<Wrapper<GetUserResponse>> {
                override fun onResponse(
                    call: Call<Wrapper<GetUserResponse>>,
                    response: Response<Wrapper<GetUserResponse>>
                ) {
//                    MyDialog.hideDialog()
                    if (response.isSuccessful) {
                        val user = response.body()?.data?.employee?.get(0)?.user
                        val employee = response.body()?.data?.employee?.first()
                        if (user != null && employee != null) {
                            HawkStorage.instance(context).setUser(user)
                            HawkStorage.instance(context).setEmployee(employee)
                            updateView()
                        }
                    } else {
                        val errorConverter: Converter<ResponseBody, Wrapper<GetUserResponse>> =
                            RetrofitClient
                                .getClient()
                                .responseBodyConverter(
                                    GetUserResponse::class.java,
                                    arrayOfNulls<Annotation>(0)
                                )
                        val errorResponse: Wrapper<GetUserResponse>?
                        try {
                            response.errorBody()?.let {
                                errorResponse = errorConverter.convert(it)
                                MyDialog.dynamicDialog(
                                    context,
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

                override fun onFailure(call: Call<Wrapper<GetUserResponse>>, t: Throwable) {
//                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

    private fun getAbsentSpot() {
        val token = HawkStorage.instance(context).getToken()
        ApiServices.getAbsentServices()
            .getAbsentSpot("Bearer $token")
            .enqueue(object : Callback<Wrapper<AbsentSpotResponse>> {
                override fun onResponse(
                    call: Call<Wrapper<AbsentSpotResponse>>,
                    response: Response<Wrapper<AbsentSpotResponse>>
                ) {
//                    MyDialog.hideDialog()
                    if (response.isSuccessful) {
                        val absentSpot = response.body()?.data?.absentSpot
                        if (absentSpot != null) {
                            HawkStorage.instance(context).setAbsentSpot(absentSpot)
                        }
                    } else {
                        val errorConverter: Converter<ResponseBody, Wrapper<AbsentSpotResponse>> =
                            RetrofitClient
                                .getClient()
                                .responseBodyConverter(
                                    AbsentSpotResponse::class.java,
                                    arrayOfNulls<Annotation>(0)
                                )
                        val errorResponse: Wrapper<AbsentSpotResponse>?
                        try {
                            response.errorBody()?.let {
                                errorResponse = errorConverter.convert(it)
                                MyDialog.dynamicDialog(
                                    context,
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
//                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

    private fun updateView() {
        val user = HawkStorage.instance(context).getUser()
        val employee = HawkStorage.instance(context).getEmployee()
        val imageUrl = BuildConfig.BASE_IMAGE_URL + user.photo
        Glide.with(requireContext()).load(imageUrl).placeholder(R.drawable.employee_photo)
            .into(binding!!.ivEmployeePhoto)
        binding?.tvNameEmployee?.text = user.name
        binding?.tvNumberIdEmployee?.text = Helpers.employeeIdFormat(employee.employeeId)
    }

    private fun checkPermission(): Boolean {
        var isHasPermission = false
        this.let {
            for (permission in requestPermissions) {
                isHasPermission = ActivityCompat.checkSelfPermission(
                    context!!,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        return isHasPermission
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            run {
                var isHasPermission = false
                val permissionNotGranted = StringBuilder()

                for (i in permissions.values) {
                    isHasPermission = i
                }

                if (isHasPermission) {
                    if (!isLocationEnabled()) {
                        goToTurnOnGps()
                    }
                } else {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.app_permission_denied),
                        Snackbar.LENGTH_SHORT
                    ).setAction(getString(
                        R.string.settings
                    ), View.OnClickListener {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            )
                        )
                    }).show()
                }
            }
//            when {
//                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                    Toast.makeText(context, "TEST 1", Toast.LENGTH_SHORT).show()
//                }
//                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                    Toast.makeText(context, "TEST 2", Toast.LENGTH_SHORT).show()
//                }
//                else -> {
//                    Toast.makeText(context, "TEST 3", Toast.LENGTH_SHORT).show()
//                }
//            }
        }


    private fun setRequestPermission() {
        requestPermissionLauncher.launch(requestPermissions)
    }

    private fun checkPermissionApp() {
        if (checkPermission()) {
            if (!isLocationEnabled()) {
                goToTurnOnGps()
            }
        } else {
            setRequestPermission()
        }
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
                init()
            }?.addOnFailureListener {
                when ((it as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvableApiException = it as ResolvableApiException
                            resolvableApiException.startResolutionForResult(
                                requireActivity(),
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


}
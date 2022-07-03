package com.fisecode.absentapp.views.absent

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentAbsentBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.AbsentSpotResponse
import com.fisecode.absentapp.model.GetUserResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.networking.RetrofitClient
import com.fisecode.absentapp.utils.Helpers
import com.fisecode.absentapp.views.absentspot.AbsentSpotActivity
import com.fisecode.absentapp.views.history.HistoryActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

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
    private var locationRequest: LocationRequest? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var settingsClient: SettingsClient? = null
    private var currentLocation: Location? = null
    private var locationCallBack: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

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
        locationManager = context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        settingsClient = LocationServices.getSettingsClient(requireActivity())
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        locationSettingsRequest = builder.build()
    }

    private fun onClick() {
        binding?.btnHistory?.setOnClickListener {
            context?.startActivity<HistoryActivity>()
        }
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
                    if (!isLocationEnabled()){
                        goToTurnOnGps()
                    }
                } else {
                    Snackbar.make(requireView(), getString(R.string.app_permission_denied), Snackbar.LENGTH_SHORT).setAction(getString(
                                            R.string.settings), View.OnClickListener {
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)))
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
            if (!isLocationEnabled()){
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
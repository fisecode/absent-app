package com.fisecode.absentapp.views.leave

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityLeaveRequestBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.*
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.networking.RetrofitClient
import com.fisecode.absentapp.utils.Helpers.formatTo
import com.fisecode.absentapp.utils.Helpers.toDateForServer
import com.fisecode.absentapp.views.employeedetail.EditProfileActivity
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LeaveRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveRequestBinding
    private var builder = MaterialDatePicker.Builder.dateRangePicker()
    private var dateRangePicker = builder.build()
    private var listLeaveType: ArrayList<String> = ArrayList()
    private var leaveTypeId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClick()
    }

    override fun onResume() {
        super.onResume()
        initLeaveType()
    }

    private fun onClick() {
        binding.tbLeaveRequest.setNavigationOnClickListener {
            finish()
        }
        binding.btnCalendar.setOnClickListener {
            dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
            dateRangePicker.addOnPositiveButtonClickListener {
                val strFormatDefault = "dd MMM yyyy"
                val simpleDateFormat = SimpleDateFormat(strFormatDefault, Locale.getDefault())
                val startDate = dateRangePicker.selection?.first
                val endDate = dateRangePicker.selection?.second
                binding.etStartDate.setText(simpleDateFormat.format(startDate))
                binding.etEndDate.setText(simpleDateFormat.format(endDate))
            }
        }

        binding.btnSubmit.setOnClickListener {
            val token = HawkStorage.instance(this).getToken()
            val startDate = binding.etStartDate.text.toString()
            val endDate = binding.etEndDate.text.toString()
            val reason = binding.etReason.text.toString()
            val leaveTypeName = binding.autoCompleteTextView.text.toString()
            val leaveType = HawkStorage.instance(this).getLeaveType()
            for (i in leaveType?.indices!!) {
                if (leaveType[i].name == leaveTypeName){
                    leaveTypeId = leaveType[i].id.toString()
                }
            }
            if (isFormValid(leaveTypeName, startDate, endDate, reason)){
                leave(token, leaveTypeId, startDate, endDate, reason)
            }
        }
    }

    private fun leave(
        token: String,
        leaveTypeId: String,
        startDate: String,
        endDate: String,
        reason: String
    ) {
        val params = HashMap<String, RequestBody>()
        MyDialog.showProgressDialog(this)

        val mediaTypeText = MultipartBody.FORM

        val requestLeaveTypeId = leaveTypeId.toRequestBody(mediaTypeText)
        val requestStartDate = startDate.toDateForServer().formatTo("yyyy-MM-dd").toRequestBody(mediaTypeText)
        val requestEndDate = endDate.toDateForServer().formatTo("yyyy-MM-dd").toRequestBody(mediaTypeText)
        val requestReason = reason.toRequestBody(mediaTypeText)

        params["leave_type_id"] = requestLeaveTypeId
        params["start_date"] = requestStartDate
        params["end_date"] = requestEndDate
        params["leave_reason"] = requestReason

        ApiServices.getAbsentServices()
            .leave("Bearer $token", params)
            .enqueue(object : Callback<Wrapper<LeaveResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<LeaveResponse>>,
                    response: Response<Wrapper<LeaveResponse>>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful){
                        val message = response.body()?.meta?.message
                        if (message != null) {
                            MyDialog.dynamicDialog(this@LeaveRequestActivity, getString(R.string.success), message)
                        }
                    }else{
                        MyDialog.dynamicDialog(this@LeaveRequestActivity, getString(R.string.alert), getString(R.string.something_wrong))
                    }
                }

                override fun onFailure(call: Call<Wrapper<LeaveResponse>>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }
            })
    }

    private fun isFormValid(
        leaveTypeName: String,
        startDate: String,
        endDate: String,
        reason: String
    ): Boolean {
        if (leaveTypeName.isEmpty()){
            binding.autoCompleteTextView.error = "Please select leave type!"
            binding.autoCompleteTextView.requestFocus()
        }else if (startDate.isEmpty()){
            binding.autoCompleteTextView.error = null
            binding.etStartDate.error = "Please select start date!"
            binding.etStartDate.requestFocus()
        }else if (endDate.isEmpty()) {
            binding.etStartDate.error = null
            binding.etEndDate.error = "Please select end date!"
            binding.etEndDate.requestFocus()
        }else if (reason.isEmpty()) {
            binding.etEndDate.error = null
            binding.etReason.error = "Please field your reason!"
            binding.etReason.requestFocus()
        }else {
            binding.autoCompleteTextView.error = null
            binding.etStartDate.error = null
            binding.etEndDate.error = null
            binding.etReason.error = null
            return true
        }
        return false
    }

    private fun init() {
        setSupportActionBar(binding.tbLeaveRequest)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getLeaveType()
        initLeaveType()
    }

    private fun getLeaveType() {
        val token = HawkStorage.instance(this).getToken()
        MyDialog.showProgressDialog(this)
        ApiServices.getAbsentServices()
            .leaveType("Bearer $token")
            .enqueue(object : Callback<Wrapper<LeaveTypeResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<LeaveTypeResponse>>,
                    response: Response<Wrapper<LeaveTypeResponse>>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful){
                        val leaveType = response.body()?.data?.leaveType
                        if (leaveType != null){
                            HawkStorage.instance(this@LeaveRequestActivity).setLeaveType(leaveType)
                            initLeaveType()
                        }
                    }else{
                        val errorConverter: Converter<ResponseBody, Wrapper<LeaveTypeResponse>> =
                            RetrofitClient
                                .getClient()
                                .responseBodyConverter(
                                    LeaveTypeResponse::class.java,
                                    arrayOfNulls<Annotation>(0)
                                )
                        val errorResponse: Wrapper<LeaveTypeResponse>?
                        try {
                            response.errorBody()?.let {
                                errorResponse = errorConverter.convert(it)
                                MyDialog.dynamicDialog(this@LeaveRequestActivity,
                                    getString(R.string.failed),
                                    errorResponse?.meta?.message.toString())
                            }
                        }catch (e: IOException){
                            e.printStackTrace()
                            Log.e(TAG, "Error: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<Wrapper<LeaveTypeResponse>>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }
            })
    }

    private fun initLeaveType() {
        val leaveType = HawkStorage.instance(this).getLeaveType()
        for (i in leaveType?.indices!!) {
            listLeaveType.add(leaveType[i].name.toString())
        }
        val adapter = ArrayAdapter(this, R.layout.dropdown_leave_type, listLeaveType)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.autoCompleteTextView.setAdapter(adapter)
    }

    companion object{
        private val TAG = LeaveTypeResponse::class.java.simpleName
    }
}
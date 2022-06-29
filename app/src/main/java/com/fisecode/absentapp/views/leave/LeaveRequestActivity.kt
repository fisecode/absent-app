package com.fisecode.absentapp.views.leave

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityLeaveRequestBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.LeaveTypeResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LeaveRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveRequestBinding
    private var builder = MaterialDatePicker.Builder.dateRangePicker()
    private var dateRangePicker = builder.build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClick()
    }

    private fun onClick() {
        binding.tbLeaveRequest.setNavigationOnClickListener {
            finish()
        }
        binding.btnCalendar.setOnClickListener {
            dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
            dateRangePicker.addOnPositiveButtonClickListener {
                val strFormatDefault = "dd MMMM yyyy"
                val simpleDateFormat = SimpleDateFormat(strFormatDefault, Locale.getDefault())
                val startDate = dateRangePicker.selection?.first
                val endDate = dateRangePicker.selection?.second
                binding.etStartDate.setText(simpleDateFormat.format(startDate))
                binding.etEndDate.setText(simpleDateFormat.format(endDate))
            }
        }
    }

    private fun init() {
        setSupportActionBar(binding.tbLeaveRequest)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(this, R.layout.dropdown_leave_type, items)
        binding.autoCompleteTextView.setAdapter(adapter)

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
                            val leaveType = HawkStorage.instance(this@LeaveRequestActivity).getLeaveType()
                            val listSpinner: List<String> = ArrayList()
                            for (i in leaveType.indices) {
                                leaveType[i]?.name
                                TODO("Not yet implemented")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Wrapper<LeaveTypeResponse>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
}
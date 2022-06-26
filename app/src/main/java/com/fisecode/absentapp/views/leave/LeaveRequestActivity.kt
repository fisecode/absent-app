package com.fisecode.absentapp.views.leave

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.util.Pair
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityLeaveRequestBinding
import com.fisecode.absentapp.model.dummy.LeaveModel
import com.google.android.material.datepicker.MaterialDatePicker
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
}
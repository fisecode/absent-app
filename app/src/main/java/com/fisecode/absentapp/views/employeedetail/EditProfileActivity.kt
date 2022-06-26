package com.fisecode.absentapp.views.employeedetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityEditProfileBinding
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var builder = MaterialDatePicker.Builder.datePicker()
    private var datePicker = builder.build()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        onClick()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun onClick() {
        binding.tbEditProfile.setNavigationOnClickListener {
            finish()
        }
        binding.tgGender.addOnButtonCheckedListener{ _, checkedId, isChecked ->
           if (isChecked){
               when (checkedId) {
                   R.id.btn_male -> Toast.makeText(this, "Button Male", Toast.LENGTH_SHORT).show()
                   R.id.btn_female -> Toast.makeText(this, "Button Female", Toast.LENGTH_SHORT).show()
               }
           }
        }
        binding.etAddress.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
        binding.etDob.setOnClickListener {
            datePicker.show(supportFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener {
                val strFormatDefault = "dd MMMM yyyy"
                val simpleDateFormat = SimpleDateFormat(strFormatDefault, Locale.getDefault())
                val selectDate = datePicker.selection
                binding.etDob.setText(simpleDateFormat.format(selectDate))
            }
        }
    }

    private fun init() {
        setSupportActionBar(binding.tbEditProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
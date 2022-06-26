package com.fisecode.absentapp.views.employeedetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityEmployeeDetailBinding
import com.fisecode.absentapp.views.leave.LeaveRequestActivity
import org.jetbrains.anko.startActivity

class EmployeeDetailActivity : AppCompatActivity() {

    private var binding: ActivityEmployeeDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        init()
        onClick()
    }

    private fun onClick() {
        binding?.tbEmployeeDetail?.setNavigationOnClickListener {
            finish()
        }
        binding?.tvChangePhoto?.setOnClickListener {
            Toast.makeText(this,"CHANGE PHOTO", Toast.LENGTH_SHORT).show()
        }
        binding?.btnEditProfile?.setOnClickListener {
            startActivity<EditProfileActivity>()
        }

    }

    private fun init() {
        setSupportActionBar(binding?.tbEmployeeDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
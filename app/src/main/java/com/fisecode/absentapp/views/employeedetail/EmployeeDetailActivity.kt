package com.fisecode.absentapp.views.employeedetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityEmployeeDetailBinding
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.utils.Helpers
import com.fisecode.absentapp.utils.Helpers.formatTo
import com.fisecode.absentapp.utils.Helpers.getDateForServer
import com.fisecode.absentapp.utils.Helpers.toDate
import com.fisecode.absentapp.utils.Helpers.toDateForServer
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

    private fun updateView() {
        val user = HawkStorage.instance(this).getUser()
        val employee = HawkStorage.instance(this).getEmployee()
        val imageUrl = BuildConfig.BASE_IMAGE_URL + user.photo
        Glide.with(this).load(imageUrl).placeholder(R.drawable.employee_photo).into(binding!!.ivEmployeePhoto)
        binding?.tvFullName?.text = user.name
        binding?.tvEmployeeId?.text = Helpers.employeeIdFormat(employee.employeeId)
        binding?.tvEmail?.text = employee.email
        binding?.tvDob?.text = employee.dob?.toDate()?.formatTo("dd MMM yyyy")
        binding?.tvGender?.text = employee.gender
        binding?.tvAddress?.text = employee.address
        binding?.tvPhone?.text = employee.phone
        binding?.tvDoj?.text = employee.doj?.toDate()?.formatTo("dd MMM yyyy")
        binding?.tvDivision?.text = employee.division
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
            finish()
        }

    }

    private fun init() {
        setSupportActionBar(binding?.tbEmployeeDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateView()
    }
}
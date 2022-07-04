package com.fisecode.absentapp.views.employeedetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityEditProfileBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.UpdateProfileResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.utils.Helpers
import com.fisecode.absentapp.utils.Helpers.formatTo
import com.fisecode.absentapp.utils.Helpers.toDate
import com.fisecode.absentapp.utils.Helpers.toDateForServer
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var builder = MaterialDatePicker.Builder.datePicker()
    private var datePicker = builder.build()
    private var gender = ""

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
                   R.id.btn_male -> gender = "Male"
                   R.id.btn_female -> gender = "Female"
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
        binding.btnUpdateProfile.setOnClickListener {
//            Toast.makeText(this@EditProfileActivity, "Update Profile Successfully.", Toast.LENGTH_SHORT).show()
            val token = HawkStorage.instance(this).getToken()
            val name = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            if (isFormValid(name, email)){
                updateProfile(token)
            }
        }
    }

    private fun isFormValid(name: String, email: String): Boolean {
        if (name.isEmpty()){
            binding.etFullName.error = getString(R.string.please_field_your_fullname)
            binding.etFullName.requestFocus()
        }else if (email.isEmpty()){
            binding.etFullName.error = null
            binding.etEmail.error = getString(R.string.please_field_your_email)
            binding.etEmail.requestFocus()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = getString(R.string.please_use_valid_email)
            binding.etEmail.requestFocus()
        }else{
            binding.etFullName.error = null
            binding.etEmail.error = null
            return true
        }
        return false
    }

    private fun updateProfile(token: String) {
        val params = HashMap<String, RequestBody>()
        MyDialog.showProgressDialog(this)
        val name = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()
        val dob = binding.etDob.text.toString().toDateForServer().formatTo("yyyy-MM-dd")
        val gender = gender
        val phone = binding.etPhone.text.toString()
        val address = binding.etAddress.text.toString()

        val mediaTypeText = MultipartBody.FORM

        val requestName = name.toRequestBody(mediaTypeText)
        val requestEmail = email.toRequestBody(mediaTypeText)
        val requestDob = dob.toRequestBody(mediaTypeText)
        val requestGender = gender.toRequestBody(mediaTypeText)
        val requestPhone = phone.toRequestBody(mediaTypeText)
        val requestAddress = address.toRequestBody(mediaTypeText)

        params["name"] = requestName
        params["email"] = requestEmail
        params["dob"] = requestDob
        params["gender"] = requestGender
        params["phone"] = requestPhone
        params["address"] = requestAddress

        ApiServices.getAbsentServices()
            .updateProfile("Bearer $token", params)
            .enqueue(object : Callback<Wrapper<UpdateProfileResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<UpdateProfileResponse>>,
                    response: Response<Wrapper<UpdateProfileResponse>>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful){
                        val user = response.body()?.data?.employee?.get(0)?.user
                        val employee = response.body()?.data?.employee?.first()
                        if (user != null && employee != null){
                            HawkStorage.instance(this@EditProfileActivity).setUser(user)
                            HawkStorage.instance(this@EditProfileActivity).setEmployee(employee)
                            finish()
                            onBackPressed()
                            Toast.makeText(this@EditProfileActivity, "Update Profile Successfully.", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        MyDialog.dynamicDialog(this@EditProfileActivity, getString(R.string.alert), getString(R.string.something_wrong))
                    }
                }

                override fun onFailure(call: Call<Wrapper<UpdateProfileResponse>>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }
            })

    }

    private fun init() {
        setSupportActionBar(binding.tbEditProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateView()

        //Toggle Button
        if (binding.btnMale.isChecked){
            gender = "Male"
        }else if (binding.btnFemale.isChecked){
            gender = "Female"
        }
    }

    private fun updateView() {
        val employee = HawkStorage.instance(this).getEmployee()
        binding.etFullName.setText(employee.name)
        binding.etEmployeeId.setText(Helpers.employeeIdFormat(employee.employeeId))
        binding.etEmail.setText(employee.email)
        binding.etDob.setText( employee.dob?.toDate()?.formatTo("dd MMMM yyyy"))
        if (employee.gender == "Male"){
            binding.tgGender.check(R.id.btn_male)
        }else{
            binding.tgGender.check(R.id.btn_female)
        }
//        binding.etGender.setText = employee.gender
        binding.etAddress.setText(employee.address)
        binding.etPhone.setText(employee.phone)
        binding.etDoj.setText(employee.doj?.toDate()?.formatTo("dd MMMM yyyy"))
        binding.etDivision.setText(employee.division)
    }


    companion object{
        private val TAG = EditProfileActivity::class.java.simpleName
    }
}
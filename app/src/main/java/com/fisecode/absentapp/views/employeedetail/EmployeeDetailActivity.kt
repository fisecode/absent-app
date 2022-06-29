package com.fisecode.absentapp.views.employeedetail

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityEmployeeDetailBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.UploadPhotoResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.utils.Helpers
import com.fisecode.absentapp.utils.Helpers.formatTo
import com.fisecode.absentapp.utils.Helpers.toDate
import com.fisecode.absentapp.views.signin.SignInActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EmployeeDetailActivity : AppCompatActivity() {

    private var binding: ActivityEmployeeDetailBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        init()
        onClick()
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    Glide.with(this).load(fileUri).placeholder(R.drawable.employee_photo).into(binding!!.ivEmployeePhoto)
                    updatePhoto(fileUri)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onResume() {
        super.onResume()
        updateView()
    }

    private fun onClick() {
        binding?.tbEmployeeDetail?.setNavigationOnClickListener {
            finish()
        }
        binding?.tvChangePhoto?.setOnClickListener {
//            Toast.makeText(this,"CHANGE PHOTO", Toast.LENGTH_SHORT).show()
            ImagePicker.with(this)
                .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        binding?.btnEditProfile?.setOnClickListener {
            startActivity<EditProfileActivity>()
        }

    }

    private fun init() {
        setSupportActionBar(binding?.tbEmployeeDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateView()
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

    private fun updatePhoto(photoPath: Uri) {
        val token = HawkStorage.instance(this).getToken()
        MyDialog.showProgressDialog(this)
        val file = File(photoPath.path)
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file)
        val typeFile = this.contentResolver.getType(uri)
        val mediaTypeFile = typeFile?.toMediaType()
        val requestPhotoFile = file.asRequestBody(mediaTypeFile)
        val multipartBody = MultipartBody.Part.createFormData("photo", file.name, requestPhotoFile)
        ApiServices.getAbsentServices()
            .updatePhoto("Bearer $token", multipartBody)
            .enqueue(object : Callback<Wrapper<UploadPhotoResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<UploadPhotoResponse>>,
                    response: Response<Wrapper<UploadPhotoResponse>>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful){
                        val user = response.body()?.data?.user
                        if (user != null){
                            HawkStorage.instance(this@EmployeeDetailActivity).setUser(user)
                            updateView()
                            Toast.makeText(this@EmployeeDetailActivity, "Change Photo Successfully.", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Toast.makeText(this@EmployeeDetailActivity, "Change Photo Fails.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Wrapper<UploadPhotoResponse>>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

    companion object{
        private val TAG = EmployeeDetailActivity::class.java.simpleName
    }

}
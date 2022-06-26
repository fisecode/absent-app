package com.fisecode.absentapp.views.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivitySigninBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.SignInResponse
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.networking.RetrofitClient
import com.fisecode.absentapp.views.forgotpassword.ForgotPasswordActivity
import com.fisecode.absentapp.views.main.MainActivity
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()
    }

    private fun onClick() {
        binding.btnSignin.setOnClickListener {
            val email = binding.etEmailSignin.text.toString()
            val password = binding.etPasswordSignin.text.toString()
            if (isFormValid(email, password)){
                signInToServer(email, password)
            }
        }

        binding.btnForgotPassword.setOnClickListener {
            startActivity<ForgotPasswordActivity>()
        }
    }

    private fun signInToServer(email: String, password: String) {
        val signInRequest = SignInRequest(email = email, password = password)
        val signInRequestString = Gson().toJson(signInRequest)
        MyDialog.showProgressDialog(this)

        ApiServices.getAbsentServices()
            .signInRequest(signInRequestString)
            .enqueue(object : Callback<SignInResponse> {
                override fun onResponse(
                    call: Call<SignInResponse>,
                    response: Response<SignInResponse>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful){
                        val user = response.body()?.data?.user
                        val token = response.body()?.data?.accessToken
                        val employee = response.body()?.data?.employee
                        if (user != null && token != null && employee != null){
                            HawkStorage.instance(this@SignInActivity).setUser(user)
                            HawkStorage.instance(this@SignInActivity).setToken(token)
                            HawkStorage.instance(this@SignInActivity).setEmployee(employee)
                            goToMain()
                        }
                    }else{
                        val errorCoverter: Converter<ResponseBody, SignInResponse> =
                            RetrofitClient
                                .getClient()
                                .responseBodyConverter(
                                    SignInResponse::class.java,
                                    arrayOfNulls<Annotation>(0)
                                )
                        var errorResponse: SignInResponse?
                        try {
                            response.errorBody()?.let {
                                errorResponse = errorCoverter.convert(it)
                                MyDialog.dynamicDialog(this@SignInActivity,
                                    getString(R.string.failed),
                                    errorResponse?.meta?.message.toString()
                                )
                            }
                        }catch (e: IOException){
                            e.printStackTrace()
                            Log.e(TAG, "Error: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }
            })
    }

    private fun goToMain() {
        startActivity<MainActivity>()
        finishAffinity()
    }

    private fun isFormValid(email: String, password: String): Boolean {
        if (email.isEmpty()){
            binding.etEmailSignin.error = getString(R.string.please_field_your_email)
            binding.etEmailSignin.requestFocus()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmailSignin.error = getString(R.string.please_use_valid_email)
            binding.etEmailSignin.requestFocus()
        }else if (password.isEmpty()){
            binding.etEmailSignin.error = null
            binding.etPasswordSignin.error = getString(R.string.please_field_your_password)
        }else{
            binding.etEmailSignin.error = null
            binding.etPasswordSignin.error = null
            return true
        }
        return false
    }

    companion object{
        private val TAG = SignInActivity::class.java.simpleName
    }
}
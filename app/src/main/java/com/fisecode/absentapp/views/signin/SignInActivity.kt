package com.fisecode.absentapp.views.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivitySigninBinding
import com.fisecode.absentapp.views.forgotpassword.ForgotPasswordActivity
import com.fisecode.absentapp.views.main.MainActivity
import org.jetbrains.anko.startActivity

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
            startActivity<MainActivity>()
        }

        binding.btnForgotPassword.setOnClickListener {
            startActivity<ForgotPasswordActivity>()
        }
    }
}
package com.fisecode.absentapp.views.forgotpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setSupportActionBar(binding.tbForgotPassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
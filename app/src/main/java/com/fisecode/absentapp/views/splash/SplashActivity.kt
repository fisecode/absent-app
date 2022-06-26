package com.fisecode.absentapp.views.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.fisecode.absentapp.R
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.views.main.MainActivity
import com.fisecode.absentapp.views.signin.SignInActivity
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        afterDelayGoToSignIn()
    }

    private fun afterDelayGoToSignIn() {
        Handler(Looper.getMainLooper()).postDelayed({
            checkIsLogin()
        }, 1200)
    }

    private fun checkIsLogin() {
        val isSignIn = HawkStorage.instance(this).isSignIn()
        if (isSignIn){
            startActivity<MainActivity>()
            finishAffinity()
        }else{
            startActivity<SignInActivity>()
            finishAffinity()
        }
    }
}
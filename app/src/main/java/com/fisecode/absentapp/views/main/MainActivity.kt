package com.fisecode.absentapp.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityMainBinding
import com.fisecode.absentapp.views.absent.AbsentFragment
import com.fisecode.absentapp.views.profile.ProfileFragment
import com.fisecode.absentapp.views.timeoff.TimeOffFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.btmNavigationMain.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.action_leave -> {
                    openFragment(TimeOffFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.action_absent -> {
                    openFragment(AbsentFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.action_profile -> {
                    openFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
        openHomeFragment()
    }

    private fun openHomeFragment() {
        binding.btmNavigationMain.selectedItemId = R.id.action_absent
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .addToBackStack(null)
            .commit()
    }
}
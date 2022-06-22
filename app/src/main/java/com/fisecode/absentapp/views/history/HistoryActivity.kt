package com.fisecode.absentapp.views.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fisecode.absentapp.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClick()
    }

    private fun onClick() {
        binding.tbHistory.setNavigationOnClickListener {
            finish()
        }
    }

    private fun init() {
        setSupportActionBar(binding.tbHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
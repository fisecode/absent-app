package com.fisecode.absentapp.utils

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

object Helpers {
    fun employeeIdFormat( number: String?): String {
        val id = String.format("%05d", number?.toInt())
        return "LMS00$id"
    }

    fun String.toDate(dateFormat: String = "yyyy-MM-dd", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date? {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun String.toDateForServer(dateFormat: String = "dd MMM yyyy", timeZone: TimeZone = TimeZone.getDefault()): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)!!
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

    fun getCurrentDateForServer(): String{
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}
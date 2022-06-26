package com.fisecode.absentapp.networking

object ApiServices {
    fun getAbsentServices(): AbsentApiServices{
        return RetrofitClient
            .getClient()
            .create(AbsentApiServices::class.java)
    }
}
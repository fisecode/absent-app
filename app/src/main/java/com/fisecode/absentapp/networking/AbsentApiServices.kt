package com.fisecode.absentapp.networking

import com.fisecode.absentapp.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AbsentApiServices {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("login")
    fun loginRequest(@Body body: String): Call<LoginResponse>
}
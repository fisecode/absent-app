package com.fisecode.absentapp.networking

import com.fisecode.absentapp.model.SignInResponse
import com.fisecode.absentapp.model.SignOutResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AbsentApiServices {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("login")
    fun signInRequest(@Body body: String): Call<SignInResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("logout")
    fun signOutRequest(@Header("Authorization") token: String): Call<SignOutResponse>
}
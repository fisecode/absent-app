package com.fisecode.absentapp.networking

import com.fisecode.absentapp.model.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface AbsentApiServices {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("login")
    fun signInRequest(@Body body: String): Call<Wrapper<SignInResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("logout")
    fun signOutRequest(@Header("Authorization") token: String): Call<SignOutResponse>

    @Headers ("Accept: application/json")
    @GET("user")
    fun getUser(@Header("Authorization") token: String) :Call<Wrapper<GetUserResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user")
    fun updateProfile(@Header("Authorization") token: String,
                      @PartMap params: HashMap<String, RequestBody>
    ): Call<Wrapper<UpdateProfileResponse>>
}
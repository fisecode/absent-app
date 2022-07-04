package com.fisecode.absentapp.networking

import com.fisecode.absentapp.model.*
import okhttp3.MultipartBody
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

    @Multipart
    @Headers("Accept: application/json")
    @POST("user")
    fun updateProfile(@Header("Authorization") token: String,
                      @PartMap params: HashMap<String, RequestBody>
    ): Call<Wrapper<UpdateProfileResponse>>

    @Multipart
    @Headers("Accept: application/json")
    @POST("user/photo")
    fun updatePhoto(@Header("Authorization") token: String,
                      @Part photo: MultipartBody.Part
    ): Call<Wrapper<UploadPhotoResponse>>

    @Headers ("Accept: application/json")
    @GET("leave/types")
    fun leaveType(@Header("Authorization") token: String) :Call<Wrapper<LeaveTypeResponse>>

    @Multipart
    @Headers("Accept: application/json")
    @POST("leave")
    fun leave(@Header("Authorization") token: String,
                      @PartMap params: HashMap<String, RequestBody>
    ): Call<Wrapper<LeaveResponse>>

    @Headers ("Accept: application/json")
    @GET("leave/history")
    fun leaveHistory(@Header("Authorization") token: String) :Call<Wrapper<LeaveHistoryResponse>>

    @Multipart
    @Headers("Accept: application/json")
    @POST("absent/spot")
    fun absentSpot(@Header("Authorization") token: String,
                      @PartMap params: HashMap<String, RequestBody>
    ): Call<Wrapper<AbsentSpotResponse>>

    @Headers("Accept: application/json")
    @GET("absent/spot")
    fun getAbsentSpot(@Header("Authorization") token: String): Call<Wrapper<AbsentSpotResponse>>

    @Multipart
    @Headers("Accept: application/json")
    @POST("absent")
    fun absentIn(@Header("Authorization") token: String,
               @PartMap params: HashMap<String, RequestBody>,
               @Part photo: MultipartBody.Part
    ): Call<Wrapper<AbsentResponse>>

    @Multipart
    @Headers("Accept: application/json")
    @POST("absent")
    fun absentOut(@Header("Authorization") token: String,
               @PartMap params: HashMap<String, RequestBody>
    ): Call<Wrapper<AbsentResponse>>

    @Headers("Accept: application/json")
    @GET("absent/history")
    fun getHistoryAbsent(@Header("Authorization") token: String,
                             @Query("from") fromDate: String,
                             @Query("to") toDate: String
    ): Call<Wrapper<AbsentHistoryResponse>>
}
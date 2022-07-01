package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class AbsentSpot(

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("name_spot")
    val nameSpot: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class AbsentHistory(

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("absent_spot")
    val absentSpot: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("check_in")
    val checkIn: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("photo")
    val photo: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("check_out")
    val checkOut: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("employee_id")
    val employeeId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("status")
    val status: Any? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null
)
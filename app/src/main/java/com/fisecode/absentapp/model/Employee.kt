package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName


data class Employee(

    @field:SerializedName("work_from")
    val workFrom: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("division")
    val division: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("dob")
    val dob: String? = null,

    @field:SerializedName("employee_id")
    val employeeId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("doj")
    val doj: String? = null
)
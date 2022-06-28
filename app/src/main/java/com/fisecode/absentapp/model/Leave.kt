package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

class Leave {
    @field:SerializedName("code")
    val code: Int? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: String? = null
}
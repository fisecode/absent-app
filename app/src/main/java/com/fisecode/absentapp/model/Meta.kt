package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName


data class Meta(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)
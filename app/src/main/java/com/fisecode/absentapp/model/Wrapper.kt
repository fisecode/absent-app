package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

class Wrapper<T> {

    @SerializedName("data")
    val data: T?= null

    @field:SerializedName("meta")
    val meta: Meta? = null
}
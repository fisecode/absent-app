package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(

	@field:SerializedName("data")
	val data: List<Any?>? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)

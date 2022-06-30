package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class SignOutResponse(

	@field:SerializedName("data")
	val data: Boolean? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)


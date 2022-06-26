package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class SignOutResponse(

	@field:SerializedName("data")
	val data: Boolean? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)

data class MetaSignOut(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

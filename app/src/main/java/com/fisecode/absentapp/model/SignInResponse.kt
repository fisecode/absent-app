package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class SignInResponse(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("employee")
	val employee: Employee? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("message")
	val message: String? = null

)


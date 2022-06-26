package com.fisecode.absentapp.views.signin

import com.google.gson.annotations.SerializedName

data class SignInRequest(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class GetUserResponse(

	@field:SerializedName("employee")
	val employee: List<Employee?>? = null
)

package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(

	@field:SerializedName("employee")
	val employee: List<Employee?>? = null
)



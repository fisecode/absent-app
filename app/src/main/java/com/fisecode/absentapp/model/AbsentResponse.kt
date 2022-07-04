package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class AbsentResponse(

	@field:SerializedName("absent")
	val absent: AbsentHistory? = null
)


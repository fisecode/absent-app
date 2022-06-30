package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class LeaveResponse(

	@field:SerializedName("leave")
	val leave: Leave? = null
)

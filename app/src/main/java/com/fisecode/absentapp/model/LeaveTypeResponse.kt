package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class LeaveTypeResponse(

	@field:SerializedName("leaveType")
	val leaveType: List<LeaveType>? = null
)

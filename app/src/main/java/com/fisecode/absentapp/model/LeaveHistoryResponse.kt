package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class LeaveHistoryResponse(

	@field:SerializedName("leave_history")
	val leaveHistory: List<LeaveHistory?>? = null

)

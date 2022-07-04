package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class AbsentHistoryResponse(

	@field:SerializedName("absent")
	val absent: List<AbsentHistory>? = null
)


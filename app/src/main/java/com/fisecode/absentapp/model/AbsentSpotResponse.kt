package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class AbsentSpotResponse(

	@field:SerializedName("absent_spot")
	val absentSpot: AbsentSpot
)


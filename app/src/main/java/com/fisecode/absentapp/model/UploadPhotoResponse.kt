package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class UploadPhotoResponse(

	@field:SerializedName("user")
	val user: User? = null
)


package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class User(

    @field:SerializedName("profile_photo_url")
    val profilePhotoUrl: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("timezone")
    val timezone: Any? = null,

    @field:SerializedName("roles")
    val roles: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("photo")
    val photo: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("email_verified_at")
    val emailVerifiedAt: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("two_factor_confirmed_at")
    val twoFactorConfirmedAt: Any? = null,

    @field:SerializedName("email")
    val email: String? = null
)

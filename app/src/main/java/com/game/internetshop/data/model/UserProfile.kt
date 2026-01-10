package com.game.internetshop.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile (
    @SerialName("email")
    val email: String,
    @SerialName("user_name")
    val userName: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("role_id")
    val roleId: Int,
    @SerialName("auth_uid")
    val authUid: String
)
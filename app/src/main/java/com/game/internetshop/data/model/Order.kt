package com.game.internetshop.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order (
    @SerialName("registration_date")
    val registrationDate: Instant,
    @SerialName("total_price")
    val totalPrice: Float,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("status_id")
    val statusId: Int,
    @SerialName("payment_id")
    val paymentId: Int
)
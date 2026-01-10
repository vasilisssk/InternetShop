package com.game.internetshop.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductInOrder(
    @SerialName("product_id")
    val productId: Int,
    @SerialName("order_id")
    val orderId: Int,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("registration_price")
    val registrationPrice: Float
)

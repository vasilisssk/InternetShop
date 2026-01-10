package com.game.internetshop.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductInCart(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("product_id")
    val productId: Int,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("addition_price")
    val additionPrice: Float
) {
    fun totalSum(): Float {
        return additionPrice * quantity
    }
}

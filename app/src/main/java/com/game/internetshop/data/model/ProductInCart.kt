package com.game.internetshop.data.model

data class ProductInCart(
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val additionPrice: Float
) {
    fun totalSum(): Float {
        return additionPrice * quantity
    }
}

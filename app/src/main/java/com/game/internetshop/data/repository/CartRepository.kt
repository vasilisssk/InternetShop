package com.game.internetshop.data.repository

import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.common.Result

interface CartRepository {
    suspend fun addToCart(userId: Int, productId: Int): Result<Unit>
    suspend fun removeFromCart(userId: Int, productId: Int, removeAll: Boolean = false): Result<Unit>
    suspend fun getCartItems(userId: Int): Result<List<ProductInCart>>
    suspend fun clearCart(userId: Int): Result<Unit>
}
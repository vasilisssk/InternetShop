package com.game.internetshop.data.repository

import com.game.internetshop.data.model.CartResult
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductInCart

interface CartRepository {
    suspend fun addToCart(userId: Int, productId: Int): CartResult<Unit>
    suspend fun removeFromCart(userId: Int, productId: Int, removeAll: Boolean = false): CartResult<Unit>
    suspend fun getCartItems(userId: Int): CartResult<List<ProductInCart>>
    suspend fun clearCart(userId: Int): CartResult<Unit>
}
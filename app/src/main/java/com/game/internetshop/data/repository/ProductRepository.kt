package com.game.internetshop.data.repository

import com.game.internetshop.data.model.Product
import com.game.internetshop.data.common.Result

interface ProductRepository {
    suspend fun getProductById(productId: Int): Result<Product>
    suspend fun getAllProducts(): Result<List<Product>>
}
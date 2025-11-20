package com.game.internetshop.data.repository

import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductResult

interface ProductRepository {
    suspend fun getProductById(productId: Int): ProductResult<Product>
    suspend fun getAllProducts(): ProductResult<List<Product>>
}
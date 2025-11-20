package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductResult
import com.game.internetshop.data.repository.ProductRepository

class GetAllProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(): ProductResult<List<Product>> {
        return productRepository.getAllProducts()
    }
}
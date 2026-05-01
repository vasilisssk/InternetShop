package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.repository.ProductRepository

class GetAllProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(): Result<List<Product>> {
        return productRepository.getAllProducts()
    }
}
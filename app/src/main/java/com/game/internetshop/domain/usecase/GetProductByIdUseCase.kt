package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductResult
import com.game.internetshop.data.repository.ProductRepository

class GetProductByIdUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: Int): ProductResult<Product> {
        return productRepository.getProductById(productId)
    }
}
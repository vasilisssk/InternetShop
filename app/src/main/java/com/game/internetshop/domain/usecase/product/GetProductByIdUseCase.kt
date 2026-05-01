package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Product
import com.game.internetshop.data.common.Result
import com.game.internetshop.data.repository.ProductRepository

class GetProductByIdUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: Int): Result<Product> {
        return productRepository.getProductById(productId)
    }
}
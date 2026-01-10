package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.CartResult
import com.game.internetshop.data.repository.CartRepository

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(userId: Int, productId: Int): CartResult<Unit> {
        return cartRepository.addToCart(userId, productId)
    }
}
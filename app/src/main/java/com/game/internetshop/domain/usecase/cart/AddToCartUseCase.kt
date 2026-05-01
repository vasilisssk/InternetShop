package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.repository.CartRepository

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(userId: Int, productId: Int): Result<Unit> {
        return cartRepository.addToCart(userId, productId)
    }
}
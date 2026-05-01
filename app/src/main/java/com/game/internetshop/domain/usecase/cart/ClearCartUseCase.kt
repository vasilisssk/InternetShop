package com.game.internetshop.domain.usecase

import com.game.internetshop.data.repository.CartRepository

class ClearCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(userId: Int) {
        cartRepository.clearCart(userId)
    }
}
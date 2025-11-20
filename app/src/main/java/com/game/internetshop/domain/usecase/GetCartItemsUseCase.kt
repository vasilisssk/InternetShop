package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.CartResult
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.repository.CartRepository

class GetCartItemsUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(userId: Int): CartResult<List<ProductInCart>> {
        return cartRepository.getCartItems(userId)
    }
}
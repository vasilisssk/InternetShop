package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.repository.CartRepository

class GetCartItemsUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(userId: Int): Result<List<ProductInCart>> {
        return cartRepository.getCartItems(userId)
    }
}
package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.OrderInsert
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.repository.OrderRepository

class CreateNewOrderUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(userId: Int, productsList: List<ProductInCart>, paymentVariant: Int): Result<OrderInsert> {
        return orderRepository.createNewOrder(userId, productsList, paymentVariant)
    }
}
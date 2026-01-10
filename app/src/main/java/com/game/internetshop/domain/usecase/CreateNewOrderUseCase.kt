package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.OrderResult
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.repository.OrderRepository

class CreateNewOrderUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(userId: Int, productsList: List<ProductInCart>, paymentVariant: Int): OrderResult<Order> {
        return orderRepository.createNewOrder(userId, productsList, paymentVariant)
    }
}
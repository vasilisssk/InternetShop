package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.common.Result
import com.game.internetshop.data.repository.OrderRepository

class GetAllUserOrdersUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(userId: Int): Result<List<Order>> {
        return orderRepository.getAllUserOrders(userId)
    }
}
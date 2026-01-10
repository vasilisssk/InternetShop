package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.OrderOnlyRead
import com.game.internetshop.data.model.OrderResult
import com.game.internetshop.data.repository.OrderRepository

class GetAllUserOrdersUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(userId: Int): OrderResult<List<OrderOnlyRead>> {
        return orderRepository.getAllUserOrders(userId)
    }
}
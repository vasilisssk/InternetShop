package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductInOrder
import com.game.internetshop.data.repository.OrderRepository

class GetAllProductsInOrderUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: Int): Result<List<ProductInOrder>> {
        return orderRepository.getAllProductsInOrder(orderId)
    }
}
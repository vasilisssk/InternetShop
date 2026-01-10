package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.OrderResult
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductInOrder
import com.game.internetshop.data.repository.OrderRepository

class GetAllProductsInOrderUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: Int): OrderResult<List<ProductInOrder>> {
        return orderRepository.getAllProductsInOrder(orderId)
    }
}
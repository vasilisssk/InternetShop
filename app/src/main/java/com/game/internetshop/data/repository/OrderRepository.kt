package com.game.internetshop.data.repository

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.OrderOnlyRead
import com.game.internetshop.data.model.OrderResult
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductInOrder

interface OrderRepository {
    suspend fun createNewOrder(userId: Int, productsList: List<ProductInCart>, paymentVariant: Int): OrderResult<Order>
    suspend fun getAllUserOrders(userId: Int): OrderResult<List<OrderOnlyRead>>
    suspend fun getAllProductsInOrder(orderId: Int): OrderResult<List<ProductInOrder>>
}
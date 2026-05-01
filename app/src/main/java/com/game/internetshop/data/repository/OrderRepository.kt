package com.game.internetshop.data.repository

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.OrderInsert
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductInOrder

interface OrderRepository {
    suspend fun createNewOrder(userId: Int, productsList: List<ProductInCart>, paymentVariant: Int): Result<OrderInsert>
    suspend fun getAllUserOrders(userId: Int): Result<List<Order>>
    suspend fun getAllProductsInOrder(orderId: Int): Result<List<ProductInOrder>>
}
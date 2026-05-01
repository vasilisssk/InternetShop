package com.game.internetshop.views.base.orders

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductInOrder

data class OrdersUiItem (
    val order: Order,
    val productsInOrder: List<ProductInOrder>,
    val products: List<Product>
)
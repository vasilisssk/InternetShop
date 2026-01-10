package com.game.internetshop.ui.orders

import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.OrderOnlyRead
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductInOrder

data class OrdersUiItem (
    val order: OrderOnlyRead,
    val productsInOrder: List<ProductInOrder>,
    val products: List<Product>
)
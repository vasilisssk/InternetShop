package com.game.internetshop.views.base.catalogue

import com.game.internetshop.data.model.Product

data class CatalogueUiItem(
    val product: Product,
    val quantityInCart: Int
)
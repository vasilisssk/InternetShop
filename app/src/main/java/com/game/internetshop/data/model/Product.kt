package com.game.internetshop.data.model

data class Product(
    val id: Int,
    val name: String,
    val price: Float,
    val imageUrl: String? = null,
    val imageRes: Int? = null
)

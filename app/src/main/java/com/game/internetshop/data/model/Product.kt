package com.game.internetshop.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("product_id")
    val id: Int,
    @SerialName("product_name")
    val name: String,
    @SerialName("price")
    val price: Float,
    @SerialName("brand")
    val brand: String
)

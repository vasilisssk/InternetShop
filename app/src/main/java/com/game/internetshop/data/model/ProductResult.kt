package com.game.internetshop.data.model

sealed class ProductResult<out T> {
    data class Success<T>(val data: T): ProductResult<T>()
    data class Error(val message: String) : ProductResult<Nothing>()
    object Loading: ProductResult<Nothing>()
}
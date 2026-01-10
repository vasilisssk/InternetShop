package com.game.internetshop.data.model

sealed class OrderResult<out T> {
    data class Success<T>(val data: T) : OrderResult<T>()
    data class Error(val message: String) : OrderResult<Nothing>()
    object Loading: OrderResult<Nothing>()
}
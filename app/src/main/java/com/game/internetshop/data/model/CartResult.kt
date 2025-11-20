package com.game.internetshop.data.model

sealed class CartResult<out T> {
    data class Success<T>(val data: T): CartResult<T>()
    data class Error(val message: String) : CartResult<Nothing>()
    object Loading: CartResult<Nothing>()
}
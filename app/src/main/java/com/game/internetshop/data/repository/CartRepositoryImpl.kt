package com.game.internetshop.data.repository

import com.game.internetshop.data.model.CartResult
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductResult

class CartRepositoryImpl(
    private val productRepository: ProductRepository
): CartRepository {

    // заглушка, пусть id пользователя не учитывается <productId, quantity>
    private val cartItems = mutableMapOf<Int, Int>()

    override suspend fun addToCart(userId: Int, productId: Int, quantity: Int): CartResult<Unit> {
        return try {
            // Проверяем существует ли продукт
            val productResult = productRepository.getProductById(productId)
            if (productResult is ProductResult.Success) {
                val currentQuantity = cartItems[productId] ?: 0
                cartItems[productId] = currentQuantity + quantity
                CartResult.Success(Unit)
            } else {
                CartResult.Error("Product with id=${productId}")
            }

        } catch (e: Exception) {
            CartResult.Error("Failed adding to cart: ${e.message}")
        }
    }

    override suspend fun removeFromCart(userId: Int, productId: Int): CartResult<Unit> {
        return try {
            val currentQuantity = cartItems[productId] ?: 0
            if (currentQuantity <= 1) {
                cartItems.remove(productId)
            } else {
                cartItems[productId] = currentQuantity - 1
            }
            CartResult.Success(Unit)
        } catch (e: Exception) {
            CartResult.Error("Failed removing from cart: ${e.message}")
        }
    }

    override suspend fun getCartItems(userId: Int): CartResult<List<ProductInCart>> {
        return try {
            val items = cartItems.mapNotNull { (productId, quantity) ->
                // получаем актуальную информацию о товаре
                when (val result = productRepository.getProductById(productId)) {
                    is ProductResult.Success -> {
                        val product = result.data
                        ProductInCart(
                            userId = userId,
                            productId = productId,
                            quantity = quantity,
                            additionPrice = product.price
                        )
                    }
                    else -> null
                }
            }
            CartResult.Success(items)
        } catch (e: Exception) {
            CartResult.Error("Failed getting cart items: ${e.message}")
        }
    }

    override suspend fun clearCart(userId: Int): CartResult<Unit> {
        return try {
            cartItems.clear()
            CartResult.Success(Unit)
        } catch (e: Exception) {
            CartResult.Error("Failed clearing cart: ${e.message}")
        }
    }
}
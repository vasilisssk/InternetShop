package com.game.internetshop.data.repository

import android.util.Log
import com.game.internetshop.data.model.CartResult
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class CartRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val productRepository: ProductRepository
) : CartRepository {

    // заглушка, пусть id пользователя не учитывается <productId, quantity>
    private val cartItems = mutableMapOf<Int, Int>()

    override suspend fun addToCart(userId: Int, productId: Int): CartResult<Unit> {
        return try {
            // Проверяем существует ли продукт
            Log.w("supabase_addtocart", "Start adding to cart")
            val productResult = productRepository.getProductById(productId)
            if (productResult is ProductResult.Success) {
                Log.w("supabase_addtocart", "Before working with cart items")
                val quantity = getProductQuantityInCart(userId, productId)
                Log.w("supabase_addtocart", "After getting quantity: $quantity")

                val addition_price = getProductPrice(productId)

                if (addition_price == null) {
                    return CartResult.Error("Failed adding to cart")
                }
                Log.w("supabase_addtocart", "After getting price: $addition_price")

                when (quantity) {
                    // продукта нет в корзине, поэтому мы должны вствить новую запись
                    0 -> {
                        Log.w("supabase_addtocart", "Before inserting new product")
                        supabaseClient.postgrest
                            .from("product_in_cart")
                            .insert(ProductInCart(userId, productId, 1, addition_price))
                        Log.w("supabase_addtocart", "After inserting new product")
                    }
                    // если продукт есть в корзине, то мы просто должны обновить информацию
                    else -> {
                        Log.w("supabase_addtocart", "Before updating(+1) existing product")
                        changeQuantityInCart(userId, productId, quantity,  addition_price, 1)
                        Log.w("supabase_addtocart", "After updating(+1) existing product")
                    }
                }

                CartResult.Success(Unit)
            } else {
                CartResult.Error("Failed adding to cart")
            }
        } catch (e: Exception) {
            Log.e("supabse_addtocart", e.message.toString())
            CartResult.Error("Failed adding to cart")
        }
    }

    override suspend fun removeFromCart(userId: Int, productId: Int, removeAll: Boolean): CartResult<Unit> {
        return try {
            Log.w("supabase_removefromcart","Start removing from cart")
            val quantity = getProductQuantityInCart(userId, productId) // никогда не будет 0

            val addition_price = getProductPrice(productId)
            if (addition_price == null) {
                return CartResult.Error("Failed removing from cart")
            }
            Log.w("supabase_addtocart", "After getting price: $addition_price")

            if (quantity == 1 || removeAll) { // если количнство == 1, то удаляем товар из корзины или передан флан полного удаления
                Log.w("supabase_removefromcart","Before deleting from cart")
                supabaseClient.postgrest
                    .from("product_in_cart")
                    .delete {
                        filter {
                            eq("user_id", userId)
                            eq("product_id", productId)
                        }
                    }
                Log.w("supabase_removefromcart","After deleting from cart")
            } else {
                Log.w("supabase_removefromcart","Before updating(-1) existing product")
                changeQuantityInCart(userId, productId, quantity, addition_price, -1)
                Log.w("supabase_removefromcart","After updating(-1) existing product")
            }
            CartResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("supabase_removefromcart",e.message.toString())
            CartResult.Error("Failed removing from cart")
        }
    }

    override suspend fun getCartItems(userId: Int): CartResult<List<ProductInCart>> {
        return try {
            val userProductsInCart = getAllUserProducts(userId)
            CartResult.Success(userProductsInCart)
        } catch (e: Exception) {
            Log.e("supabase_getcartitems",e.message.toString())
            CartResult.Error("Failed getting cart items")
        }
    }

    override suspend fun clearCart(userId: Int): CartResult<Unit> {
        return try {
            Log.w("supabase_clearcart", "Start clearing cart")
            supabaseClient.postgrest
                .from("product_in_cart")
                .delete {
                    filter {
                        eq("user_id", userId)
                    }
                }
            Log.w("supabase_clearcart", "After clearing cart")
            CartResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("supabase_clearcart", e.message.toString())
            CartResult.Error("Failed clearing cart")
        }
    }

    private suspend fun getAllUserProducts(userId: Int): List<ProductInCart> {
        return try {
            Log.w("supabase_getalluserproducts", "Before getting all user products with id $userId")
            val userProductsInCart = supabaseClient.postgrest
                .from("product_in_cart")
                .select(Columns.list("user_id", "product_id", "quantity", "addition_price")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<ProductInCart>()
            Log.w("supabase_getalluserproducts", "After getting all user products, UserProductInCart: $userProductsInCart")
            userProductsInCart
        } catch (e: Exception) {
            Log.e("supabase_getalluserproducts", e.message.toString())
            emptyList()
        }
    }

    private suspend fun getProductQuantityInCart(userId: Int, productId: Int): Int {
        val quantity = supabaseClient.postgrest
            .from("product_in_cart")
            .select(Columns.list("quantity")) {
                filter {
                    eq("user_id", userId)
                    eq("product_id", productId)
                }
            }
            .decodeList<Map<String, Int>>()
            .firstOrNull()
            ?.get("quantity") ?: 0
        return quantity
    }

    private suspend fun getProductPrice(productId: Int): Float? {
        val price = supabaseClient.postgrest
            .from("product")
            .select(Columns.list("price")) {
                filter {
                    eq("product_id", productId)
                }
            }
            .decodeList<Map<String, Float>>()
            .firstOrNull()
            ?.get("price")
        return price
    }

    private suspend fun changeQuantityInCart(userId: Int, productId: Int, quantity: Int, additionPrice: Float, change: Int) {
        supabaseClient.postgrest
            .from("product_in_cart")
            .update(
                {
                    set("quantity", quantity+change)
                    set("addition_price", additionPrice)
                }
            ) {
                filter {
                    eq("user_id", userId)
                    eq("product_id", productId)
                }
            }
    }
}
package com.game.internetshop.data.repository

import android.util.Log
import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductInOrder
import com.game.internetshop.data.common.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class OrderRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val cartRepository: CartRepository
) : OrderRepository {
    /**
     * 1. Создать заказ
     * 2. Создать записи в product_in_order
     * 3. Удалить записи из product_in_cart
     */
    override suspend fun createNewOrder(userId: Int, productsList: List<ProductInCart>, paymentVariant: Int): Result<Order> {
        return try {
            var totalPrice = 0f
            for (productInCart in productsList) {
                totalPrice += productInCart.quantity * productInCart.additionPrice
            }
            Log.w("supabse_createneworder", "1 Start and totalPrice: ${totalPrice}")

            val localDateTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            val localDateTimeWithoutNanos = LocalDateTime(
                year = localDateTime.year,
                month = localDateTime.month,
                dayOfMonth = localDateTime.dayOfMonth,
                hour = localDateTime.hour,
                minute = localDateTime.minute,
                second = localDateTime.second,
                nanosecond = 0
            )

            val order = Order(registrationDate = localDateTimeWithoutNanos, totalPrice = totalPrice, userId = userId, statusId = 1, paymentId = paymentVariant)
            val insertedOrderId = supabaseClient.postgrest
                .from("orders")
                .insert(order) {
                    select(Columns.list("order_id"))
                }
                .decodeList<Map<String, Int>>()
                .firstOrNull()
                ?.get("order_id")
            Log.w("supabse_createneworder", "1 After inserting and getting new order id: $insertedOrderId")

            if (insertedOrderId == null) {
                return Result.Error("Failed creating new order")
            }

            insertNewProductsInOrder(orderId = insertedOrderId, productsInCartList = productsList)

            Log.w("supabse_createneworder", "3 Before deleting products from cart")
            for (productInCart in productsList) {
                cartRepository.removeFromCart(userId, productInCart.productId, removeAll = true)
            }
            Log.e("supabse_createneworder", "3 After deleting products from cart. End")
            Result.Success(order)
        } catch(e: Exception) {
            Log.e("supabse_createneworder", e.message.toString())
            Result.Error("Failed creating new order")
        }
    }

    override suspend fun getAllUserOrders(userId: Int): Result<List<Order>> {
        return try {
            Log.w("supabase_getalluserorders", "Start")
            val ordersList = supabaseClient.postgrest
                .from("orders")
                .select(Columns.list(/*"registration_date", "total_price", "user_id", "status_id", "payment_id"*/)) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Order>()
            Log.w("supabase_getalluserorders", "After sql query: ${ordersList.size}")
            Result.Success(ordersList)
        } catch (e: Exception) {
            Log.e("supabase_getalluserorders", e.message.toString())
            Result.Error("Failed getting all user orders")
        }
    }

    // для фрагмента с заказами
    override suspend fun getAllProductsInOrder(orderId: Int): Result<List<ProductInOrder>> {
        return try {
            Log.w("supabase_getallproductsinorder", "Start")
            val productsInOrderList = supabaseClient.postgrest
                .from("product_in_order")
                .select(Columns.list("product_id", "order_id", "quantity", "registration_price")) {
                    filter {
                        eq("order_id", orderId)
                    }
                }.decodeList<ProductInOrder>()
            Log.w("supabase_getallproductsinorder", "After getting all products in order: ${productsInOrderList.size}")
            Result.Success(productsInOrderList)
        } catch (e: Exception) {
            Log.e("supabase_getallproductsinorder", e.message.toString())
            Result.Error("Failed getting all products in order")
        }
    }

    // вряд ли пригодится где-то ещё, так что приватная функция
    private suspend fun insertNewProductsInOrder(orderId: Int, productsInCartList: List<ProductInCart>) {
        try {
            Log.w("supabase_insertnewproductsinorder", "2 Start")
            val productsInOrderList = mutableListOf<ProductInOrder>()
            for (productInCart in productsInCartList) {
                val newProductInOrder = ProductInOrder(
                    productId = productInCart.productId,
                    orderId = orderId,
                    quantity = productInCart.quantity,
                    registrationPrice = productInCart.additionPrice)
                productsInOrderList.add(newProductInOrder)
            }
            Log.w("supabase_insertnewproductsinorder", "2 After creating new list of products in order: $productsInOrderList")

            Log.w("supabase_insertnewproductsinorder", "2 Before inserting new products in order")
            supabaseClient.postgrest
                .from("product_in_order")
                .insert(productsInOrderList)
            Log.w("supabase_insertnewproductsinorder", "2 After inserting new products in order")
        } catch (e: Exception) {
            Log.e("supabase_insertnewproductsinorder", "2 "+e.message.toString())
        }
    }
}
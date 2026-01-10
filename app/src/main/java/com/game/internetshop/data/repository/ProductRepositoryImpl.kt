package com.game.internetshop.data.repository

import android.util.Log
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class ProductRepositoryImpl(
    private val supabaseClient: SupabaseClient
): ProductRepository {

    override suspend fun getProductById(productId: Int): ProductResult<Product> {
        return try {
            Log.w("supabase_getproductbyid", "Before getting product from supabase")
            val product = supabaseClient.postgrest
                .from("product")
                .select {
                    filter {
                        eq("product_id", productId)
                    }
                }.decodeSingle<Product>()
            Log.w("supabase_getproductbyid", "After getting product from supabase: ${product.id} }} ${product.name} || ${product.price} || ${product.brand}")
            ProductResult.Success(product)
        } catch (e: Exception) {
            Log.e("supabase_getproductbyid", e.message.toString())
            ProductResult.Error("Failed getting product by id")
        }
    }

    override suspend fun getAllProducts(): ProductResult<List<Product>> {
        return try {
            Log.w("supabase_getallproducts", "Before getting all products")
            val products = supabaseClient.postgrest
                .from("product")
                .select()
                .decodeList<Product>()
            Log.w("supabase_getallproducts", "After getting all products: ${products.size}}")
            ProductResult.Success(products)
        } catch (e: Exception) {
            Log.w("supabase_getallproducts", e.message.toString())
            ProductResult.Error("Failed getting all products")
        }
    }
}
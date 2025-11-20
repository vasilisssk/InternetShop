package com.game.internetshop.data.repository

import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductResult

class ProductRepositoryImpl: ProductRepository {

    // ЗАГЛУШКА <productId, Product>
    private val productsMap = mutableMapOf<Int,Product>().apply {
        put(1, Product(1, "Смартфон Samsung Galaxy S21", 50000f))
        put(2, Product(2, "Смартфон Samsung Galaxy S22", 60000f))
        put(3, Product(3, "Смартфон Samsung Galaxy S23", 70000f))
        put(4, Product(4, "Смартфон Samsung Galaxy S24", 80000f))
        put(5, Product(5, "Смартфон Samsung Galaxy S25", 90000f))
        put(6, Product(6, "Наушники Xiaomi Redmi Buds 4 Lite", 2000f))
        put(7, Product(7, "Наушники Xiaomi Redmi Buds 5 Lite", 3000f))
        put(8, Product(8, "Наушники Xiaomi Redmi Buds 6 Lite", 4000f))
        put(9, Product(9, "Наушники Xiaomi Redmi Buds 4", 3500f))
        put(10, Product(10, "Наушники Xiaomi Redmi Buds 5", 4500f))
        put(11, Product(11, "Наушники Xiaomi Redmi Buds 6", 5500f))
        put(12, Product(12, "Наушники Xiaomi Redmi Buds 4 Pro", 6000f))
        put(13, Product(13, "Наушники Xiaomi Redmi Buds 5 Pro", 7000f))
        put(14, Product(14, "Наушники Xiaomi Redmi Buds 6 Pro", 8000f))
        put(15, Product(15, "Наушники Red Square RS319 чёрный", 7500f))
        put(16, Product(16, "Наушники Red Square RS319 белый", 7500f))
        put(17, Product(17, "Наушники Red Square RS319 бирюзовый", 7500f))
        put(18, Product(18, "Наушники Red Square RS319 розовый", 7500f))
        put(19, Product(19, "Наушники Red Square RS319 зелёный", 7500f))
        put(20, Product(20, "Наушники Red Square RS319 красный", 7500f))
    }

    override suspend fun getProductById(productId: Int): ProductResult<Product> {
        return try {
            val product = productsMap[productId]
            if (product != null) {
                ProductResult.Success(product)
            } else {
                ProductResult.Error("Товар с id=${productId} не найден")
            }
        } catch (e: Exception) {
            ProductResult.Error("Failed to get products: ${e.message}")
        }
    }

    override suspend fun getAllProducts(): ProductResult<List<Product>> {
        return try {
            ProductResult.Success(productsMap.values.toList())
        } catch (e: Exception) {
            ProductResult.Error("Failed to get all products: ${e.message}")
        }
    }
}
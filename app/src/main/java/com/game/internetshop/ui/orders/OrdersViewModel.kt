package com.game.internetshop.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.Order
import com.game.internetshop.data.model.OrderOnlyRead
import com.game.internetshop.data.model.OrderResult
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductInOrder
import com.game.internetshop.data.model.ProductResult
import com.game.internetshop.domain.usecase.GetAllProductsInOrderUseCase
import com.game.internetshop.domain.usecase.GetAllUserOrdersUseCase
import com.game.internetshop.domain.usecase.GetCurrentUserIdUseCase
import com.game.internetshop.domain.usecase.GetProductByIdUseCase
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getAllUserOrdersUseCase: GetAllUserOrdersUseCase,
    private val getAllProductsInOrderUseCase: GetAllProductsInOrderUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase
): ViewModel() {
    private val _uiState = MutableLiveData(OrdersUiState()) // состояние UI
    val uiState: LiveData<OrdersUiState> = _uiState
    private var userOrders: List<OrderOnlyRead> = mutableListOf()
    private var productsInOrder: List<ProductInOrder> = mutableListOf()
    private var productsInfo: MutableList<Product> = mutableListOf()
    private var ordersUiItems: MutableList<OrdersUiItem> = mutableListOf()
    private var currentUserId: Int = 0

    init {
        loadUserAndHisOrders()
    }

    data class OrdersUiState(
        val ordersUiItems: List<OrdersUiItem> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val isInitialized: Boolean = false
    )

    private fun loadUserAndHisOrders() {
        var ordersUiItemsTemp: MutableList<OrdersUiItem> = mutableListOf()
        viewModelScope.launch {
            val userIdResult = getCurrentUserIdUseCase.invoke()
            when (userIdResult) {
                is AuthResult.Success -> {
                    currentUserId = userIdResult.data
                    val ordersResult = getAllUserOrdersUseCase(currentUserId)
                    when (ordersResult) {
                        is OrderResult.Success -> {
                            userOrders = ordersResult.data
                            for (order in userOrders) {
                                val productsInOrderResult = getAllProductsInOrderUseCase(order.orderId)
                                when (productsInOrderResult) {
                                    is OrderResult.Success -> {
                                        productsInOrder = productsInOrderResult.data
                                        for (product in productsInOrder) {
                                            val productInfoResult = getProductByIdUseCase(product.productId)
                                            when (productInfoResult) {
                                                is ProductResult.Success -> {
                                                    productsInfo.add(productInfoResult.data)
                                                }
                                                is ProductResult.Error -> _uiState.value = _uiState.value?.copy(errorMessage = productInfoResult.message, isLoading = false)
                                                is ProductResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
                                            }
                                        }
                                        val orderUiItem = OrdersUiItem(order, productsInOrder, productsInfo)
                                        ordersUiItemsTemp.add(orderUiItem)
                                    }
                                    is OrderResult.Error -> _uiState.value = _uiState.value?.copy(errorMessage = productsInOrderResult.message, isLoading = false)
                                    is OrderResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
                                }
                            }
                            ordersUiItems.addAll(ordersUiItemsTemp)
                            ordersUiItems.sortBy { it.order.orderId }
                            formOrdersUiItemsList()
                        }
                        is OrderResult.Error -> _uiState.value = _uiState.value?.copy(errorMessage = ordersResult.message, isLoading = false)
                        is OrderResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
                    }
                }
                is AuthResult.Error -> _uiState.value = _uiState.value?.copy(errorMessage = userIdResult.message, isLoading = false)
                is AuthResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    private fun formOrdersUiItemsList() {
        _uiState.value = _uiState.value?.copy(
            ordersUiItems = ordersUiItems,
            isInitialized = true
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}
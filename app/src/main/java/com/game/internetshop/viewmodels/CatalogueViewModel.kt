package com.game.internetshop.viewmodels

import CartRealtimeService
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.domain.usecase.AddToCartUseCase
import com.game.internetshop.domain.usecase.GetAllProductsUseCase
import com.game.internetshop.domain.usecase.GetCartItemsUseCase
import com.game.internetshop.domain.usecase.auth.GetCurrentUserIdUseCase
import com.game.internetshop.domain.usecase.RemoveFromCartUseCase
import com.game.internetshop.views.base.catalogue.CatalogueUiItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogueViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val cartRealtimeService: CartRealtimeService
    ): ViewModel() {

    private val _uiState = MutableLiveData(CatalogueUiState()) // состояние UI
    val uiState: LiveData<CatalogueUiState> = _uiState
    private val allProducts = mutableListOf<Product>() // все продукты изначально загружаются из репозитория/бд
    private val userCartItems = mutableListOf<ProductInCart>() // кэш корзины пользователя
    private var isSubscribed = false
    private var currentUserId: Int = 0
    // Job для управления корутинами подписки
    private var subscriptionJob: Job? = null
    private var eventsCollectionJob: Job? = null


    init {
        loadCurrentUserId()
    }

    data class CatalogueUiState(
        val catalogueUiItems: List<CatalogueUiItem> = emptyList(),
        val searchQuery: String = "",
        val selectedFilter: ProductFilter = ProductFilter.NONE,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isInitialized: Boolean = false
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value?.copy(searchQuery = query)
        applyFilterAndSearch()
    }

    fun onFilterSelected(filter: ProductFilter) {
        _uiState.value = _uiState.value?.copy(selectedFilter = filter)
        applyFilterAndSearch()
    }

    fun onAddingToCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)

            when (val result = addToCartUseCase.invoke(currentUserId, productId)) {
                is Result.Success -> loadUserCart()
                is Result.Error -> {
                    _uiState.value = _uiState.value?.copy(errorMessage = result.message, isLoading = false)
                }
                is Result.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            when(val result = getCurrentUserIdUseCase.invoke()) {
                is Result.Success -> {
                    currentUserId = result.data
                    // загрузка данных после получения currentUserId
                    loadInitialData()
                    setupRealtimeSubscription()
                }
                is Result.Error -> _uiState.value = _uiState.value?.copy(errorMessage = result.message, isLoading = false)
                is Result.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)

            when (val result = getAllProductsUseCase()) {
                is Result.Success -> {
                    allProducts.clear()
                    allProducts.addAll(result.data)

                    loadUserCart()

                    applyFilterAndSearch()
                    _uiState.value = _uiState.value?.copy(isLoading = false)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                } else -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    fun onRemovingFromCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)

            when (val result = removeFromCartUseCase(currentUserId, productId)) {
                is Result.Success -> loadUserCart()
                is Result.Error -> {
                    _uiState.value = _uiState.value?.copy(errorMessage = result.message, isLoading = false)
                }
                is Result.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    private suspend fun loadUserCart() {
        if (currentUserId == 0) return

        when (val result = getCartItemsUseCase(currentUserId)) {
            is Result.Success -> {
                userCartItems.clear()
                userCartItems.addAll(result.data)
                applyFilterAndSearch()
            }
            is Result.Error -> _uiState.value = _uiState.value?.copy(errorMessage = result.message)
            is Result.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
        }
    }

    private fun applyFilterAndSearch() {
        val state = _uiState.value
        var filteredProducts = allProducts

        // применяем поиск
        if (state.searchQuery.isNotBlank()) {
            filteredProducts = filteredProducts.filter { product ->
                product.name.contains(state.searchQuery, ignoreCase = true)
            }.toMutableList()
        }

        // применяем выбранный фильтр
        filteredProducts = when (state.selectedFilter) {
            ProductFilter.NONE -> filteredProducts
            ProductFilter.PRICE_LOW_TO_HIGH -> filteredProducts.sortedBy { product -> product.price }.toMutableList()
            ProductFilter.PRICE_HIGH_TO_LOW -> filteredProducts.sortedByDescending { product -> product.price }.toMutableList()
            ProductFilter.NAME_A_TO_Z -> filteredProducts.sortedBy { product -> product.name }.toMutableList()
            ProductFilter.NAME_Z_TO_A -> filteredProducts.sortedByDescending { product -> product.name }.toMutableList()
        }

        // создаем ui для модели
        val catalogueUiItems = filteredProducts.map {
            product ->
            CatalogueUiItem(
                product,
                quantityInCart = userCartItems.find { it.productId == product.id }?.quantity ?: 0
            )
        }

        _uiState.value = state?.copy(
            catalogueUiItems=catalogueUiItems,
            isLoading = false,
            isInitialized = true
        ) ?: CatalogueUiState (catalogueUiItems = catalogueUiItems)
    }

    private fun setupRealtimeSubscription() {
        if (currentUserId == 0) return

        subscriptionJob?.cancel()
        eventsCollectionJob?.cancel()

        subscriptionJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                cartRealtimeService.subscribeToCartChanges(currentUserId)
            } catch (e: Exception) {
                Log.e("supabase_startrealtimesub", e.message.toString())
                isSubscribed = false
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value?.copy(
                        errorMessage = "Error in realtime connection"
                    )
                }
            }
        }

        eventsCollectionJob = viewModelScope.launch {
            cartRealtimeService.cartEvents.collect {
                loadUserCart()
            }
        }
    }

    override fun onCleared() {
        Log.w("supabase_oncleared", "Before unsubscribing")
        subscriptionJob?.cancel()
        eventsCollectionJob?.cancel()

        viewModelScope.launch {
            cartRealtimeService.unsubscribe()
        }
        Log.w("supabase_oncleared", "After unsubscribing")
        super.onCleared()
    }

    fun clearError() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }

    fun refreshRealtimeSubscription() {
        if (currentUserId != 0) {
            setupRealtimeSubscription()
        }
    }

    enum class ProductFilter {
        NONE,
        PRICE_LOW_TO_HIGH,
        PRICE_HIGH_TO_LOW,
        NAME_A_TO_Z,
        NAME_Z_TO_A
    }
}
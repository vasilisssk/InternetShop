package com.game.internetshop.ui.cart

import CartRealtimeService
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.CartResult
import com.game.internetshop.data.model.OrderResult
import com.game.internetshop.data.model.Product
import com.game.internetshop.data.model.ProductInCart
import com.game.internetshop.data.model.ProductResult
import com.game.internetshop.domain.usecase.AddToCartUseCase
import com.game.internetshop.domain.usecase.CreateNewOrderUseCase
import com.game.internetshop.domain.usecase.GetCartItemsUseCase
import com.game.internetshop.domain.usecase.GetCurrentUserIdUseCase
import com.game.internetshop.domain.usecase.GetProductByIdUseCase
import com.game.internetshop.domain.usecase.RemoveFromCartUseCase
import com.game.internetshop.ui.catalogue.CatalogueUiItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val createNewOrderUseCase: CreateNewOrderUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val cartRealtimeService: CartRealtimeService
): ViewModel() {
    private val _uiState = MutableLiveData(CartUiState()) // состояние UI
    val uiState: LiveData<CartUiState> = _uiState
    private val selectedProducts = mutableListOf<Product>() // информация о продуктах, которые выбрал пользователь
    private val userCartItems = mutableListOf<ProductInCart>() // кэш корзины пользователя
    private var isSubscribed = false
    private var currentUserId: Int = 0

    // Job для отслеживания корутин подписки
    private var subscriptionJob: Job? = null
    private var eventsCollectionJob: Job? = null

    init {
        loadUserAndHisCart()
    }

    data class CartUiState(
        val cartUiItems: List<CatalogueUiItem> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val isCreationSuccessful: Boolean = false,
        val paymentMethod: PaymentMethod = PaymentMethod.NOT_CHOSEN,
        val isInitialized: Boolean = false
    )

    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        _uiState.value = _uiState.value?.copy(
            paymentMethod = paymentMethod
        )
    }

    fun onCreateOrderClicked() {
        if (_uiState.value?.paymentMethod != PaymentMethod.NOT_CHOSEN) {
            viewModelScope.launch {
                Log.w("supabase_cartviewmodel_oncretaeorderclicked", "Before creating new order. User cart items: $userCartItems")
                val result = createNewOrderUseCase.invoke(currentUserId, userCartItems, _uiState.value?.paymentMethod?.code!!)
                when (result) {
                    is OrderResult.Success -> {
                        _uiState.value = _uiState.value?.copy(isCreationSuccessful = true)
                        loadUserCartInfo(currentUserId)
                    }
                    is OrderResult.Error -> {
                        _uiState.value = _uiState.value?.copy(errorMessage = result.message)
                    }
                    else -> {
                        _uiState.value = _uiState.value?.copy(isLoading = true)
                    }
                }
            }
            Log.w("supabase_cartviewmodel_oncretaeorderclicked", "After creating new order. User cart items: $userCartItems")
        }
        else {
            _uiState.value = _uiState.value?.copy(errorMessage = "Select payment method")
        }
    }

    fun onAddingToCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)

            when (val result = addToCartUseCase(currentUserId, productId)) {
                is CartResult.Success -> loadUserCartInfo(currentUserId)
                is CartResult.Error -> {
                    _uiState.value = _uiState.value?.copy(errorMessage = result.message, isLoading = false)
                }
                is CartResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    fun onRemovingFromCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)

            when (val result = removeFromCartUseCase(currentUserId, productId)) {
                is CartResult.Success -> loadUserCartInfo(currentUserId)
                is CartResult.Error -> {
                    _uiState.value = _uiState.value?.copy(errorMessage = result.message, isLoading = false)
                }
                is CartResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    private fun loadUserAndHisCart() {
        viewModelScope.launch {
            val result = getCurrentUserIdUseCase.invoke()
            when(result) {
                is AuthResult.Success ->  {
                    currentUserId = result.data
                    loadUserCartInfo(currentUserId)
                    setupRealtimeSubscription(currentUserId)
                }
                is AuthResult.Error -> _uiState.value = _uiState.value?.copy(errorMessage = result.message, isLoading = false)
                is AuthResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
    }

    private fun setupRealtimeSubscription(userId: Int) {
        // Отменяем предыдущие подписки, если есть
        subscriptionJob?.cancel()
        eventsCollectionJob?.cancel()

        subscriptionJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                cartRealtimeService.subscribeToCartChanges(userId)
            } catch (e: Exception) {
                Log.e("cartviewmodel_setuprealtimesubscription", e.message.toString())
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value?.copy(
                        errorMessage = "Realtime connection error"
                    )
                }
            }
        }

        // собираем события в отдельной корутине
        eventsCollectionJob = viewModelScope.launch {
            cartRealtimeService.cartEvents.collect {
                // данный collect не блокирует корутину
                loadUserCartInfo(userId)
            }
        }
    }

    private fun loadUserCartInfo(currentUserId: Int) {
        Log.w("supabase_cartviewmodel_loadusercartinfo", "Before loading")
        viewModelScope.launch {
            when (val result = getCartItemsUseCase(currentUserId)) {
                is CartResult.Success -> {
                    userCartItems.clear()
                    userCartItems.addAll(result.data)
                    Log.w("supabase_cartviewmodel_loadusercartinfo","UserCartItems after getCartItemsUseCase: $userCartItems")

                    selectedProducts.clear()
                    val selectedProductsFromDB = mutableListOf<Product>()

                    val deferredProducts = result.data.map { cartItem ->
                        async { getProductByIdUseCase.invoke(cartItem.productId) }
                    }

                    deferredProducts.forEach { deferred ->
                        when (val productResult = deferred.await()) {
                            is ProductResult.Success -> {
                                selectedProductsFromDB.add(productResult.data)
                            }
                            is ProductResult.Error -> {
                                Log.e("cartviewmodel_loadusercartinfo", "Failed to load product")
                            }
                            else -> Unit
                        }
                    }

                    selectedProducts.addAll(selectedProductsFromDB)
                    selectedProducts.sortBy { it.name }
                    formCartUiItemsList()
                }
                is CartResult.Error -> _uiState.value = _uiState.value?.copy(errorMessage = result.message)
                is CartResult.Loading -> _uiState.value = _uiState.value?.copy(isLoading = true)
            }
        }
        Log.w("supabase_cartviewmodel_loadusercartinfo", "After loading. CartUiItems - ${_uiState.value?.cartUiItems.toString()}")
    }

    private fun formCartUiItemsList() {
        val cartUiItems = selectedProducts.map {
            product -> CatalogueUiItem(
                product,
                userCartItems.find {it.productId == product.id}?.quantity!!
            )
        }

        _uiState.value = _uiState.value?.copy(
            cartUiItems = cartUiItems,
            isLoading = false,
            isInitialized = true
        ) ?: CartUiState (cartUiItems = cartUiItems)
    }

    override fun onCleared() {
        Log.w("supabase_oncleared", "Before unsubscribing")
        // Отменяем все корутины
        subscriptionJob?.cancel()
        eventsCollectionJob?.cancel()
        // Затем отписываемся от сервиса
        viewModelScope.launch {
            cartRealtimeService.unsubscribe()
        }
        Log.w("supabase_oncleared", "After unsubscribing")
        super.onCleared()
    }

    fun refreshRealtimeSubscription() {
        if (currentUserId != 0) {
            setupRealtimeSubscription(currentUserId)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value?.copy(isCreationSuccessful = false)
    }

    enum class PaymentMethod(val code: Int) {
        NOT_CHOSEN(0),
        CARD_ONLINE(1),
        CARD_UPON_RECEIPT(2),
        CASH(3),
        FPS(4),
        CRYPTO(5);

        companion object {
            fun getPaymentName(code: Int): PaymentMethod {
                return when (code) {
                    1 -> CARD_ONLINE
                    2 -> CARD_UPON_RECEIPT
                    3 -> CASH
                    4 -> FPS
                    5 -> CRYPTO
                    else -> NOT_CHOSEN
                }
            }


        }
    }
}
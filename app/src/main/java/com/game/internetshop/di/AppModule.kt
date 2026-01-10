package com.game.internetshop.di

import CartRealtimeService
import com.game.internetshop.BuildConfig
import com.game.internetshop.data.repository.AuthRepository
import com.game.internetshop.data.repository.AuthRepositoryImpl
import com.game.internetshop.data.repository.CartRepository
import com.game.internetshop.data.repository.CartRepositoryImpl
import com.game.internetshop.data.repository.OrderRepository
import com.game.internetshop.data.repository.OrderRepositoryImpl
import com.game.internetshop.data.repository.ProductRepository
import com.game.internetshop.data.repository.ProductRepositoryImpl
import com.game.internetshop.domain.usecase.AddToCartUseCase
import com.game.internetshop.domain.usecase.CreateNewOrderUseCase
import com.game.internetshop.domain.usecase.GetAllProductsInOrderUseCase
import com.game.internetshop.domain.usecase.GetAllProductsUseCase
import com.game.internetshop.domain.usecase.GetAllUserOrdersUseCase
import com.game.internetshop.domain.usecase.GetCartItemsUseCase
import com.game.internetshop.domain.usecase.GetCurrentUserIdUseCase
import com.game.internetshop.domain.usecase.GetCurrentUserUseCase
import com.game.internetshop.domain.usecase.GetProductByIdUseCase
import com.game.internetshop.domain.usecase.IsSessionActiveUseCase
import com.game.internetshop.domain.usecase.RegisterUseCase
import com.game.internetshop.domain.usecase.RemoveFromCartUseCase
import com.game.internetshop.domain.usecase.SignOutUseCase
import com.game.internetshop.domain.usecase.SignInUseCase
import com.game.internetshop.ui.cart.CartViewModel
import com.game.internetshop.ui.catalogue.CatalogueViewModel
import com.game.internetshop.ui.login.SigningInViewModel
import com.game.internetshop.ui.orders.OrdersViewModel
import com.game.internetshop.ui.registration.RegistrationViewModel
import com.game.internetshop.ui.settings.SettingsViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import kotlinx.serialization.json.Json
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

class AppModule {
    val appModule = module {

        single { createSupabaseClient() }

        single<CartRealtimeService> { CartRealtimeService(get()) }

        single<AuthRepository> { AuthRepositoryImpl(supabaseClient = get()) }
        single<ProductRepository> { ProductRepositoryImpl(supabaseClient = get()) }
        single<CartRepository> {
            CartRepositoryImpl(
                supabaseClient = get(),
                productRepository = get()
            )
        }
        single<OrderRepository> {
            OrderRepositoryImpl(
                supabaseClient = get(),
                cartRepository = get()
            )
        }

        factory { SignInUseCase(authRepository = get()) }
        factory { SignOutUseCase(authRepository = get()) }
        factory { RegisterUseCase(authRepository = get()) }
        factory { GetCurrentUserIdUseCase(authRepository = get()) }
        factory { GetCurrentUserUseCase(authRepository = get()) }
        factory { IsSessionActiveUseCase(authRepository = get()) }

        factory { GetAllProductsUseCase(productRepository = get()) }
        factory { GetProductByIdUseCase(productRepository = get()) }

        factory { GetCartItemsUseCase(cartRepository = get()) }
        factory { AddToCartUseCase(cartRepository = get()) }
        factory { RemoveFromCartUseCase(cartRepository = get()) }

        factory { CreateNewOrderUseCase(orderRepository = get()) }
        factory { GetAllUserOrdersUseCase(orderRepository = get()) }
        factory { GetAllProductsInOrderUseCase(orderRepository = get()) }

        viewModel { SigningInViewModel(signInUseCase = get()) }
        viewModel { RegistrationViewModel(registerUseCase = get(), isSessionActiveUseCase = get()) }
        viewModel {
            CatalogueViewModel(
                getAllProductsUseCase = get(),
                addToCartUseCase = get(),
                removeFromCartUseCase = get(),
                getCartItemsUseCase = get(),
                getCurrentUserIdUseCase = get(),
                cartRealtimeService = get()
            )
        }
        viewModel { SettingsViewModel(signOutUseCase = get(), getCurrentUserUseCase = get()) }
        viewModel {
            CartViewModel(
                addToCartUseCase = get(),
                removeFromCartUseCase = get(),
                getCartItemsUseCase = get(),
                getCurrentUserIdUseCase = get(),
                createNewOrderUseCase = get(),
                getProductByIdUseCase = get(),
                cartRealtimeService = get()
            )
        }
        viewModel {
            OrdersViewModel(
                getCurrentUserIdUseCase = get(),
                getAllUserOrdersUseCase = get(),
                getAllProductsInOrderUseCase = get(),
                getProductByIdUseCase = get()
            )
        }
    }

    fun createSupabaseClient(): SupabaseClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .pingInterval(20, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
                        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .build()
        val okHttpConfig = OkHttpConfig()
        okHttpConfig.preconfigured = client

        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Realtime)
            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
            httpEngine = OkHttpEngine(okHttpConfig)
        }
    }
}
package com.game.internetshop.di

import com.game.internetshop.data.repository.AuthRepository
import com.game.internetshop.data.repository.AuthRepositoryImpl
import com.game.internetshop.data.repository.CartRepository
import com.game.internetshop.data.repository.CartRepositoryImpl
import com.game.internetshop.data.repository.ProductRepository
import com.game.internetshop.data.repository.ProductRepositoryImpl
import com.game.internetshop.domain.usecase.AddToCartUseCase
import com.game.internetshop.domain.usecase.GetAllProductsUseCase
import com.game.internetshop.domain.usecase.GetCartItemsUseCase
import com.game.internetshop.domain.usecase.RegisterUseCase
import com.game.internetshop.domain.usecase.RemoveFromCartUseCase
import com.game.internetshop.domain.usecase.SigningInUseCase
import com.game.internetshop.ui.catalogue.CatalogueViewModel
import com.game.internetshop.ui.login.SigningInViewModel
import com.game.internetshop.ui.registration.RegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AppModule {
    val appModule = module {
        single<AuthRepository> { AuthRepositoryImpl() }
        single<ProductRepository> { ProductRepositoryImpl() }
        single<CartRepository> { CartRepositoryImpl(productRepository = get()) }

        factory { SigningInUseCase(authRepository = get()) }
        factory { RegisterUseCase(authRepository = get()) }
        factory { GetAllProductsUseCase(productRepository = get()) }
        factory { GetCartItemsUseCase(cartRepository = get()) }
        factory { AddToCartUseCase(cartRepository = get()) }
        factory { RemoveFromCartUseCase(cartRepository = get()) }

        viewModel { SigningInViewModel(signingInUseCase = get()) }
        viewModel { RegistrationViewModel(registerUseCase = get()) }
        viewModel {
            CatalogueViewModel(
                getAllProductsUseCase = get(),
                addToCartUseCase = get(),
                removeFromCartUseCase = get(),
                getCartItemsUseCase = get(),
                currentUserId = 1
            )
        }
    }
}
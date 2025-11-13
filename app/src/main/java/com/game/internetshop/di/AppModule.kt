package com.game.internetshop.di

import com.game.internetshop.data.repository.AuthRepository
import com.game.internetshop.data.repository.AuthRepositoryImpl
import com.game.internetshop.domain.usecase.RegisterUseCase
import com.game.internetshop.domain.usecase.SigningInUseCase
import com.game.internetshop.ui.login.SigningInViewModel
import com.game.internetshop.ui.registration.RegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AppModule {
    val appModule = module {
        single<AuthRepository> { AuthRepositoryImpl() }
        factory { SigningInUseCase(get()) }
        factory { RegisterUseCase(get()) }

        viewModel { SigningInViewModel(get()) }
        viewModel { RegistrationViewModel(get()) }
    }
}
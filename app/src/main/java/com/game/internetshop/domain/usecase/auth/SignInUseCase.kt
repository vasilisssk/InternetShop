package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.User
import com.game.internetshop.data.repository.AuthRepository

class SignInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
}
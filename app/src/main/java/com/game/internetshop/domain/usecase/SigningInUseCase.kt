package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.User
import com.game.internetshop.data.repository.AuthRepository

class SigningInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResult<User> {
        return authRepository.login(email, password)
    }
}
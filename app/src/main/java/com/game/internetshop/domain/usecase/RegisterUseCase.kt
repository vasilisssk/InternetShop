package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.User
import com.game.internetshop.data.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, phoneNumber:String, email: String, password: String): AuthResult<User> {
        return authRepository.register(name, phoneNumber, email, password)
    }
}
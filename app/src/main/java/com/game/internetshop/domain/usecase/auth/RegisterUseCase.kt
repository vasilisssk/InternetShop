package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.User
import com.game.internetshop.data.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, phoneNumber:String, email: String, password: String): Result<User> {
        return authRepository.register(name, phoneNumber, email, password)
    }
}
package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.repository.AuthRepository

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): AuthResult<Unit> {
        return authRepository.logout()
    }
}
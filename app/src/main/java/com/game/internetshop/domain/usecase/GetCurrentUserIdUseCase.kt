package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.repository.AuthRepository

class GetCurrentUserIdUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): AuthResult<Int> {
        return authRepository.getCurrentUserId()
    }
}
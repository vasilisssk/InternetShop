package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.repository.AuthRepository

class GetCurrentUserIdUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Int> {
        return authRepository.getCurrentUserId()
    }
}
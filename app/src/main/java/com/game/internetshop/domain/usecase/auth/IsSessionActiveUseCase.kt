package com.game.internetshop.domain.usecase.auth

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.repository.AuthRepository

class IsSessionActiveUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Boolean> {
        return authRepository.isSessionActive()
    }
}
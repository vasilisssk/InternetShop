package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.repository.AuthRepository

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
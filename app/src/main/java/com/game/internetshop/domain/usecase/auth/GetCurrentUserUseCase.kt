package com.game.internetshop.domain.usecase

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.UserProfile
import com.game.internetshop.data.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<UserProfile> {
        return authRepository.getCurrentUser()
    }
}
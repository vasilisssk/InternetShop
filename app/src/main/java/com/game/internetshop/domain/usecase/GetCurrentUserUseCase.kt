package com.game.internetshop.domain.usecase

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.UserProfile
import com.game.internetshop.data.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): AuthResult<UserProfile> {
        return authRepository.getCurrentUser()
    }
}
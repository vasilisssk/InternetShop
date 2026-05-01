package com.game.internetshop.data.repository

import com.game.internetshop.data.common.Result
import com.game.internetshop.data.model.User
import com.game.internetshop.data.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, phoneNumber: String, email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUserId(): Result<Int>
    suspend fun isSessionActive(): Result<Boolean>
    suspend fun getCurrentUser(): Result<UserProfile>
}
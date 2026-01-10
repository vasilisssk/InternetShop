package com.game.internetshop.data.repository

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.User
import com.game.internetshop.data.model.UserProfile
import io.github.jan.supabase.auth.user.UserSession

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun register(name: String, phoneNumber: String, email: String, password: String): AuthResult<User>
    suspend fun logout(): AuthResult<Unit>
    suspend fun getCurrentUserId(): AuthResult<Int>
    suspend fun isSessionActive(): AuthResult<Boolean>
    suspend fun getCurrentUser(): AuthResult<UserProfile>
}
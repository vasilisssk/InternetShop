package com.game.internetshop.data.repository

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun register(name: String, phoneNumber: String, email: String, password: String): AuthResult<User>
}
package com.game.internetshop.data.repository

import com.game.internetshop.data.model.AuthResult
import com.game.internetshop.data.model.User

class AuthRepositoryImpl: AuthRepository {
    override suspend fun login(email: String, password: String): AuthResult<User> {
        return try {
            if (email == "erokhovvasiliy2005@gmail.com" && password == "123456") {
                AuthResult.Success(User(1, email, "Test User"))
            } else {
                AuthResult.Error("Invalid credentials")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(name: String, phoneNumber: String, email: String, password: String): AuthResult<User> {
        return try {
                AuthResult.Success(User(2, email, name))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }
}
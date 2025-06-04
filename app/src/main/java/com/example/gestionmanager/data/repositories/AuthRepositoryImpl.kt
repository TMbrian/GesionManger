package com.example.gestionmanager.data.repositories

import com.example.gestionmanager.data.datasources.remote.ApiService
import com.example.gestionmanager.data.model.request.GoogleAuthRequest
import com.example.gestionmanager.data.model.request.LoginRequest
import com.example.gestionmanager.data.model.response.AuthResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): AuthResponse {
        return try {
            apiService.login(LoginRequest(email, password))
        } catch (e: Exception) {
            throw AuthException("Error en login con email: ${e.message}")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): AuthResponse {
        return try {
            apiService.loginWithGoogle(GoogleAuthRequest(idToken))
        } catch (e: Exception) {
            throw AuthException("Error en login con Google: ${e.message}")
        }
    }
}

class AuthException(message: String) : Exception(message)
package com.example.gestionmanager.data.repositories

import com.example.gestionmanager.data.model.response.AuthResponse

// domain/repositories/AuthRepository.kt
interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): AuthResponse
    suspend fun loginWithGoogle(idToken: String): AuthResponse
}
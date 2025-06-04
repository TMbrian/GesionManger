package com.example.gestionmanager.domain.usecases

import com.example.gestionmanager.data.model.response.AuthResponse
import com.example.gestionmanager.data.repositories.AuthRepository
import com.example.gestionmanager.data.util.Resource
import javax.inject.Inject

class LoginGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Resource<AuthResponse> {
        return try {
            val response = repository.loginWithGoogle(idToken)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al iniciar sesión con Google")
        }
    }
}
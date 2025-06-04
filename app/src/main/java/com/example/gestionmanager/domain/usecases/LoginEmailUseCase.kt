package com.example.gestionmanager.domain.usecases

import com.example.gestionmanager.data.model.response.AuthResponse
import com.example.gestionmanager.data.util.Resource
import com.example.gestionmanager.data.repositories.AuthRepository
import javax.inject.Inject

class LoginEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Resource<AuthResponse> {  // Cambiado a Resource<AuthResponse>
        return try {
            val response = repository.loginWithEmail(email, password)
            Resource.Success(response)  // Envuelve la respuesta en Resource.Success
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error desconocido")  // Maneja errores
        }
    }
}
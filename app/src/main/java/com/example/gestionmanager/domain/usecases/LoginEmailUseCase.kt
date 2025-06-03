package com.example.gestionmanager.domain.usecases

import com.example.gestionmanager.data.model.response.AuthResponse
import com.example.gestionmanager.data.repositories.AuthRepository
import javax.inject.Inject

class LoginEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): AuthResponse {
        return repository.loginWithEmail(email, password)
    }
}
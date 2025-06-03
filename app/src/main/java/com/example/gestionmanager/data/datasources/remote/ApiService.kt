package com.example.gestionmanager.data.datasources.remote

import com.example.gestionmanager.data.model.request.GoogleAuthRequest
import com.example.gestionmanager.data.model.request.LoginRequest
import com.example.gestionmanager.data.model.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

// data/datasources/remote/ApiService.kt
interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): AuthResponse
}
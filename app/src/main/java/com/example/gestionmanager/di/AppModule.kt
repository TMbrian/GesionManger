package com.example.gestionmanager.di

import com.example.gestionmanager.data.datasources.remote.ApiService
import com.example.gestionmanager.domain.repositories.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. Proporciona Retrofit primero
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://tu-api.com/") // Reemplaza con tu URL base
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Proporciona ApiService
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // 3. Ahora sí puedes proporcionar AuthRepositoryImpl
    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepositoryImpl {
        return AuthRepositoryImpl(apiService)
    }
}
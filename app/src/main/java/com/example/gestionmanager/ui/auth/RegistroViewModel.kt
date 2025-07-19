package com.example.gestionmanager.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionmanager.data.model.Usuario
import com.example.gestionmanager.data.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
        // Aquí inyectarías tu repositorio
        // private val authRepository: AuthRepository
    ) : ViewModel() {

        private val _registroState = MutableLiveData<Resource<Usuario>>()
        val registroState: LiveData<Resource<Usuario>> = _registroState

        /**
         * Registra un nuevo usuario en el sistema
         */
        fun registrarUsuario(usuario: Usuario) {
            viewModelScope.launch {
                try {
                    _registroState.value = Resource.Loading()

                    // Simular llamada a API (reemplazar con tu lógica real)
                    delay(2000)

                    // Simular validación de usuario existente
                    if (usuario.nombreUsuario == "admin") {
                        _registroState.value = Resource.Error("El nombre de usuario ya existe")
                        return@launch
                    }

                    // Simular registro exitoso
                    val usuarioRegistrado = usuario.copy(id = "user_${System.currentTimeMillis()}")
                    _registroState.value = Resource.Success(usuarioRegistrado)

                } catch (e: Exception) {
                    _registroState.value = Resource.Error(e.message ?: "Error durante el registro")
                }
            }
        }
    }
}
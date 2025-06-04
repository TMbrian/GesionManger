package com.example.gestionmanager.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionmanager.data.model.response.AuthResponse
import com.example.gestionmanager.data.util.Resource
import com.example.gestionmanager.domain.usecases.LoginEmailUseCase
import com.example.gestionmanager.domain.usecases.LoginGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel encargado de gestionar la lógica de autenticación del usuario.
 * Utiliza Hilt para la inyección de dependencias y expone un estado observable
 * del resultado del login.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailUseCase: LoginEmailUseCase,
    private val loginGoogleUseCase: LoginGoogleUseCase
) : ViewModel() {

    // LiveData que representa el estado actual del login
    private val _loginState = MutableLiveData<Resource<AuthResponse>>()
    val loginState: LiveData<Resource<AuthResponse>> = _loginState

    // Inicia el proceso de login usando email y contraseña
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = loginEmailUseCase(email, password)
        }
    }

    // Inicia el proceso de login usando la autenticación de Google
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = loginGoogleUseCase(idToken)
        }
    }
}
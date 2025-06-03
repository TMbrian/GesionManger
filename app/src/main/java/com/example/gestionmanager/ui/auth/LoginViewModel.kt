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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailUseCase: LoginEmailUseCase,
    private val loginGoogleUseCase: LoginGoogleUseCase
) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<AuthResponse>>()
    val loginState: LiveData<Resource<AuthResponse>> = _loginState

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = loginEmailUseCase(email, password)
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = loginGoogleUseCase(idToken)
        }
    }
}
package com.example.gestionmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.gestionmanager.MainActivity
import com.example.gestionmanager.R
import com.example.gestionmanager.data.util.Resource
import com.example.gestionmanager.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad de inicio de sesión del usuario.
 *
 * Esta clase maneja la interfaz de usuario y los eventos de interacción
 * para permitir el login mediante correo electrónico/contraseña o Google.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de la vista
    private lateinit var binding: ActivityLoginBinding

    // ViewModel inyectado usando delegación de viewModels()
    private val viewModel: LoginViewModel by viewModels()

    // Cliente de inicio de sesión de Google configurado con el ID de cliente web
    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers() // Observa los cambios de estado de login

        // Login con email y contraseña
        binding.btnIniciarSesion.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.loginWithEmail(email, password)
        }

        // Login con Google
        binding.btnGoogleSignin.setOnClickListener {
            startActivityForResult(
                googleSignInClient.signInIntent,
                RC_GOOGLE_SIGN_IN
            )
        }
    }

    /**
     * Observa el estado del login y actualiza la UI según sea necesario
     */
    private fun setupObservers() {
        viewModel.loginState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this@LoginActivity, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Maneja el resultado de la autenticación con Google
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { viewModel.loginWithGoogle(it) }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Muestra u oculta el indicador de carga
     */
    private fun showLoading(show: Boolean) {
        // Implementa tu lógica de loading aquí
    }

    companion object {
        // Código de solicitud para el inicio de sesión con Google
        private const val RC_GOOGLE_SIGN_IN = 9001
    }
}
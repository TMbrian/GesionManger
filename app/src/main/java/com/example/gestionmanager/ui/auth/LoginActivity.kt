package com.example.gestionmanager.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gestionmanager.MainActivity
import com.example.gestionmanager.R
import com.example.gestionmanager.data.util.Resource
import com.example.gestionmanager.databinding.ActivityLoginBinding
import com.example.gestionmanager.ui.auth.dialog.GenericProgressBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *                                    LOGIN ACTIVITY
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *
 * Actividad principal de autenticación del usuario que maneja:
 *
 * 🔐 FUNCIONALIDADES PRINCIPALES:
 * • Autenticación con email/contraseña
 * • Autenticación con Google Sign-In
 * • Manejo automático de permisos en tiempo de ejecución
 * • Validación de campos de entrada
 * • Estados de carga con diálogo de progreso personalizado
 *
 * @author Tu Brian Billy Tzuc Mut
 * @version 1.0.0
 * @since 2025
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                                    PROPIEDADES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /** ViewBinding para acceso type-safe a los elementos de la vista */
    private lateinit var binding: ActivityLoginBinding

    /** ViewModel inyectado para manejo de lógica de negocio */
    private val viewModel: LoginViewModel by viewModels()

    /** Diálogo de progreso personalizado para operaciones de login */
    private var progressDialog: AlertDialog? = null

    /** Cliente de Google Sign-In configurado con token ID */
    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    /**
     * Permisos requeridos según la versión de Android
     * Se adapta automáticamente a los cambios de API
     */
    private val permisosRequeridos = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            // Android 14+ (API 34+) - Acceso selectivo a fotos
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            // Android 13 (API 33) - Nuevos permisos de media
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        }
        else -> {
            // Android 7-12 (API 24-32) - Permisos legacy
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                                 CONSTANTES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    companion object {
        /** Código de solicitud para permisos en tiempo de ejecución */
        private const val PERMISSION_REQUEST_CODE = 100

        /** Código de solicitud para Google Sign-In */
        private const val RC_GOOGLE_SIGN_IN = 9001
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              CICLO DE VIDA
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar permisos al iniciar
        verificarYSolicitarPermisos()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar recursos del diálogo de progreso
        progressDialog?.dismiss()
        progressDialog = null
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              MANEJO DE PERMISOS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Verifica si todos los permisos necesarios están otorgados
     * @return true si todos los permisos están otorgados, false en caso contrario
     */
    private fun tienePermisosNecesarios(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            verificarPermisosAndroid14Plus()
        } else {
            permisosRequeridos.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    /**
     * Verificación específica para Android 14+ con manejo de acceso selectivo a fotos
     */
    private fun verificarPermisosAndroid14Plus(): Boolean {
        val permisosBasicos = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        // Verificar cada permiso individualmente con logs
        permisosBasicos.forEach { permiso ->
            val estado = ContextCompat.checkSelfPermission(this, permiso)
            Log.d("PermisosDebug", "$permiso: ${if (estado == PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"}")
        }

        val permisosBasicosOtorgados = permisosBasicos.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        Log.d("PermisosDebug", "Permisos básicos otorgados: $permisosBasicosOtorgados")

        if (!permisosBasicosOtorgados) {
            return false
        }

        // Verificar acceso a fotos
        val tieneAccesoCompleto = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val tieneAccesoSeleccionado = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

        val tieneAccesoFotos = tieneAccesoCompleto || tieneAccesoSeleccionado
        Log.d("PermisosDebug", "Acceso fotos: completo=$tieneAccesoCompleto, seleccionado=$tieneAccesoSeleccionado")

        return tieneAccesoFotos
    }

    /**
     * Verifica permisos y los solicita si es necesario, luego inicializa la UI
     */
    private fun verificarYSolicitarPermisos() {
        Log.d("PermisosDebug", "Verificando permisos iniciales...")
        if (tienePermisosNecesarios()) {
            Log.d("PermisosDebug", "Todos los permisos ya están otorgados")
            inicializarComponentes()
        } else {
            Log.d("PermisosDebug", "Faltan permisos, solicitando...")
            solicitarPermisos()
        }
    }

    /**
     * Solicita los permisos necesarios según la versión de Android
     */
    private fun solicitarPermisos() {
        val permisosPendientes = permisosRequeridos.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        Log.d("PermisosDebug", "Permisos pendientes: ${permisosPendientes.contentToString()}")

        if (permisosPendientes.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permisosPendientes, PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * Maneja la respuesta del usuario a la solicitud de permisos
     * Aquí es donde se verifica después de la respuesta del usuario
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            Log.d("PermisosDebug", "Callback de permisos recibido")

            // Log de la respuesta del usuario
            permissions.forEachIndexed { index, permission ->
                val granted = grantResults[index] == PackageManager.PERMISSION_GRANTED
                Log.d("PermisosDebug", "Respuesta - $permission: ${if (granted) "GRANTED" else "DENIED"}")
            }

            // Pequeño delay para asegurar que el sistema haya procesado los permisos
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d("PermisosDebug", "Verificando permisos después del callback...")

                // AQUÍ es donde verificamos los permisos después de la respuesta
                if (tienePermisosNecesarios()) {
                    Log.d("PermisosDebug", "✅ Permisos verificados correctamente - Inicializando componentes")
                    inicializarComponentes()
                } else {
                    Log.d("PermisosDebug", "❌ Aún faltan permisos - Mostrando opción de configuración")
                    mostrarOpcionConfiguracion()
                }
            }, 100) // Delay de 100ms
        }
    }

    /**
     * Muestra un diálogo simple para ir a configuración si no se otorgaron permisos
     */
    private fun mostrarOpcionConfiguracion() {
        AlertDialog.Builder(this)
            .setTitle("Permisos necesarios")
            .setMessage("La aplicación necesita permisos para funcionar correctamente.")
            .setPositiveButton("Configuración") { _, _ ->
                abrirConfiguracionApp()
            }
            .setNegativeButton("Continuar") { _, _ ->
                inicializarComponentes()
            }
            .show()
    }

    /**
     * Abre la configuración de la aplicación para que el usuario otorgue permisos manualmente
     */
    private fun abrirConfiguracionApp() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              INICIALIZACIÓN DE UI
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Inicializa todos los componentes de la interfaz de usuario
     * Solo se ejecuta cuando los permisos están otorgados
     */
    private fun inicializarComponentes() {
        Log.d("PermisosDebug", "🎉 Inicializando componentes de la UI")
        setupObservers()
        setupClickListeners()
        habilitarBotones(true)
    }

    /**
     * Configura los listeners de los botones de la interfaz
     */
    private fun setupClickListeners() {
        // Login con email y contraseña
        binding.btnIniciarSesion.setOnClickListener {
            if (tienePermisosNecesarios()) {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()

                if (validarCampos(email, password)) {
                    viewModel.loginWithEmail(email, password)
                }
            } else {
                Toast.makeText(this, "Se requieren permisos para iniciar sesión", Toast.LENGTH_SHORT).show()
                verificarYSolicitarPermisos()
            }
        }

        // Login con Google
        binding.btnGoogleSignin.setOnClickListener {
            if (tienePermisosNecesarios()) {
                startActivityForResult(
                    googleSignInClient.signInIntent,
                    RC_GOOGLE_SIGN_IN
                )
            } else {
                Toast.makeText(this, "Se requieren permisos para iniciar sesión", Toast.LENGTH_SHORT).show()
                verificarYSolicitarPermisos()
            }
        }
    }

    /**
     * Valida los campos de email y contraseña
     * @param email Email ingresado por el usuario
     * @param password Contraseña ingresada por el usuario
     * @return true si los campos son válidos, false en caso contrario
     */
    private fun validarCampos(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                binding.etEmail.error = "Ingrese su correo electrónico"
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Ingrese un correo válido"
                return false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Ingrese su contraseña"
                return false
            }
            password.length < 6 -> {
                binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                return false
            }
            else -> return true
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              OBSERVADORES Y ESTADOS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Configura los observadores para los estados del ViewModel
     * Maneja los diferentes estados: Loading, Success, Error
     */
    private fun setupObservers() {
        viewModel.loginState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    mostrarDialogoProgreso(true)
                }
                is Resource.Success -> {
                    mostrarDialogoProgreso(false)
                    navegarAMainActivity()
                }
                is Resource.Error -> {
                    mostrarDialogoProgreso(false)
                    mostrarError(resource.message ?: "Error desconocido")
                }
            }
        }
    }

    /**
     * Muestra u oculta el diálogo de progreso personalizado
     * @param mostrar true para mostrar, false para ocultar
     */
    private fun mostrarDialogoProgreso(mostrar: Boolean) {
        if (mostrar) {
            if (progressDialog == null) {
                // Crear el diálogo de progreso personalizado
                val progressView = GenericProgressBar(this).apply {
                    setTitle("Iniciando sesión...")
                    setDescription("Verificando credenciales")
                    setIndeterminate(true)
                    showButtons(false)
                    showLinearProgress(false)
                }

                progressDialog = AlertDialog.Builder(this)
                    .setView(progressView)
                    .setCancelable(false)
                    .create()
            }
            progressDialog?.show()
            habilitarBotones(false)
        } else {
            progressDialog?.dismiss()
            habilitarBotones(true)
        }
    }

    /**
     * Habilita o deshabilita los botones de la interfaz
     * @param habilitar true para habilitar, false para deshabilitar
     */
    private fun habilitarBotones(habilitar: Boolean) {
        binding.btnIniciarSesion.isEnabled = habilitar
        binding.btnGoogleSignin.isEnabled = habilitar
    }

    /**
     * Muestra un mensaje de error al usuario
     * @param mensaje Mensaje de error a mostrar
     */
    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    /**
     * Navega a la actividad principal después del login exitoso
     */
    private fun navegarAMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              GOOGLE SIGN-IN
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Maneja el resultado de la autenticación con Google
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let {
                    viewModel.loginWithGoogle(it)
                } ?: run {
                    mostrarError("Error: No se pudo obtener el token de Google")
                }
            } catch (e: ApiException) {
                mostrarError("Error con Google Sign-In: ${e.message}")
            }
        }
    }
}
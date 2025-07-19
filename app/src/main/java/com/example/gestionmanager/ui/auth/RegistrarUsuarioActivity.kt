package com.example.gestionmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gestionmanager.BuildConfig
import com.example.gestionmanager.MainActivity
import com.example.gestionmanager.databinding.ActivityRegistrarUsuarioBinding
import com.example.gestionmanager.ui.auth.dialog.mensajeGenericoDialog
import com.example.gestionmanager.ui.auth.dialog.progressBarGenericoDialog
import com.example.gestionmanager.ui.auth.viewmodel.RegistroViewModel
import com.example.gestionmanager.data.model.Usuario
import com.example.gestionmanager.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *                                    REGISTRAR USUARIO ACTIVITY
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 *
 * Actividad para el registro de nuevos usuarios en el sistema
 *
 * 🔐 FUNCIONALIDADES PRINCIPALES:
 * • Validación de campos de entrada
 * • Registro de usuario con diferentes roles
 * • Manejo de estados de carga y errores
 * • Navegación automática después del registro exitoso
 * • Modo debug para rol de administrador
 *
 * @author Tu Brian Billy Tzuc Mut
 * @version 1.0.0
 * @since 2025
 * ═══════════════════════════════════════════════════════════════════════════════════════════════
 */
@AndroidEntryPoint
class RegistrarUsuarioActivity : AppCompatActivity() {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                                    PROPIEDADES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /** ViewBinding para acceso type-safe a los elementos de la vista */
    private lateinit var binding: ActivityRegistrarUsuarioBinding

    /** ViewModel para manejar la lógica de registro */
    private lateinit var viewModel: RegistroViewModel

    /** Diálogo de progreso personalizado para operaciones de registro */
    private var progressDialog: AlertDialog? = null

    /** Diálogo de mensajes personalizado para operaciones de registro */
    private var mensajeDialog: AlertDialog? = null

    /** Rol seleccionado por el usuario */
    private var rolSeleccionado: String = "dueno"

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              CICLO DE VIDA
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[RegistroViewModel::class.java]

        inicializarComponentes()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar recursos de los diálogos
        progressDialog?.dismiss()
        progressDialog = null
        mensajeDialog?.dismiss()
        mensajeDialog = null
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              INICIALIZACIÓN DE UI
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Inicializa todos los componentes de la interfaz de usuario
     */
    private fun inicializarComponentes() {
        Log.d("RegistroDebug", "🎉 Inicializando componentes de la UI")
        setupObservers()
        setupClickListeners()
        configurarRoles()
        habilitarBotones(true)
    }

    /**
     * Configura los listeners de los botones de la interfaz
     */
    private fun setupClickListeners() {
        // Botón de registro
        binding.btnRegistrar.setOnClickListener {
            if (validarCampos()) {
                registrarUsuario()
            }
        }

        // Botón de iniciar sesión (navegar al login)
        binding.btnIniciarSesion.setOnClickListener {
            finish() // Volver al login
        }

        // RadioGroup para selección de rol
        binding.rgRol.setOnCheckedChangeListener { _, checkedId ->
            rolSeleccionado = when (checkedId) {
                binding.rbDueno.id -> "dueno"
                binding.rbAdministrador.id -> "administrador"
                binding.rbVendedor.id -> "vendedor"
                else -> "dueno"
            }
            Log.d("RegistroDebug", "Rol seleccionado: $rolSeleccionado")
        }
    }

    /**
     * Configura la visibilidad de los roles según el modo debug
     */
    private fun configurarRoles() {
        // Mostrar rol de administrador solo en modo debug
        if (BuildConfig.DEBUG) {
            binding.rbAdministrador.visibility = View.VISIBLE
            Log.d("RegistroDebug", "🔧 Modo Debug: Rol de Administrador disponible")
        } else {
            binding.rbAdministrador.visibility = View.GONE
            Log.d("RegistroDebug", "🚀 Modo Producción: Rol de Administrador oculto")
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              VALIDACIÓN DE DATOS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Valida todos los campos del formulario
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private fun validarCampos(): Boolean {
        var esValido = true

        // Limpiar errores previos
        limpiarErrores()

        // Validar nombre
        val nombre = binding.etNombre.text.toString().trim()
        if (nombre.isEmpty()) {
            binding.tilNombre.error = "El nombre es requerido"
            esValido = false
        } else if (nombre.length < 2) {
            binding.tilNombre.error = "El nombre debe tener al menos 2 caracteres"
            esValido = false
        }

        // Validar apellido
        val apellido = binding.etApellido.text.toString().trim()
        if (apellido.isEmpty()) {
            binding.tilApellido.error = "El apellido es requerido"
            esValido = false
        } else if (apellido.length < 2) {
            binding.tilApellido.error = "El apellido debe tener al menos 2 caracteres"
            esValido = false
        }

        // Validar email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "El email es requerido"
            esValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Formato de email inválido"
            esValido = false
        }

        // Validar nombre de usuario
        val usuario = binding.etUsuario.text.toString().trim()
        if (usuario.isEmpty()) {
            binding.tilUsuario.error = "El nombre de usuario es requerido"
            esValido = false
        } else if (usuario.length < 4) {
            binding.tilUsuario.error = "El usuario debe tener al menos 4 caracteres"
            esValido = false
        } else if (!usuario.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            binding.tilUsuario.error = "Solo se permiten letras, números y guiones bajos"
            esValido = false
        }

        // Validar contraseña
        val password = binding.etPassword.text.toString()
        if (password.isEmpty()) {
            binding.tilPassword.error = "La contraseña es requerida"
            esValido = false
        } else if (password.length < 8) {
            binding.tilPassword.error = "La contraseña debe tener al menos 8 caracteres"
            esValido = false
        } else if (!validarFortalezaPassword(password)) {
            binding.tilPassword.error = "La contraseña debe contener al menos una mayúscula, una minúscula y un número"
            esValido = false
        }

        // Validar teléfono
        val telefono = binding.etTelefono.text.toString().trim()
        if (telefono.isEmpty()) {
            binding.tilTelefono.error = "El teléfono es requerido"
            esValido = false
        } else if (telefono.length < 10) {
            binding.tilTelefono.error = "El teléfono debe tener al menos 10 dígitos"
            esValido = false
        } else if (!telefono.matches(Regex("^[+]?[0-9\\s-()]+$"))) {
            binding.tilTelefono.error = "Formato de teléfono inválido"
            esValido = false
        }

        return esValido
    }

    /**
     * Valida la fortaleza de la contraseña
     * @param password Contraseña a validar
     * @return true si la contraseña es fuerte, false en caso contrario
     */
    private fun validarFortalezaPassword(password: String): Boolean {
        val tieneMayuscula = password.any { it.isUpperCase() }
        val tieneMinuscula = password.any { it.isLowerCase() }
        val tieneNumero = password.any { it.isDigit() }

        return tieneMayuscula && tieneMinuscula && tieneNumero
    }

    /**
     * Limpia todos los errores de los campos
     */
    private fun limpiarErrores() {
        binding.tilNombre.error = null
        binding.tilApellido.error = null
        binding.tilEmail.error = null
        binding.tilUsuario.error = null
        binding.tilPassword.error = null
        binding.tilTelefono.error = null
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              LÓGICA DE REGISTRO
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Ejecuta el proceso de registro del usuario
     */
    private fun registrarUsuario() {
        val usuario = Usuario(
            nombre = binding.etNombre.text.toString().trim(),
            apellido = binding.etApellido.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            nombreUsuario = binding.etUsuario.text.toString().trim(),
            password = binding.etPassword.text.toString(),
            telefono = binding.etTelefono.text.toString().trim(),
            rol = rolSeleccionado
        )

        Log.d("RegistroDebug", "Iniciando registro para usuario: ${usuario.nombreUsuario}")
        viewModel.registrarUsuario(usuario)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    //                              OBSERVADORES Y ESTADOS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Configura los observadores para los estados del ViewModel
     * Maneja los diferentes estados: Loading, Success, Error
     */
    private fun setupObservers() {
        viewModel.registroState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("RegistroDebug", "Estado: Cargando...")
                    mostrarDialogoProgreso(true)
                }

                is Resource.Success -> {
                    Log.d("RegistroDebug", "Estado: Éxito - ${resource.data}")
                    mostrarDialogoProgreso(false)
                    mostrarMensaje("ÉXITO", "Usuario registrado correctamente. Bienvenido ${resource.data?.nombre}!")
                }

                is Resource.Error -> {
                    Log.e("RegistroDebug", "Estado: Error - ${resource.message}")
                    mostrarDialogoProgreso(false)
                    mostrarMensaje("ERROR", resource.message ?: "Error desconocido durante el registro")
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
                val progressView = progressBarGenericoDialog(this).apply {
                    setTitle("Registro Usuario...")
                    setDescription("Creando cuenta de usuario")
                    setIndeterminate(true)
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
        binding.btnRegistrar.isEnabled = habilitar
        binding.btnIniciarSesion.isEnabled = habilitar
        binding.rgRol.isEnabled = habilitar

        // Deshabilitar campos durante el proceso
        binding.etNombre.isEnabled = habilitar
        binding.etApellido.isEnabled = habilitar
        binding.etEmail.isEnabled = habilitar
        binding.etUsuario.isEnabled = habilitar
        binding.etPassword.isEnabled = habilitar
        binding.etTelefono.isEnabled = habilitar
    }

    /**
     * Limpia los textos de los campos de la interfaz
     */
    private fun limpiarDatos() {
        binding.etNombre.setText("")
        binding.etApellido.setText("")
        binding.etEmail.setText("")
        binding.etUsuario.setText("")
        binding.etPassword.setText("")
        binding.etTelefono.setText("")
        binding.rbDueno.isChecked = true
        rolSeleccionado = "dueno"
        limpiarErrores()
    }

    /**
     * Muestra un mensaje de error, éxito y advertencia al usuario
     * @param tipoMensaje Tipo de mensaje (ERROR, ÉXITO, ADVERTENCIA)
     * @param mensaje Mensaje a mostrar
     */
    private fun mostrarMensaje(tipoMensaje: String, mensaje: String) {
        if (mensajeDialog == null) {
            // Crear el diálogo de mensaje personalizado
            val mensajeView = mensajeGenericoDialog(this).apply {
                setTitle(tipoMensaje)
                setSubTitle("Registro de Usuario")
                setDescription(mensaje)
            }

            mensajeDialog = AlertDialog.Builder(this)
                .setView(mensajeView)
                .setCancelable(false)
                .create()

            // Configurar el botón de cerrar
            mensajeView.getBinding().btnClose.setOnClickListener {
                mensajeDialog?.dismiss()
                mensajeDialog = null
                habilitarBotones(true)

                // Si fue exitoso, navegar al MainActivity
                if (tipoMensaje == "ÉXITO") {
                    navegarAMainActivity()
                } else {
                    // Si fue error, limpiar solo algunos campos sensibles
                    binding.etPassword.setText("")
                }
            }
        }
        mensajeDialog?.show()
        habilitarBotones(false)
    }

    /**
     * Navega a la actividad principal después del registro exitoso
     */
    private fun navegarAMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Limpiar el stack de actividades para evitar volver al registro
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
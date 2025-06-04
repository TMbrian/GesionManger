package com.example.gestionmanager.ui.auth.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gestionmanager.R
import com.example.gestionmanager.databinding.DialogGenericoProgessbarBinding

class GenericProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: DialogGenericoProgessbarBinding

    private var onCancelListener: (() -> Unit)? = null
    private var onPauseListener: (() -> Unit)? = null
    private var isPaused = false

    init {
        binding = DialogGenericoProgessbarBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            onCancelListener?.invoke()
        }

        binding.btnPause.setOnClickListener {
            togglePause()
            onPauseListener?.invoke()
        }
    }

    // Configurar progreso
    fun setProgress(progress: Int) {
        binding.circularProgress.progress = progress
        binding.linearProgress.progress = progress
        binding.tvProgressPercentage.text = "$progress%"
    }

    // Configurar modo indeterminado
    fun setIndeterminate(indeterminate: Boolean) {
        binding.circularProgress.isIndeterminate = indeterminate
        binding.linearProgress.isIndeterminate = indeterminate
        if (indeterminate) {
            binding.tvProgressPercentage.text = ""
        }
    }

    // Configurar textos
    fun setTitle(title: String) {
        binding.tvProgressTitle.text = title
    }

    fun setDescription(description: String) {
        binding.tvProgressDescription.text = description
    }

    fun setDetails(details: String) {
        binding.tvProgressDetails.text = details
        binding.tvProgressDetails.visibility = if (details.isNotEmpty()) View.VISIBLE else View.GONE
    }

    // Mostrar/ocultar progress linear
    fun showLinearProgress(show: Boolean) {
        binding.linearProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    // Mostrar/ocultar botones
    fun showButtons(show: Boolean) {
        binding.buttonsContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    // Configurar listeners
    fun setOnCancelListener(listener: () -> Unit) {
        onCancelListener = listener
    }

    fun setOnPauseListener(listener: () -> Unit) {
        onPauseListener = listener
    }

    // Pausar/reanudar
    private fun togglePause() {
        isPaused = !isPaused
        binding.btnPause.text = if (isPaused) {
            context.getString(R.string.resume)
        } else {
            context.getString(R.string.pause)
        }
    }

    // Estados del progreso
    fun showSuccess() {
        setProgress(100)
        setTitle(context.getString(R.string.completed))
        setDescription(context.getString(R.string.operation_completed))
        showButtons(false)
    }

    fun showError(errorMessage: String) {
        setTitle(context.getString(R.string.error))
        setDescription(errorMessage)
        binding.btnPause.text = context.getString(R.string.retry)
    }

    // Métodos adicionales para mayor flexibilidad
    fun setProgressColor(colorResId: Int) {
        val color = context.getColor(colorResId)
        binding.circularProgress.setIndicatorColor(color)
        binding.linearProgress.setIndicatorColor(color)
    }

    fun setMaxProgress(max: Int) {
        binding.circularProgress.max = max
        binding.linearProgress.max = max
    }

    fun getProgress(): Int {
        return binding.circularProgress.progress
    }

    fun isIndeterminate(): Boolean {
        return binding.circularProgress.isIndeterminate
    }

    // Método para acceder al binding si se necesita personalización adicional
    fun getBinding(): DialogGenericoProgessbarBinding {
        return binding
    }
}
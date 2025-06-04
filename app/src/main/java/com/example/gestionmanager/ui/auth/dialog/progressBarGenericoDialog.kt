package com.example.gestionmanager.ui.auth.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gestionmanager.databinding.DialogGenericoProgessbarBinding

class progressBarGenericoDialog @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: DialogGenericoProgessbarBinding = DialogGenericoProgessbarBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

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

    // Mostrar/ocultar progress linear
    fun showLinearProgress(show: Boolean) {
        binding.linearProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    // Método para acceder al binding si se necesita personalización adicional
    fun getBinding(): DialogGenericoProgessbarBinding {
        return binding
    }
}
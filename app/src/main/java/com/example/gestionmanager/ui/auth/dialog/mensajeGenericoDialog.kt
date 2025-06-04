package com.example.gestionmanager.ui.auth.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.gestionmanager.R
import com.example.gestionmanager.databinding.DialogRespuestaGenericaBinding

class mensajeGenericoDialog @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: DialogRespuestaGenericaBinding = DialogRespuestaGenericaBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun setTitle(title: String) {
        val (formattedTitle, tintColor) = when(title.uppercase().trim()) {
            "ERROR" -> Pair(
                "MENSAJE DE ERROR",
                ContextCompat.getColor(context, R.color.error) // Rojo
            )
            "EXITO" -> Pair(
                "MENSAJE DE ÉXITO",
                ContextCompat.getColor(context, R.color.green_800) // Verde
            )
            "ADVERTENCIA" -> Pair(
                "MENSAJE DE ADVERTENCIA",
                ContextCompat.getColor(context, R.color.amber_700) // Amarillo/Naranja
            )
            else -> Pair(
                title,
                ContextCompat.getColor(context, R.color.blue_800) // Color por defecto
            )
        }

        with(binding) {
            // Configurar título y color del texto
            tvErrorTitleNter.text = formattedTitle
            tvErrorTitleNter.setTextColor(tintColor)

            // Aplicar tint al ícono (usando el mismo drawable)
            ivIcon.setColorFilter(tintColor)

            // Opcional: Si el fondo del ícono también debe cambiar
            ivIcon.background?.setTint(
                ContextCompat.getColor(context, R.color.blue_800) // Ej: color de fondo neutral
            )
        }
    }

    // Configurar textos
    fun setSubTitle(subTitle: String) {
        binding.tvErrorDetails.text = subTitle
    }

    fun setDescription(description: String) {
        binding.tvMensajeTitle.text = description
    }

    // Método para acceder al binding si se necesita personalización adicional
    fun getBinding(): DialogRespuestaGenericaBinding {
        return binding
    }
}
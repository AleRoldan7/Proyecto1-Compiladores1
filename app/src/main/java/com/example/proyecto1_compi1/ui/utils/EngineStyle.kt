package com.example.proyecto1_compi1.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.example.proyecto1_compi1.modelo.color_style.BackgroundStyle
import com.example.proyecto1_compi1.modelo.color_style.BorderStyle
import com.example.proyecto1_compi1.modelo.color_style.ColorStyle
import com.example.proyecto1_compi1.modelo.color_style.FontStyle
import com.example.proyecto1_compi1.modelo.color_style.TextSizeStyle

object EngineStyle {

    fun resolveStyle(styles: List<Any>?) : ResultStyle {
        var textColor = Color.Black
        var backgroundColor = Color.White
        var textSize = 16
        var borderWidth = 0
        var borderColor = Color.Black
        var fontFamily = FontFamily.Default

        styles?.forEach { style ->

            when (style) {

                is ColorStyle -> {
                    textColor = style.color.colorCompose()
                }

                is BackgroundStyle -> {
                    backgroundColor = style.color.colorCompose()
                }

                is TextSizeStyle -> {
                    textSize = style.size
                }

                is BorderStyle -> {
                    borderWidth = style.width
                    borderColor = style.color.colorCompose()
                }

                is FontStyle -> {
                    fontFamily = when(style.font.uppercase()){

                        "MONO" -> FontFamily.Monospace
                        "SANS_SERIF" -> FontFamily.SansSerif
                        "CURSIVE" -> FontFamily.Cursive

                        else -> FontFamily.Default
                    }
                }
            }
        }

        return ResultStyle(
            textColor,
            backgroundColor,
            textSize,
            borderWidth,
            borderColor,
            fontFamily
        )
    }
}
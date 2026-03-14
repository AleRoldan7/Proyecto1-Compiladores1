package com.example.proyecto1_compi1.modelo.color_style

import androidx.compose.ui.graphics.Color

class RgbColor(private val r: Int, private val g: Int, private val b: Int) : ColorValue {

    override fun colorCompose(): Color {
        return Color(r, g, b)
    }
}
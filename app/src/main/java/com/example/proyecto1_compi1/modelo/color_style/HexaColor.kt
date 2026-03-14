package com.example.proyecto1_compi1.modelo.color_style

import androidx.compose.ui.graphics.Color

class HexaColor(private val hexaColor: String) : ColorValue {

    override fun colorCompose(): Color {

        val colorLong = hexaColor.removePrefix("#").toLong(16)

        val argb = 0xFF000000 or colorLong

        return Color(argb)

    }
}
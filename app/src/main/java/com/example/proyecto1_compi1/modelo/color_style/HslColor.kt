package com.example.proyecto1_compi1.modelo.color_style

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

class HslColor(private val h: Float, private val s: Float, private val l: Float) : ColorValue {

    fun toPKM(): String = "<$h,$s,$l>"

    override fun colorCompose(): Color {

        val C = (1 - abs(2 * l - 1)) * s
        val X = C * (1 - abs((h / 60 % 2) - 1))
        val m = l - C / 2

        var r = 0f
        var g = 0f
        var b = 0f

        when {
            h < 60 -> {
                r = C; g = X
            }

            h < 120 -> {
                r = X; g = C
            }

            h < 180 -> {
                g = C; b = X
            }

            h < 240 -> {
                g = X; b = C
            }

            h < 300 -> {
                r = X; b = C
            }

            else -> {
                r = C; b = X
            }
        }

        return Color(
            r + m,
            g + m,
            b + m,
            1f
        )


    }
}
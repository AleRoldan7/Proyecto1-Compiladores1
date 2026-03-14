package com.example.proyecto1_compi1.modelo.color_style

import androidx.compose.ui.graphics.Color

enum class BaseColor(
    private val color: Color
) : ColorValue {

    RED(Color.Red),
    BLUE(Color.Blue),
    GREEN(Color.Green),
    PURPLE(Color(128,0,128)),
    SKY(Color(135,206,235)),
    YELLOW(Color.Yellow),
    BLACK(Color.Black),
    WHITE(Color.White);

    override fun colorCompose(): Color {
        return color
    }

}
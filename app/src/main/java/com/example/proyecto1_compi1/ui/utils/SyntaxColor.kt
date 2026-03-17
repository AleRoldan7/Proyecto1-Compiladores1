package com.example.proyecto1_compi1.ui.utils

import androidx.compose.ui.graphics.Color

object SyntaxColor {

    val OPERATOR = Color(0xFF4CAF50)      // Verde
    val VARIABLE = Color.White             // Blanco
    val STRING = Color(0xFFFF9800)         // Naranja
    val NUMBER = Color(0xFF03A9F4)         // Celeste
    val KEYWORD = Color(0xFF9C27B0)         // Morado
    val BRACKET = Color(0xFF2196F3)         // Azul
    val EMOJI = Color(0xFFFFEB3B)           // Amarillo
    val DEFAULT = Color.White                // Blanco

    val KEYWORDS = setOf(
        "formulario", "título", "pregunta", "texto", "numero",
        "fecha", "hora", "imagen", "emojis", "opciones",
        "obligatorio", "multiple", "rango", "min", "max",
        "verdadero", "falso", "si", "no", "entero", "decimal",
        "cadena", "booleano"
    )

    val OPERATORS = setOf('+', '-', '*', '/', '=', '>', '<', '!', '&', '|', '%')

    val BRACKETS = setOf('{', '}', '(', ')', '[', ']')
}
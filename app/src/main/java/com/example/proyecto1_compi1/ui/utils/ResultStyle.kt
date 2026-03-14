package com.example.proyecto1_compi1.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily


data class ResultStyle(

    val textColor: Color = Color.Black,
    val backgroundColor: Color = Color.White,
    val textSize: Int = 16,
    val borderWidth: Int = 0,
    val borderColor: Color = Color.Black,
    val fontFamily: FontFamily = FontFamily.Default
)
package com.example.proyecto1_compi1.ui.table

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1.modelo.table.*

@Composable
fun TableUI(table: TableModel) {

    var width = 200
    var height = 150
    var borderSize = 1
    var borderColor = Color.Black

    table.properties.forEach { prop ->

        when (prop) {

            is PropertyWidth -> width = (prop.value as Int)

            is PropertyHeigth -> height = (prop.value as Int)

            is PropertyStyle -> {

                prop.styles.forEach { style ->

                    if (style is StyleBorder) {

                        val grosor = style.type

                        borderSize = when (grosor) {
                            is Int -> grosor
                            is Double -> grosor.toInt()
                            else -> 1
                        }

                        borderColor = when (style.color.lowercase()) {
                            "rojo" -> Color.Red
                            "azul" -> Color.Blue
                            "verde" -> Color.Green
                            else -> Color.Black
                        }

                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .border(borderSize.dp, borderColor)
    ) {
        Text("Tabla")
    }
}
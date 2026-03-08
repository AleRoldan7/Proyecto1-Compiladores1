package com.example.proyecto1_compi1.ui.table

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1.modelo.question.DropQuestion
import com.example.proyecto1_compi1.modelo.question.MultipleQuestion
import com.example.proyecto1_compi1.modelo.question.OpenQuestion
import com.example.proyecto1_compi1.modelo.question.QuestionRender
import com.example.proyecto1_compi1.modelo.question.SelectQuestion
import com.example.proyecto1_compi1.modelo.table.*
import com.example.proyecto1_compi1.ui.question.DropQuestionUI
import kotlin.math.log

@Composable
fun TableUI(table: TableModel) {

    var width = 200
    var height = 150
    var borderSize = 1
    var borderColor = Color.Black
    var elements: List<List<TableCell>> = emptyList()

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

            is PropertyElements -> {
                elements = prop.matrix
                Log.d("ELEMENTOS" , elements.toString())
            }
        }
    }

    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .border(borderSize.dp, borderColor)
            .padding(8.dp)
    ) {
        Column {

            elements.forEach { row ->

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    row.forEach { cell ->

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, borderColor)
                                .padding(8.dp)
                        ) {

                            Column {

                                cell.content.forEach { item ->

                                    when (item) {

                                        is String -> {
                                            Text(text = item)
                                        }

                                        is OpenQuestion,
                                        is DropQuestion,
                                        is SelectQuestion,
                                        is MultipleQuestion -> {

                                            QuestionRender(item)
                                        }

                                        else -> {
                                            Text(text = item.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
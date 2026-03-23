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
import com.example.proyecto1_compi1.ui.utils.RenderElement

@Composable
fun TableUI(table: TableModel) {

    // Verificar si la tabla está vacía
    if (table.elements.isEmpty()) {
        Box(
            modifier = Modifier
                .width(table.width.dp)
                .height(table.height.dp)
                .border(1.dp, Color.Gray),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Tabla vacía")
        }
        return
    }

    // Obtener número de filas y columnas de forma segura
    val rows = table.elements.size
    val firstRow = table.elements.firstOrNull()
    val cols = firstRow?.size ?: 1

    // Validar que rows y cols sean mayores a 0 para evitar división por cero
    val safeRows = if (rows > 0) rows else 1
    val safeCols = if (cols > 0) cols else 1

    // Calcular dimensiones de celdas con valores por defecto seguros
    val cellWidth = if (table.width > 0 && safeCols > 0) {
        table.width / safeCols
    } else {
        150 // Ancho por defecto
    }

    val cellHeight = if (table.height > 0 && safeRows > 0) {
        table.height / safeRows
    } else {
        80 // Alto por defecto
    }

    // Usar valores por defecto si las dimensiones son 0
    val tableWidth = if (table.width > 0) table.width else 400
    val tableHeight = if (table.height > 0) table.height else 300

    Column(
        modifier = Modifier
            .width(tableWidth.dp)
            .height(tableHeight.dp)
            .border(1.dp, Color.LightGray)
    ) {
        table.elements.forEachIndexed { rowIndex, row ->

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEachIndexed { colIndex, cell ->

                    Box(
                        modifier = Modifier
                            .width(cellWidth.dp)
                            .height(cellHeight.dp)
                            .border(1.dp, Color.LightGray)
                            .padding(4.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        RenderElement(cell)
                    }

                }

                // Si la fila tiene menos columnas que el resto, llenar con celdas vacías
                val emptyCells = safeCols - row.size
                if (emptyCells > 0) {
                    repeat(emptyCells) {
                        Box(
                            modifier = Modifier
                                .width(cellWidth.dp)
                                .height(cellHeight.dp)
                                .border(1.dp, Color.LightGray)
                                .padding(4.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text("")
                        }
                    }
                }
            }
        }
    }
}
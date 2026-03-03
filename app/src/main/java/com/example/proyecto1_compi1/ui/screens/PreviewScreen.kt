package com.example.proyecto1_compi1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.modelo.question.DropQuestion
import com.example.proyecto1_compi1.modelo.question.MultipleQuestion
import com.example.proyecto1_compi1.modelo.question.OpenQuestion
import com.example.proyecto1_compi1.modelo.question.SelectQuestion
import com.example.proyecto1_compi1.ui.question.DropQuestionUI
import com.example.proyecto1_compi1.ui.question.MultipleQuestionUI
import com.example.proyecto1_compi1.ui.question.SelectQuestionUI
import com.example.proyecto1_compi1.ui.table.TableUI

@Composable
fun PreviewScreen(navController: NavController) {

    val forms = ResultParser.formsModel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Vista previa formulario",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (forms != null) {

            Text(
                text = "Formulario: ${forms.name}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            forms.questions.forEach { question ->

                when (question) {

                    is OpenQuestion -> {

                        Text(question.name)

                        var text by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Respuesta") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    is DropQuestion -> {

                        Text(question.name)

                        Spacer(modifier = Modifier.height(8.dp))

                        DropQuestionUI(question.options)
                    }

                    is SelectQuestion -> {

                        Text(question.name)

                        Spacer(modifier = Modifier.height(8.dp))

                        SelectQuestionUI(question.options)
                    }

                    is MultipleQuestion -> {

                        Text(question.name)

                        Spacer(modifier = Modifier.height(8.dp))

                        MultipleQuestionUI(question.options)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            forms.tables.forEach { table ->

                Spacer(modifier = Modifier.height(20.dp))

                TableUI(table)

            }

        } else {

            Text("No hay formulario generado aún.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.popBackStack() }
        ) {
            Text("Regresar al Editor")
        }
    }
}
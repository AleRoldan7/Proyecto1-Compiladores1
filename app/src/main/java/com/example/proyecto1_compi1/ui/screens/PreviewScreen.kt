package com.example.proyecto1_compi1.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.modelo.question.DropQuestion
import com.example.proyecto1_compi1.modelo.question.MultipleQuestion
import com.example.proyecto1_compi1.modelo.question.OpenQuestion
import com.example.proyecto1_compi1.modelo.question.QuestionRender
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
            .verticalScroll(rememberScrollState())
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

                QuestionRender(question)
                Spacer(modifier = Modifier.height(20.dp))
            }

            forms.tables.forEach { table ->

                Spacer(modifier = Modifier.height(20.dp))

                TableUI(table)

            }

            if (forms.specialQuestions.isNotEmpty()) {
                Text("Preguntas especiales", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                forms.specialQuestions.forEachIndexed { index, sq ->
                    Log.d("PreviewDebug", "Renderizando special #${index+1}: ${sq.name} (${sq.type})")
                    QuestionRender(sq)
                    Spacer(Modifier.height(24.dp))
                }
            }

        } else {
            Text("No hay formulario generado aún.")
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Regresar")
        }
    }
}
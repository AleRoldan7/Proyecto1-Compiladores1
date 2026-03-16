package com.example.proyecto1_compi1.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.modelo.forms.FormsModel
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.modelo.question.DropQuestion
import com.example.proyecto1_compi1.modelo.question.MultipleQuestion
import com.example.proyecto1_compi1.modelo.question.OpenQuestion
import com.example.proyecto1_compi1.modelo.question.SelectQuestion
import com.example.proyecto1_compi1.ui.utils.RenderElement

@Composable
fun AnswerScreen() {

    val forms = ResultParser.forms

    val questions = remember {

        forms.flatMap { form ->

            form.elements.filter {

                it is OpenQuestion ||
                        it is DropQuestion ||
                        it is SelectQuestion ||
                        it is MultipleQuestion
            }
        }
    }

    var page by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Pregunta ${page + 1} / ${questions.size}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(20.dp))

        RenderElement(questions[page])

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                enabled = page > 0,
                onClick = { page-- }
            ) {
                Text("Anterior")
            }

            Button(
                enabled = page < questions.size - 1,
                onClick = { page++ }
            ) {
                Text("Siguiente")
            }
        }

        Spacer(Modifier.height(20.dp))

        if (page == questions.size - 1) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    /*
                    Toast
                        .makeText(
                            LocalContext.current,
                            "Formulario enviado",
                            Toast.LENGTH_LONG
                        )
                        .show()
*/
                }
            ) {
                Text("Enviar formulario")
            }
        }
    }
}
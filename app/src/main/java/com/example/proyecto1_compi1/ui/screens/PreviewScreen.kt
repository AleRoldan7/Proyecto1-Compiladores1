package com.example.proyecto1_compi1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.modelo.ResultParser

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

                Text(text = question.name)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Respuesta") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
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
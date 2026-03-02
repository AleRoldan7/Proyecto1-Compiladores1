package com.example.proyecto1_compi1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AnswerScreen(navController: NavController) {

    var answer by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(text = "Contestar Formulario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Respuesta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Aquí validas respuestas
            }
        ) {
            Text("Enviar")
        }
    }
}
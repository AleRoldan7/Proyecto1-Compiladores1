package com.example.proyecto1_compi1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1_compi1.ui.theme.Proyecto1Compi1Theme

@Composable
fun HomeScreen(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "PKM_FORMS",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                navController.navigate("edit")
            }
        ) {
            Text("Crear / Editar Formularios")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("upload")
            }
        ) {
            Text("Cargar Archivos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("server")
            }
        ) {
            Text("Cagar Formularios Servidor")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("answer")
            }
        ) {
            Text("Responder Formularios")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Proyecto1Compi1Theme {
        HomeScreen(navController = rememberNavController())
    }
}
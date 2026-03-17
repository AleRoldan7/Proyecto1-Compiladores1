package com.example.proyecto1_compi1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.analizador.form.Lexer
import com.example.proyecto1_compi1.analizador.form.Parser
import com.example.proyecto1_compi1.storage.getFormsStorage
import java.io.StringReader

@Composable
fun FormsSaveScreen(navController: NavController) {

    val context = LocalContext.current
    val archivos = remember { getFormsStorage(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Formularios guardados",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        archivos.forEach { archivo ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(archivo.name)

                    Button(
                        onClick = {

                            val codigo = archivo.readText()

                            val lexer = Lexer(StringReader(codigo))
                            val parser = Parser(lexer)

                            parser.parse()

                            navController.navigate("answer")

                        }
                    ) {
                        Text("Responder")
                    }
                }
            }
        }
    }
}
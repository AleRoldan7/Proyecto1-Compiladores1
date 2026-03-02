package com.example.proyecto1_compi1.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1_compi1.analizador.Lexer
import com.example.proyecto1_compi1.analizador.Parser
import com.example.proyecto1_compi1.modelo.ResultParser
import com.example.proyecto1_compi1.ui.theme.Proyecto1Compi1Theme
import java.io.StringReader
import android.util.Log

@Composable
fun EditScreen(navController: NavController? = null) {

    var editorText by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {

        Text(
            text = "Editor de Formularios",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                BasicTextField(
                    value = editorText,
                    onValueChange = { editorText = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = {

                try {
                    Lexer.listaError.clear()
                    Parser.listaErrores.clear()

                    val lexer = Lexer(StringReader(editorText.text))
                    val parser = Parser(lexer)

                    parser.parse()

                    val forms = ResultParser.formsModel

                    if (forms != null) {

                        Log.d("FORM_DEBUG", "Formulario: ${forms.name}")

                        forms.questions.forEach {
                            Log.d("FORM_DEBUG", "Pregunta: ${it.name} - Tipo: ${it.type}")
                        }

                    } else {
                        Log.e("FORM_DEBUG", "formsModel es NULL")
                    }

                    if (Lexer.listaError.isEmpty() && Parser.listaErrores.isEmpty()) {
                        Toast.makeText(context, "Formulario válido", Toast.LENGTH_LONG).show()
                    } else {
                        val totalErrores =
                            Lexer.listaError.size + Parser.listaErrores.size

                        Toast.makeText(
                            context,
                            "Se encontraron $totalErrores errores",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    Log.e("FORM_DEBUG", "Error en parseo", e)
                }
            }
        ) {
            Text("Analizar Formulario")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    navController?.navigate("preview")
                }
            ) {
                Text("Vista Previa")
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    navController?.navigate("home")
                }
            ) {
                Text("Home")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun EditPreview() {
    Proyecto1Compi1Theme {
        EditScreen(navController = rememberNavController())
    }
}
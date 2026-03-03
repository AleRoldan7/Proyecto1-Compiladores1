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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1_compi1.analizador.Lexer
import com.example.proyecto1_compi1.analizador.Parser
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.ui.theme.Proyecto1Compi1Theme
import java.io.StringReader
import android.util.Log

@Composable
fun EditScreen(navController: NavController? = null) {

    var editorText by remember { mutableStateOf(TextFieldValue("")) }
    var erroresTexto by remember { mutableStateOf("") }
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

                    erroresTexto = ""

                    Lexer.listaError.clear()
                    Parser.listaErrores.clear()

                    val lexer = Lexer(StringReader(editorText.text))
                    val parser = Parser(lexer)

                    parser.parse()

                    if (Lexer.listaError.isEmpty() && Parser.listaErrores.isEmpty()) {

                        Toast.makeText(context, "Formulario válido ✅", Toast.LENGTH_LONG).show()

                    } else {

                        val builder = StringBuilder()

                        Lexer.listaError.forEach {
                            builder.append("Error Léxico → Línea ${it.line}, Columna ${it.column}: ${it.description}\n")
                        }

                        Parser.listaErrores.forEach {
                            builder.append("Error Sintáctico → Línea ${it.line}, Columna ${it.column}: ${it.description}\n")
                        }

                        erroresTexto = builder.toString()
                    }

                } catch (e: Exception) {
                    erroresTexto = "Error fatal: ${e.message}"
                    erroresTexto = "Error fatal:\n${e.stackTraceToString()}"
                }
            }


        ) {
            Text("Analizar Formulario")
        }

        if (erroresTexto.isNotEmpty()) {

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = erroresTexto,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        Log.d("DEBUG_FORM", "formsModel = ${ResultParser.formsModel}")
        Log.d("DEBUG_FORM", "questions = ${ResultParser.formsModel?.questions?.size}")
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
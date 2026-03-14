package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.generate_lenguaje.GeneratePKM
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.modelo.question.QuestionRender
import com.example.proyecto1_compi1.ui.table.TableUI
import java.io.File
import androidx.compose.ui.platform.LocalContext
import com.example.proyecto1_compi1.ui.question.TextUi

@Composable
fun PreviewScreen(navController: NavController) {

    val forms = ResultParser.forms
    val texto = GeneratePKM.generate(ResultParser.forms)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("PREVIEW_DEBUG", "Forms size: ${forms?.size}")
        forms?.forEachIndexed { formIndex, form ->
            Log.d("PREVIEW_DEBUG", "Form $formIndex - texts size: ${form.texts?.size}")
            Log.d("PREVIEW_DEBUG", "Form $formIndex - questions size: ${form.questions?.size}")
            Log.d("PREVIEW_DEBUG", "Form $formIndex - tables size: ${form.tables?.size}")
        }
    }

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

        if (forms.isNotEmpty()) {

            forms.forEach { form ->

                Text(
                    text = "Formulario: ${form.name}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                form.questions.forEach { question ->

                    QuestionRender(question)

                    Spacer(modifier = Modifier.height(20.dp))
                }

                form.tables.forEach { table ->

                    Spacer(modifier = Modifier.height(20.dp))

                    TableUI(table)
                }

                form.texts.forEach { text ->

                    Log.d("PREVIEW_DEBUG", "Text $text")
                    TextUi(text)

                    Spacer(modifier = Modifier.height(20.dp))
                }
                if (form.specialQuestions.isNotEmpty()) {

                    Text(
                        "Preguntas especiales",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    form.specialQuestions.forEachIndexed { index, sq ->

                        Log.d(
                            "PreviewDebug",
                            "Renderizando special #${index + 1}: ${sq.name} (${sq.type})"
                        )

                        QuestionRender(sq)

                        Spacer(Modifier.height(24.dp))
                    }
                }



                Spacer(modifier = Modifier.height(40.dp))
            }

        } else {

            Text("No hay formularios generados.")
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {

                val nombre = "form_${System.currentTimeMillis()}.pkm"

                guardarFormulario(context,nombre,texto)


            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar formulario (.pkm)")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Regresar")
        }
    }
}

fun guardarFormulario(context: Context, nombre: String, contenido: String) {

    val cartpeta = File(context.getExternalFilesDir(null), "formularios")

    if (!cartpeta.exists()) {
        cartpeta.mkdirs()
    }

    val archivo = File(cartpeta, nombre)

    archivo.writeText(contenido)

    Log.d("ARCHIVO", "GUARDANDO EN: ${archivo.absolutePath}")
}
package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import java.io.File
import androidx.compose.ui.platform.LocalContext
import com.example.proyecto1_compi1.ui.utils.PKMGenerateHeader
import com.example.proyecto1_compi1.ui.utils.RenderElement

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreviewScreen(navController: NavController) {

    val forms = ResultParser.forms
    val texto = GeneratePKM.generate(ResultParser.forms)
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var autor by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var nameForm by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Log.d("PREVIEW_DEBUG", "Forms size: ${forms?.size}")
        forms?.forEachIndexed { formIndex, form ->

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

                Log.d("PREVIEW_DEBUG", "Elementos: ${form.elements.size}")
                Text(
                    text = "Formulario: ${form.name}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                form.elements.forEach { element ->

                    RenderElement(element)
                }


                Spacer(modifier = Modifier.height(40.dp))
            }

        } else {

            Text("No hay formularios generados.")
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar formulario (.pkm)")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Regresar")
        }

        if (showDialog) {

            AlertDialog(

                onDismissRequest = { showDialog = false },

                title = {
                    Text("Datos del formulario")
                },

                text = {

                    Column {

                        OutlinedTextField(
                            value = nameForm,
                            onValueChange = { nameForm = it },
                            label = { Text("Nombre del formulario") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = autor,
                            onValueChange = { autor = it },
                            label = { Text("Autor") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )

                    }
                },

                confirmButton = {

                    TextButton(onClick = {

                        val nombre = "${nameForm}.pkm"

                        val contenidoFinal = generarPKMCompleto(
                            nameForm,
                            autor,
                            description,
                            forms
                        )

                        guardarFormulario(context, nombre, contenidoFinal)

                        showDialog = false

                    }) {

                        Text("Guardar")
                    }
                },

                dismissButton = {

                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }

                }

            )
        }
    }
}

fun guardarFormulario(context: Context, nombre: String, contenido: String) {

    val cartpeta = File(context.getExternalFilesDir(null), "formularios")

    if (!cartpeta.exists()) {
        cartpeta.mkdirs()
    }

    val archivo = File(cartpeta, nombre)

    if (archivo.exists()) {

        Toast.makeText(context, "Ya existe un formulario con ese nombre", Toast.LENGTH_LONG).show()
    }
    archivo.writeText(contenido)

    Log.d("ARCHIVO", "GUARDANDO EN: ${archivo.absolutePath}")
}

@RequiresApi(Build.VERSION_CODES.O)
fun generarPKMCompleto(
    nameForm: String,
    autor: String,
    descripcion: String,
    forms: List<com.example.proyecto1_compi1.modelo.forms.FormsModel>
): String {

    val fecha = java.time.LocalDate.now()
    val hora = java.time.LocalTime.now().withNano(0)

    var totalSecciones = 0
    var totalPreguntas = 0

    val contenido = StringBuilder()

    forms.forEach { form ->

        form.elements.forEach { element ->

            // --- Section ---
            if (element is com.example.proyecto1_compi1.modelo.question.SectionsModel) {
                totalSecciones++

                contenido.append("<section=${element.width},${element.height},${element.pointX},${element.pointY},${element.orientation}>\n")

                // Agrega estilos si hay
                if (!element.styles.isNullOrEmpty()) {
                    contenido.append("<style>\n")
                    element.styles.forEach { s -> contenido.append("$s\n") }
                    contenido.append("</style>\n")
                }

                // Contenido de la sección
                contenido.append("<content>\n")
                element.elements.forEach { subElement ->
                    contenido.append(serializeElement(subElement))
                }
                contenido.append("</content>\n")
                contenido.append("</section>\n\n")
            }

            // --- Preguntas fuera de sección ---
            if (element is com.example.proyecto1_compi1.modelo.question.QuestionModel) {
                totalPreguntas++
                contenido.append(serializeElement(element))
            }

        }
    }

    return buildString {
        appendLine("###")
        appendLine("FormName: $nameForm")
        appendLine("Author: $autor")
        appendLine("Date: $fecha")
        appendLine("Time: $hora")
        appendLine("Description: $descripcion")
        appendLine("Sections: $totalSecciones")
        appendLine("Questions: $totalPreguntas")
        appendLine("###")
        appendLine()

        append(contenido.toString())
    }
}

fun serializeElement(element: Any): String {
    return when (element) {
        is com.example.proyecto1_compi1.modelo.question.TextModel -> {
            "<open=${element.width},${element.height},\${element.content}/open>\n"
        }

        is com.example.proyecto1_compi1.modelo.question.OpenQuestion -> {
            "<open=${element.width},${element.height},\"${element.label}\"/open>\n"
        }

        is com.example.proyecto1_compi1.modelo.question.SelectQuestion -> {
            val options = element.options.joinToString(",") { "\"$it\"" }
            val correct = element.correct.joinToString(",")
            "<select=${element.width},${element.height},\"${element.label}\",{${options}},${correct}/select>\n"
        }

        is com.example.proyecto1_compi1.modelo.question.DropQuestion -> {
            val options = element.options.joinToString(",") { "\"$it\"" }
            val correct = element.correct.joinToString(",")
            "<drop=${element.width},${element.height},\"${element.label}\",{${options}},${correct}/drop>\n"
        }

        is com.example.proyecto1_compi1.modelo.question.MultipleQuestion -> {
            val options = element.options.joinToString(",") { "\"$it\"" }
            val correct = element.correct.joinToString(",")
            "<multiple=${element.width},${element.height},\"${element.label}\",{${options}},{${correct}}/multiple>\n"
        }

        else -> ""
    }
}
package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.analizador.form.Lexer
import com.example.proyecto1_compi1.analizador.form.Parser
import com.example.proyecto1_compi1.generate_lenguaje.PKMCache
import com.example.proyecto1_compi1.generate_lenguaje.ReadPKM
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.ui.utils.RenderElement
import java.io.File
import java.io.StringReader


@Composable
fun UploadFilesScreen(navController: NavController) {
    val context = LocalContext.current
    val archivos = remember { getForms(context) }
    val loader = remember { ReadPKM(context) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Formularios guardados", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(archivos) { file ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(file.name)
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                // Cargar el archivo .pkm
                                PKMCache.elements = loader.loadPKM(file)
                                // Navegar a preview
                                navController.navigate("answer")
                            }
                        ) {
                            Text("Responder formulario")
                        }
                    }
                }
            }
        }
    }

}
fun getForms(context: Context): List<File> {
    val carpeta = File(context.getExternalFilesDir(null), "formularios")

    if (!carpeta.exists()) {
        carpeta.mkdirs()
    }

    return carpeta.listFiles()?.toList() ?: emptyList()
}
/*
fun UploadFilesScreen(navController: NavController) {

    val context = LocalContext.current

    val archivos = remember { getForms(context) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(text = "Formularios guardados", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(archivos) { file ->

                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(file.name)
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {

                                val contenido = readForms(file)

                                Log.d("FORM", contenido)

                               paseForms(contenido)

                                navController.navigate("preview")

                            }
                        ) {

                            Text("Cargar formulario")

                        }

                    }
                }

            }
        }
    }


}


fun getForms(context: Context): List<File> {
    val carpeta = File(context.getExternalFilesDir(null), "formularios")

    if (!carpeta.exists()) {
        carpeta.mkdirs()
    }

    return carpeta.listFiles()?.toList() ?: emptyList()
}

fun readForms(file: File): String {

    return file.readText(Charsets.UTF_8)
}


fun paseForms(text: String) {

    ResultParser.reset()

    val reader = StringReader(text)
    val lexer = Lexer(reader)
    val parser = Parser(lexer)

    parser.parse()

}

 */
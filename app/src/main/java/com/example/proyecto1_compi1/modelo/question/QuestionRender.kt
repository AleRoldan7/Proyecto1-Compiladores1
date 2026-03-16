package com.example.proyecto1_compi1.modelo.question

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1.ui.question.DropQuestionUI
import com.example.proyecto1_compi1.ui.question.MultipleQuestionUI
import com.example.proyecto1_compi1.ui.question.SelectQuestionUI
import com.example.proyecto1_compi1.ui.question.TextUi

@Composable
fun QuestionRender(question: QuestionModel) {

    Log.d("QuestionRender", "Tipo recibido: ${question.javaClass.simpleName}")

    when (question) {

        is OpenQuestion -> {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Text(
                    text = question.label ?: "",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                var text by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Respuesta") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        is DropQuestion -> {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Text(
                    text = question.label ?: "",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                DropQuestionUI(question.options)
            }
        }

        is SelectQuestion -> {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Text(
                    text = question.label ?: "",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                SelectQuestionUI(question.options)
            }
        }

        is MultipleQuestion -> {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Text(
                    text = question.label ?: "",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                MultipleQuestionUI(question.options)
            }
        }


        else -> {

            Text(
                text = "Elemento no soportado: ${question.javaClass.simpleName}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}



/*
@Composable
fun QuestionRender(question: Any) {
    Log.d("QuestionRender", "Tipo recibido: ${question.javaClass.simpleName}")
    when (question) {

        is OpenQuestion -> {

            Text(question.name)

            var text by remember { mutableStateOf("") }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Respuesta") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        is DropQuestion -> {
            Text(question.name)
            Spacer(modifier = Modifier.height(8.dp))
            DropQuestionUI(question.options)

        }

        is SelectQuestion -> {
            Text(question.name)
            Spacer(modifier = Modifier.height(8.dp))
            SelectQuestionUI(question.options)
        }

        is MultipleQuestion -> {
            Text(question.name)
            Spacer(modifier = Modifier.height(8.dp))
            MultipleQuestionUI(question.options)
        }

        is SpecialQuestion -> {
            Log.d("QuestionRender", "Entró a SpecialQuestion → ${question.name}")
            val resolved = question.getResolvedProperties()

            val width = resolved.find { it.key == "width" }?.value as? Number ?: 300
            val height = resolved.find { it.key == "height" }?.value as? Number ?: 120
            val label = resolved.find { it.key == "label" }?.value?.toString() ?: question.name

            Text(
                text = question.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = height.toDouble().dp)
            ) {
                when (question.type.uppercase()) {
                    "OPENQUESTION" -> {
                        var text by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text(label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "DROPQUESTION" -> {
                        val options = resolved.find { it.key == "options" }?.value as? List<String> ?: emptyList()
                        DropQuestionUI(options)
                    }
                    "SELECTQUESTION" -> {
                        val options = resolved.find { it.key == "options" }?.value as? List<String> ?: emptyList()
                        SelectQuestionUI(options)
                    }
                    "MULTIPLEQUESTION" -> {
                        val options = resolved.find { it.key == "options" }?.value as? List<String> ?: emptyList()
                        MultipleQuestionUI(options)
                    }
                    else -> {
                        Text(
                            text = "Tipo especial no soportado: ${question.type}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (resolved == question.properties) {
                Text(
                    text = "(Valores por defecto – faltó .draw() o argumentos insuficientes)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        else -> {
            Text(
                text = "Elemento no soportado: ${question.javaClass.simpleName}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
 */
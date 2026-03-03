package com.example.proyecto1_compi1.ui.question

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment


@Composable
fun MultipleQuestionUI(options: List<String>) {

    val selectedOptions = remember { mutableStateListOf<String>() }

    Column {
        options.forEach { option ->

            Row(verticalAlignment = Alignment.CenterVertically) {

                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedOptions.add(option)
                        } else {
                            selectedOptions.remove(option)
                        }
                    }
                )

                Text(option)
            }
        }
    }
}
package com.example.proyecto1_compi1.ui.question

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment


@Composable
fun SelectQuestionUI(options: List<String>) {

    var selectedOption by remember { mutableStateOf("") }

    Column {
        options.forEach { option ->

            Row(verticalAlignment = Alignment.CenterVertically) {

                RadioButton(
                    selected = selectedOption == option,
                    onClick = { selectedOption = option }
                )

                Text(option)
            }
        }
    }
}
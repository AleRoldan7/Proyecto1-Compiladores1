package com.example.proyecto1_compi1.ui.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class EditViewModel : ViewModel() {

    var editText by mutableStateOf(TextFieldValue(""))
}
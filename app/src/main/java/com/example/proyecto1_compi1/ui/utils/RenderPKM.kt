package com.example.proyecto1_compi1.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1.generate_lenguaje.ReadPKM
import java.io.File

@Composable
fun RenderPKM(pkmFile: File) {

    val context = LocalContext.current
    val loader = remember { ReadPKM(context) }
    var elements by remember { mutableStateOf<List<Any>>(emptyList()) }

    LaunchedEffect(pkmFile) {
        elements = loader.loadPKM(pkmFile)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {

        elements.forEach { element ->
            RenderElement(element)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}
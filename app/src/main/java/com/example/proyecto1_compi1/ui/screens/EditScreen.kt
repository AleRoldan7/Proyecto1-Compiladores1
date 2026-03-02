package com.example.proyecto1_compi1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1_compi1.ui.theme.Proyecto1Compi1Theme

@Composable

fun EditScreen(navController: NavController? = null) {

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = "Editor", style = MaterialTheme.typography.headlineSmall)

    }
}

@Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    Proyecto1Compi1Theme {
        EditScreen(navController = rememberNavController())
    }
}
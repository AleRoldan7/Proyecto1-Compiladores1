package com.example.proyecto1_compi1.ui.screens

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import com.example.proyecto1_compi1.ui.server.view.FormularioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun ServerScreen(viewModel: FormularioViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        viewModel.cargarFormulario()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("SI jalo el servidor")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Formularios totales: ${viewModel.formularios.size}")
        LazyColumn {

            items(viewModel.formularios) { form ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            text = form.nombreArchivo,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "Autor: ${form.autor}",
                            fontSize = 14.sp
                        )

                    }

                }

            }

        }

    }

}


/*
LaunchedEffect(Unit) {
    viewModel.cargarFormularios()
}

Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
) {

    Text(
        text = "Respuesta del servidor:",
        fontSize = 20.sp
    )

    Spacer(modifier = Modifier.height(20.dp))



}


 */

/*
LaunchedEffect(true) {
    viewModel.cargarFormulario()
}

Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

    Text("Formulario Servidor", fontSize = 22.sp)

    Spacer(modifier = Modifier.height(20.dp))

    LazyColumn {

        items(viewModel.formulario){ form ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ){

                Column(
                    modifier = Modifier.padding(12.dp)
                ){

                    Text(form.nombreArchivo)

                    Text(
                        "Autor: ${form.autor}",
                        fontSize = 14.sp
                    )

                    Button(
                        onClick = {
                            //descargarFormulario(form.idFormulario)
                        }
                    ){
                        Text("Descargar")
                    }

                }

            }

        }

    }

}
*/


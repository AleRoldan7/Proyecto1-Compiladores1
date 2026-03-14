package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import com.example.proyecto1_compi1.ui.server.view.FormularioViewModel
import androidx.compose.ui.platform.LocalContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerScreen(
    navController: NavController,
    viewModel: FormularioViewModel = viewModel()
) {

    val context = LocalContext.current

    val formularios by viewModel.formularios.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val uploadResult by viewModel.uploadResult.collectAsState()

    val isDownloading by viewModel.isDownloading.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val downloadResult by viewModel.downloadResult.collectAsState()

    var downloadingId by remember { mutableStateOf<Int?>(null) }

    var showUploadDialog by remember { mutableStateOf(false) }


    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uploadResult) {
        uploadResult?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(downloadResult) {
        downloadResult?.let {

            Toast.makeText(
                context,
                it,
                Toast.LENGTH_LONG
            ).show()

            viewModel.clearMessages()
            downloadingId = null
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servidor de Formularios") },

                navigationIcon = {
                    Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text("←")
                    }
                },

                actions = {

                    Button(
                        onClick = { viewModel.cargarFormularios() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("↻")
                    }

                    Button(
                        onClick = { showUploadDialog = true }
                    ) {
                        Text("+")
                    }

                }

            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (isLoading) {

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Cargando formularios...")

                }

            } else {

                if (formularios.isEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "📭",
                            fontSize = 64.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No hay formularios en el servidor",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Toca el botón + para subir uno",
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.cargarFormularios() }
                        ) {
                            Text("Reintentar")
                        }

                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(formularios) { form ->

                            FormularioCard(

                                formulario = form,

                                isDownloading = isDownloading && downloadingId == form.idFormulario,

                                downloadProgress = downloadProgress,

                                onDownloadClick = {

                                    downloadingId = form.idFormulario

                                    viewModel.descargarFormulario(
                                        form.idFormulario,
                                        form.nombreArchivo,
                                        context
                                    )

                                }

                            )

                        }

                    }

                }

            }

        }

    }

    if (showUploadDialog) {

        UploadFormDialog(

            onDismiss = { showUploadDialog = false },

            onUpload = { file, autor ->

                viewModel.subirFormulario(file, autor)

                showUploadDialog = false

            },

            context = context

        )

    }

}


@Composable
fun FormularioCard(
    formulario: FormularioDTO,
    onDownloadClick: () -> Unit,
    isDownloading: Boolean,
    downloadProgress: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = formulario.nombreArchivo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Autor: ${formulario.autor}",
                    fontSize = 14.sp
                )

                Text(
                    text = "ID: ${formulario.idFormulario}",
                    fontSize = 12.sp
                )

            }

            Button(
                onClick = onDownloadClick,
                modifier = Modifier.padding(start = 8.dp),
                enabled = !isDownloading
            ) {

                if (isDownloading) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 6.dp),
                            strokeWidth = 2.dp
                        )

                        Text("$downloadProgress%")

                    }

                } else {

                    Text("📥 Descargar")

                }

            }

        }

    }

}


@Composable
fun UploadFormDialog(
    onDismiss: () -> Unit,
    onUpload: (File, String) -> Unit,
    context: Context
) {

    var selectedFile by remember { mutableStateOf<File?>(null) }

    var autor by remember { mutableStateOf("") }

    var showFilePicker by remember { mutableStateOf(false) }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = { Text("Subir Formulario .pkm") },

        text = {

            Column {

                OutlinedTextField(
                    value = autor,
                    onValueChange = { autor = it },
                    label = { Text("Autor") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(

                    value = selectedFile?.name ?: "",

                    onValueChange = {},

                    label = { Text("Archivo .pkm") },

                    modifier = Modifier.fillMaxWidth(),

                    readOnly = true,

                    placeholder = { Text("Ningún archivo seleccionado") },

                    trailingIcon = {

                        Button(
                            onClick = { showFilePicker = true }
                        ) {
                            Text("📁")
                        }

                    }

                )

            }

        },

        confirmButton = {

            Button(

                onClick = {

                    if (selectedFile != null && autor.isNotBlank()) {

                        onUpload(selectedFile!!, autor)

                    } else {

                        Toast.makeText(
                            context,
                            "Selecciona un archivo y escribe el autor",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                },

                enabled = selectedFile != null && autor.isNotBlank()

            ) {

                Text("Subir Formulario")

            }

        },

        dismissButton = {

            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }

        }

    )

    if (showFilePicker) {

        SimpleFilePickerDialog(

            onDismiss = { showFilePicker = false },

            onFileSelected = {

                selectedFile = it

                showFilePicker = false

            },

            context = context

        )

    }

}


@Composable
fun SimpleFilePickerDialog(
    onDismiss: () -> Unit,
    onFileSelected: (File) -> Unit,
    context: Context
) {

    val formsDir = File(context.getExternalFilesDir(null), "formularios")

    val files = if (formsDir.exists()) {
        formsDir.listFiles { _, name -> name.endsWith(".pkm") }?.toList() ?: emptyList()
    } else {
        emptyList()
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = { Text("Seleccionar archivo .pkm") },

        text = {

            if (files.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Text("No hay archivos .pkm guardados")

                }

            } else {

                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {

                    items(files) { file ->

                        TextButton(
                            onClick = { onFileSelected(file) },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = file.name,
                                modifier = Modifier.padding(8.dp)
                            )

                        }

                        Divider()

                    }

                }

            }

        },

        confirmButton = {

            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }

        }

    )

}
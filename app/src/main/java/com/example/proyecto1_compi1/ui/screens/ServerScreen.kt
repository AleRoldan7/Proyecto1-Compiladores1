package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto1_compi1.servidor.controller.ApiCliente
import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import com.example.proyecto1_compi1.ui.server.view.FormularioViewModel
import java.io.File

private val BgDeep        = Color(0xFF090C10)
private val BgSurface     = Color(0xFF161B22)
private val BgCard        = Color(0xFF1C2128)
private val BgCardAlt     = Color(0xFF21262D)
private val BgEditor      = Color(0xFF0D1117)
private val CyanGlow      = Color(0xFF39D0D8)
private val CyanDim       = Color(0xFF1A6E73)
private val GreenOk       = Color(0xFF3FB950)
private val RedErr        = Color(0xFFF85149)
private val YellowWarn    = Color(0xFFE3B341)
private val TextPrimary   = Color(0xFFCDD9E5)
private val TextSecondary = Color(0xFF636E7B)
private val TextMuted     = Color(0xFF3D444D)
private val BorderColor   = Color(0xFF2D333B)

@Composable
fun ServerScreen(
    navController: NavController,
    viewModel: FormularioViewModel = viewModel()
) {
    val context          = LocalContext.current
    val formularios      by viewModel.formularios.collectAsState()
    val isLoading        by viewModel.isLoading.collectAsState()
    val error            by viewModel.error.collectAsState()
    val uploadResult     by viewModel.uploadResult.collectAsState()
    val isDownloading    by viewModel.isDownloading.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val downloadResult   by viewModel.downloadResult.collectAsState()

    var downloadingId    by remember { mutableStateOf<Int?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showUrlDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages() }
    }
    LaunchedEffect(uploadResult) {
        uploadResult?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages() }
    }
    LaunchedEffect(downloadResult) {
        downloadResult?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
            downloadingId = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier.fillMaxWidth().background(BgSurface)
                    .drawBehind {
                        drawLine(CyanDim, Offset(0f, size.height),
                            Offset(size.width, size.height), 1.dp.toPx())
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("SERVIDOR", color = CyanGlow, fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp)
                        Text("formularios remotos", color = TextSecondary,
                            fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(BgCardAlt)
                            .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
                            .clickable { viewModel.cargarFormularios(context) }
                            .padding(horizontal = 9.dp, vertical = 5.dp)) {
                            Text("↻", color = CyanGlow, fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace)
                        }
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))
                            .background(YellowWarn.copy(0.12f))
                            .border(1.dp, YellowWarn.copy(0.4f), RoundedCornerShape(4.dp))
                            .clickable { showUploadDialog = true }
                            .padding(horizontal = 9.dp, vertical = 5.dp)) {
                            Text("+ Subir", color = YellowWarn, fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                        }
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(BgCardAlt)
                            .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
                            .clickable { navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }}
                            .padding(horizontal = 9.dp, vertical = 5.dp)) {
                            Text("⌂ Home", color = TextSecondary, fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyanDim.copy(0.2f))
                                .border(1.dp, CyanGlow.copy(0.5f))
                                .clickable { showUrlDialog = true }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                "URL",
                                color = CyanGlow,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
            if (showUrlDialog) {
                NgrokUrlDialog(
                    context = context,
                    onDismiss = { showUrlDialog = false }
                )
            }
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = CyanGlow, strokeWidth = 2.dp)
                        Text("Cargando formularios...", color = TextSecondary,
                            fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            } else if (formularios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Sin formularios en el servidor", color = TextSecondary,
                            fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                        Text("Sube uno con el botón + Subir", color = TextMuted,
                            fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Spacer(Modifier.height(4.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            .background(CyanDim.copy(0.3f))
                            .border(1.dp, CyanGlow.copy(0.5f), RoundedCornerShape(6.dp))
                            .clickable { viewModel.cargarFormularios(context) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)) {
                            Text("↻ Reintentar", color = CyanGlow, fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(formularios) { form ->
                        ServerFormCard(
                            formulario    = form,
                            isDownloading = isDownloading && downloadingId == form.idFormulario,
                            downloadProgress = downloadProgress,
                            onDownloadClick  = {
                                downloadingId = form.idFormulario
                                viewModel.descargarFormulario(
                                    form.idFormulario, form.nombreArchivo, context)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        ServerUploadDialog(
            onDismiss = { showUploadDialog = false },
            onUpload  = { file, autor ->
                viewModel.subirFormulario(file, autor, context)
                showUploadDialog = false
            },
            context = context
        )
    }
}

@Composable
fun NgrokUrlDialog(
    context: Context,
    onDismiss: () -> Unit
) {

    var url by remember {
        mutableStateOf(ApiCliente.getSavedUrl(context))
    }

    Dialog(onDismissRequest = onDismiss) {

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(BgCard)
                .border(1.dp, CyanGlow, RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {

            Text(
                "URL servidor (ngrok)",
                color = CyanGlow,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(10.dp))

            BasicTextField(
                value = url,
                onValueChange = { url = it },
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgEditor)
                    .border(1.dp, BorderColor)
                    .padding(10.dp)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {

                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }

                TextButton(
                    onClick = {
                        ApiCliente.saveUrl(context, url)
                        Toast
                            .makeText(context,"URL guardada",Toast.LENGTH_SHORT)
                            .show()

                        onDismiss()
                    }
                ) {
                    Text("Guardar", color = CyanGlow)
                }
            }

        }

    }
}
@Composable
private fun ServerFormCard(
    formulario:       FormularioDTO,
    isDownloading:    Boolean,
    downloadProgress: Int,
    onDownloadClick:  () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BgCard)
            .border(1.dp, GreenOk.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(BgCardAlt)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.clip(RoundedCornerShape(3.dp))
                    .background(GreenOk.copy(0.15f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)) {
                    Text(".pkm", color = GreenOk, fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                }
                Text(formulario.nombreArchivo, color = TextPrimary, fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("ID: ${formulario.idFormulario}", color = TextMuted,
                fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        HorizontalDivider(color = BorderColor.copy(0.4f), thickness = 0.5.dp)

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Autor: ${formulario.autor}", color = TextSecondary,
                fontSize = 10.sp, fontFamily = FontFamily.Monospace)

            if (isDownloading) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(CyanDim.copy(0.2f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = CyanGlow, strokeWidth = 1.5.dp
                    )
                    Text("$downloadProgress%", color = CyanGlow, fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace)
                }
            } else {
                Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))
                    .background(GreenOk.copy(0.12f))
                    .border(1.dp, GreenOk.copy(0.4f), RoundedCornerShape(5.dp))
                    .clickable(onClick = onDownloadClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp)) {
                    Text("↓ Descargar", color = GreenOk, fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun ServerUploadDialog(
    onDismiss:  () -> Unit,
    onUpload:   (File, String) -> Unit,
    context:    Context
) {
    var selectedFile     by remember { mutableStateOf<File?>(null) }
    var autor            by remember { mutableStateOf("") }
    var showFilePicker   by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(BgCard)
                .border(1.dp, YellowWarn.copy(0.4f), RoundedCornerShape(10.dp))
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().background(BgSurface)
                .padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text("Subir formulario .pkm", color = YellowWarn, fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
            }
            HorizontalDivider(color = YellowWarn.copy(0.3f))

            Column(modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Autor", color = TextSecondary, fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace)
                    Box(modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp)).background(BgEditor)
                        .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)) {
                        if (autor.isEmpty()) Text("Escribe el autor", color = TextMuted,
                            fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        androidx.compose.foundation.text.BasicTextField(
                            value = autor, onValueChange = { autor = it },
                            textStyle = TextStyle(color = TextPrimary, fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace),
                            cursorBrush = SolidColor(CyanGlow),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Archivo .pkm", color = TextSecondary, fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)
                            .clip(RoundedCornerShape(6.dp)).background(BgEditor)
                            .border(1.dp,
                                if (selectedFile != null) GreenOk.copy(0.5f) else BorderColor,
                                RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)) {
                            Text(
                                selectedFile?.name ?: "Ningún archivo seleccionado",
                                color = if (selectedFile != null) GreenOk else TextMuted,
                                fontSize = 11.sp, fontFamily = FontFamily.Monospace,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            .background(CyanDim.copy(0.3f))
                            .border(1.dp, CyanGlow.copy(0.5f), RoundedCornerShape(6.dp))
                            .clickable { showFilePicker = true }
                            .padding(horizontal = 10.dp, vertical = 8.dp)) {
                            Text("Buscar", color = CyanGlow, fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            HorizontalDivider(color = BorderColor)

            Row(
                modifier = Modifier.fillMaxWidth().background(BgSurface)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(BgCardAlt)
                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Text("Cancelar", color = TextSecondary, fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    .background(
                        if (selectedFile != null && autor.isNotBlank())
                            YellowWarn.copy(0.15f) else BgCardAlt
                    )
                    .border(1.dp,
                        if (selectedFile != null && autor.isNotBlank())
                            YellowWarn.copy(0.5f) else BorderColor,
                        RoundedCornerShape(6.dp))
                    .clickable {
                        if (selectedFile != null && autor.isNotBlank()) {
                            onUpload(selectedFile!!, autor)
                        } else {
                            Toast.makeText(context,
                                "Selecciona un archivo y escribe el autor",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Text("⇡ Subir",
                        color = if (selectedFile != null && autor.isNotBlank())
                            YellowWarn else TextMuted,
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace)
                }
            }
        }
    }

    if (showFilePicker) {
        ServerFilePickerDialog(
            onDismiss = { showFilePicker = false },
            onFileSelected = { selectedFile = it; showFilePicker = false },
            context = context
        )
    }
}

@RequiresApi(Build.VERSION_CODES.FROYO)
@Composable
private fun ServerFilePickerDialog(
    onDismiss:      () -> Unit,
    onFileSelected: (File) -> Unit,
    context:        Context
) {
    val formsDir = File(context.getExternalFilesDir(null), "formularios")
    val files    = if (formsDir.exists())
        formsDir.listFiles { _, n -> n.endsWith(".pkm") }?.toList() ?: emptyList()
    else emptyList()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(BgCard)
                .border(1.dp, CyanDim, RoundedCornerShape(10.dp))
        ) {
            Box(modifier = Modifier.fillMaxWidth().background(BgSurface)
                .padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text("Seleccionar .pkm", color = CyanGlow, fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
            }
            HorizontalDivider(color = CyanDim.copy(0.3f))

            if (files.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center) {
                    Text("No hay archivos .pkm guardados", color = TextSecondary,
                        fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(files) { file ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onFileSelected(file) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(3.dp))
                                .background(GreenOk.copy(0.15f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)) {
                                Text(".pkm", color = GreenOk, fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            }
                            Text(file.name, color = TextPrimary, fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f),
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("→", color = CyanGlow, fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace)
                        }
                        HorizontalDivider(color = BorderColor.copy(0.3f), thickness = 0.5.dp)
                    }
                }
            }

            HorizontalDivider(color = BorderColor)
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd) {
                Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(BgCardAlt)
                    .border(1.dp, BorderColor, RoundedCornerShape(5.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 12.dp, vertical = 5.dp)) {
                    Text("Cerrar", color = TextSecondary, fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
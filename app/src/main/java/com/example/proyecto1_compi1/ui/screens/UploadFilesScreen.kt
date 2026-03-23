package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.generate_lenguaje.PKMCache
import com.example.proyecto1_compi1.generate_lenguaje.ReadPKM
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private val BgDeep        = Color(0xFF090C10)
private val BgSurface     = Color(0xFF161B22)
private val BgCard        = Color(0xFF1C2128)
private val BgCardAlt     = Color(0xFF21262D)
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
fun UploadFilesScreen(navController: NavController) {
    val context  = LocalContext.current
    var archivos by remember { mutableStateOf(getForms(context)) }
    val loader   = remember { ReadPKM(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .drawBehind {
                        drawLine(CyanDim, Offset(0f, size.height),
                            Offset(size.width, size.height), 1.dp.toPx())
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("FORMULARIOS GUARDADOS", color = CyanGlow, fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp)
                        Text("archivos .pkm locales", color = TextSecondary,
                            fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Recargar
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(BgCardAlt)
                                .border(1.dp, BorderColor, RoundedCornerShape(5.dp))
                                .clickable { archivos = getForms(context) }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text("↻ Actualizar", color = TextSecondary,
                                fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                        // Home
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(BgCardAlt)
                                .border(1.dp, BorderColor, RoundedCornerShape(5.dp))
                                .clickable { navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }}
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text("⌂ Home", color = TextSecondary,
                                fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // ── Contador ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgCard)
                    .padding(horizontal = 14.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${archivos.size} archivo(s) encontrado(s)",
                    color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace
                )
            }

            // ── Lista de archivos ─────────────────────────────────────────
            if (archivos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Sin archivos .pkm", color = TextSecondary,
                            fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Text("Crea y guarda un formulario primero",
                            color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(archivos) { _, file ->
                        FileCard(
                            file    = file,
                            onResponder = {
                                try {
                                    PKMCache.elements = loader.loadPKM(file)
                                    navController.navigate("answer")
                                } catch (e: Exception) {
                                    Toast.makeText(context,
                                        "Error al cargar: ${e.message}",
                                        Toast.LENGTH_LONG).show()
                                }
                            },
                            onEliminar = {
                                file.delete()
                                archivos = getForms(context)
                                Toast.makeText(context, "Archivo eliminado", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileCard(
    file:        File,
    onResponder: () -> Unit,
    onEliminar:  () -> Unit
) {
    val sdf      = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fecha    = sdf.format(Date(file.lastModified()))
    val tamano   = "%.1f KB".format(file.length() / 1024.0)
    val isPkm    = file.name.endsWith(".pkm")
    val accentColor = if (isPkm) GreenOk else YellowWarn

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BgCard)
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgCardAlt)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(accentColor.copy(0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            if (isPkm) ".pkm" else file.extension,
                            color = accentColor, fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = file.nameWithoutExtension,
                        color = TextPrimary, fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                Text(tamano, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }

        HorizontalDivider(color = BorderColor.copy(alpha = 0.4f), thickness = 0.5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Modificado: $fecha", color = TextSecondary,
                fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        HorizontalDivider(color = BorderColor.copy(alpha = 0.4f), thickness = 0.5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isPkm) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(GreenOk.copy(0.12f))
                        .border(1.dp, GreenOk.copy(0.4f), RoundedCornerShape(6.dp))
                        .clickable(onClick = onResponder),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶ Responder", color = GreenOk, fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                }
            }

            Box(
                modifier = Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(RedErr.copy(0.1f))
                    .border(1.dp, RedErr.copy(0.35f), RoundedCornerShape(6.dp))
                    .clickable(onClick = onEliminar)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("✕ Eliminar", color = RedErr, fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace)
            }
        }
    }
}

fun getForms(context: Context): List<File> {
    val carpeta = File(context.getExternalFilesDir(null), "formularios")
    if (!carpeta.exists()) carpeta.mkdirs()
    return carpeta.listFiles()
        ?.filter { it.isFile }
        ?.sortedByDescending { it.lastModified() }
        ?: emptyList()
}
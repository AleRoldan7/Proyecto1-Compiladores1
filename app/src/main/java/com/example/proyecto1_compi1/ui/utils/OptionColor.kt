package com.example.proyecto1_compi1.ui.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class ColorOption(
    val name: String,
    val color: Color,
    val hex: String,
    val rgb: String,
    val hsl: String? = null
)

object ColorPresets {
    val colors = listOf(
        ColorOption("Rojo", Color(0xFFF44336), "#F44336", "(244, 67, 54)", "<0, 100, 50>"),
        ColorOption("Verde", Color(0xFF4CAF50), "#4CAF50", "(76, 175, 80)", "<120, 100, 50>"),
        ColorOption("Azul", Color(0xFF2196F3), "#2196F3", "(33, 150, 243)", "<210, 100, 50>"),
        ColorOption("Amarillo", Color(0xFFFFEB3B), "#FFEB3B", "(255, 235, 59)", "<60, 100, 50>"),
        ColorOption("Naranja", Color(0xFFFF9800), "#FF9800", "(255, 152, 0)", "<30, 100, 50>"),
        ColorOption("Morado", Color(0xFF9C27B0), "#9C27B0", "(156, 39, 176)", "<280, 100, 50>"),
        ColorOption("Cian", Color(0xFF00BCD4), "#00BCD4", "(0, 188, 212)", "<180, 100, 50>"),
        ColorOption("Rosa", Color(0xFFE91E63), "#E91E63", "(233, 30, 99)", "<340, 100, 50>"),
        ColorOption("Negro", Color(0xFF000000), "#000000", "(0, 0, 0)", "<0, 0, 0>"),
        ColorOption("Blanco", Color(0xFFFFFFFF), "#FFFFFF", "(255, 255, 255)", "<0, 0, 100>"),
        ColorOption("Gris", Color(0xFF9E9E9E), "#9E9E9E", "(158, 158, 158)", "<0, 0, 50>"),
        ColorOption("Celeste", Color(0xFF03A9F4), "#03A9F4", "(3, 169, 244)", "<200, 100, 50>"),
        ColorOption("Lima", Color(0xFFCDDC39), "#CDDC39", "(205, 220, 57)", "<70, 100, 50>"),
        ColorOption("Índigo", Color(0xFF3F51B5), "#3F51B5", "(63, 81, 181)", "<230, 100, 50>"),
        ColorOption("Café", Color(0xFF795548), "#795548", "(121, 85, 72)", "<20, 100, 50>")
    )
}

@Composable
fun ColorPickerDialog(
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit,
    currentColor: String? = null
) {
    var selectedColor by remember { mutableStateOf<String?>(currentColor) }
    var selectedFormat by remember { mutableStateOf("Hex") }
    var customRed by remember { mutableStateOf("") }
    var customGreen by remember { mutableStateOf("") }
    var customBlue by remember { mutableStateOf("") }
    var customHue by remember { mutableStateOf("") }
    var customSat by remember { mutableStateOf("") }
    var customLight by remember { mutableStateOf("") }
    var customHex by remember { mutableStateOf("") }

    val formats = listOf("Hex", "RGB", "HSL", "Paleta")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = "🎨 SELECTOR DE COLORES",
                    color = Color(0xFF39D0D8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Vista previa del color seleccionado
                val previewColor = when (selectedFormat) {
                    "Hex" -> try { Color(android.graphics.Color.parseColor(customHex.ifEmpty { "#FFFFFF" })) } catch(e: Exception) { Color.White }
                    "RGB" -> try { Color(customRed.toIntOrNull() ?: 255, customGreen.toIntOrNull() ?: 255, customBlue.toIntOrNull() ?: 255) } catch(e: Exception) { Color.White }
                    "HSL" -> try {
                        val h = customHue.toFloatOrNull() ?: 0f
                        val s = customSat.toFloatOrNull() ?: 100f
                        val l = customLight.toFloatOrNull() ?: 50f
                        hslToColor(h, s, l)
                    } catch(e: Exception) { Color.White }
                    else -> ColorPresets.colors.find { it.hex == selectedColor }?.color ?: Color.White
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(previewColor)
                        .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedColor ?: "Sin seleccionar",
                        color = if (previewColor == Color.White || previewColor == Color.Yellow) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selector de formato
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    formats.forEach { format ->
                        FilterChip(
                            selected = selectedFormat == format,
                            onClick = { selectedFormat = format },
                            label = { Text(format, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF39D0D8).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFF39D0D8)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Contenido según formato seleccionado
                when (selectedFormat) {
                    "Hex" -> {
                        OutlinedTextField(
                            value = customHex,
                            onValueChange = { customHex = it },
                            label = { Text("Color Hex (ej: #FF5733)") },
                            placeholder = { Text("#FFFFFF") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF39D0D8),
                                cursorColor = Color(0xFF39D0D8)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val colorCode = if (customHex.startsWith("#")) customHex else "#$customHex"
                                onColorSelected("\"$colorCode\"")
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A6E73),
                                contentColor = Color(0xFF39D0D8)
                            )
                        ) {
                            Text("Insertar Color Hex")
                        }
                    }

                    "RGB" -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customRed,
                                onValueChange = { customRed = it },
                                label = { Text("R (0-255)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF39D0D8)
                                )
                            )
                            OutlinedTextField(
                                value = customGreen,
                                onValueChange = { customGreen = it },
                                label = { Text("G (0-255)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF39D0D8)
                                )
                            )
                            OutlinedTextField(
                                value = customBlue,
                                onValueChange = { customBlue = it },
                                label = { Text("B (0-255)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF39D0D8)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val r = customRed.toIntOrNull() ?: 255
                                val g = customGreen.toIntOrNull() ?: 255
                                val b = customBlue.toIntOrNull() ?: 255
                                onColorSelected("($r, $g, $b)")
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A6E73),
                                contentColor = Color(0xFF39D0D8)
                            )
                        ) {
                            Text("Insertar Color RGB")
                        }
                    }

                    "HSL" -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customHue,
                                onValueChange = { customHue = it },
                                label = { Text("H (0-360)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF39D0D8)
                                )
                            )
                            OutlinedTextField(
                                value = customSat,
                                onValueChange = { customSat = it },
                                label = { Text("S (0-100)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF39D0D8)
                                )
                            )
                            OutlinedTextField(
                                value = customLight,
                                onValueChange = { customLight = it },
                                label = { Text("L (0-100)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF39D0D8)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val h = customHue.toFloatOrNull() ?: 0f
                                val s = customSat.toFloatOrNull() ?: 100f
                                val l = customLight.toFloatOrNull() ?: 50f
                                onColorSelected("<$h, $s, $l>")
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A6E73),
                                contentColor = Color(0xFF39D0D8)
                            )
                        ) {
                            Text("Insertar Color HSL")
                        }
                    }

                    "Paleta" -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ColorPresets.colors) { colorOption ->
                                ColorPaletteItem(
                                    colorOption = colorOption,
                                    isSelected = selectedColor == colorOption.hex,
                                    onClick = { selectedColor = colorOption.hex },
                                    onInsert = { format ->
                                        val code = when (format) {
                                            "Hex" -> "\"${colorOption.hex}\""
                                            "RGB" -> "${colorOption.rgb}"
                                            "HSL" -> "${colorOption.hsl ?: colorOption.rgb}"
                                            else -> "\"${colorOption.hex}\""
                                        }
                                        onColorSelected(code)
                                        onDismiss()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón cerrar
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar", color = Color(0xFF636E7B))
                }
            }
        }
    }
}

@Composable
fun ColorPaletteItem(
    colorOption: ColorOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    onInsert: (String) -> Unit
) {
    var showFormatMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF39D0D8).copy(alpha = 0.1f) else Color(0xFF161B22)
        ),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFF39D0D8)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorOption.color)
                        .border(2.dp, Color.White, CircleShape)
                )
                Column {
                    Text(
                        text = colorOption.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = colorOption.hex,
                        color = Color(0xFF636E7B),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Botón de menú usando Text en lugar de Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D333B))
                    .clickable { showFormatMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⋮",
                    color = Color(0xFF39D0D8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    DropdownMenu(
        expanded = showFormatMenu,
        onDismissRequest = { showFormatMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("Insertar como Hex", fontFamily = FontFamily.Monospace) },
            onClick = { onInsert("Hex"); showFormatMenu = false }
        )
        DropdownMenuItem(
            text = { Text("Insertar como RGB", fontFamily = FontFamily.Monospace) },
            onClick = { onInsert("RGB"); showFormatMenu = false }
        )
        DropdownMenuItem(
            text = { Text("Insertar como HSL", fontFamily = FontFamily.Monospace) },
            onClick = { onInsert("HSL"); showFormatMenu = false }
        )
    }
}

fun hslToColor(h: Float, s: Float, l: Float): Color {
    val c = (1 - kotlin.math.abs(2 * l / 100 - 1)) * s / 100
    val x = c * (1 - kotlin.math.abs((h / 60) % 2 - 1))
    val m = l / 100 - c / 2

    val (r, g, b) = when {
        h < 60 -> Triple(c, x, 0f)
        h < 120 -> Triple(x, c, 0f)
        h < 180 -> Triple(0f, c, x)
        h < 240 -> Triple(0f, x, c)
        h < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color((r + m), (g + m), (b + m))
}
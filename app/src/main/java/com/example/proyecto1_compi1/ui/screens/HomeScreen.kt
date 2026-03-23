package com.example.proyecto1_compi1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val BgDeep        = Color(0xFF090C10)
private val CyanGlow      = Color(0xFF39D0D8)
private val CyanDim       = Color(0xFF1A6E73)
private val GreenOk       = Color(0xFF3FB950)
private val RedErr        = Color(0xFFF85149)
private val YellowWarn    = Color(0xFFE3B341)
private val TextSecondary = Color(0xFF636E7B)

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(GreenOk))
                Box(Modifier.size(10.dp).clip(CircleShape).background(YellowWarn))
                Box(Modifier.size(10.dp).clip(CircleShape).background(RedErr))
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "PKM_FORMS", color = CyanGlow, fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "compilador de formularios", color = TextSecondary,
                fontSize = 11.sp, fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(48.dp))

            HomeBtn("Crear / Editar formulario", "Escribe código .form",
                CyanGlow, Color(0xFF0D2A2C)) { navController.navigate("edit") }
            Spacer(Modifier.height(10.dp))
            HomeBtn("Cargar archivos .pkm", "Responde formularios guardados",
                GreenOk, Color(0xFF0D1F0D)) { navController.navigate("upload") }
            Spacer(Modifier.height(10.dp))
            HomeBtn("Servidor", "Sube y descarga formularios",
                YellowWarn, Color(0xFF221D08)) { navController.navigate("server") }
            Spacer(Modifier.height(10.dp))
            HomeBtn("Responder formulario", "Contesta el formulario actual",
                RedErr, Color(0xFF220D0D)) { navController.navigate("answer") }

            Spacer(Modifier.height(40.dp))
            Text(
                text = "Compiladores 1  ·  USAC  ·  2026",
                color = TextSecondary.copy(alpha = 0.4f),
                fontSize = 9.sp, fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun HomeBtn(
    label: String, sublabel: String,
    accent: Color, bg: Color, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(label, color = accent, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(2.dp))
                Text(sublabel, color = TextSecondary, fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace)
            }
            Text("→", color = accent.copy(0.6f), fontSize = 16.sp,
                fontFamily = FontFamily.Monospace)
        }
    }
}
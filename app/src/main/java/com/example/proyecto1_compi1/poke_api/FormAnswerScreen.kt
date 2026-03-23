package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.generate_lenguaje.PKMCache
import com.example.proyecto1_compi1.modelo.color_style.*
import com.example.proyecto1_compi1.modelo.question.*
import com.example.proyecto1_compi1.modelo.table.TableModel
import com.example.proyecto1_compi1.poke.PokeRepository
import com.example.proyecto1_compi1.ui.table.TableUI
//import com.example.proyecto1_compi1.ui.utils.colorCompose
import kotlinx.coroutines.launch
import java.io.File

private val BgDeep        = Color(0xFF090C10)
private val BgSurface     = Color(0xFF161B22)
private val BgCard        = Color(0xFF1C2128)
private val BgCardAlt     = Color(0xFF21262D)
private val CyanGlow      = Color(0xFF39D0D8)
private val CyanDim       = Color(0xFF1A6E73)
private val GreenOk       = Color(0xFF3FB950)
private val RedErr        = Color(0xFFF85149)
private val YellowWarn    = Color(0xFFE3B341)
private val PokeYellow    = Color(0xFFFFCC00)
private val PokeBall      = Color(0xFFEE1515)
private val TextPrimary   = Color(0xFFCDD9E5)
private val TextSecondary = Color(0xFF636E7B)
private val TextMuted     = Color(0xFF3D444D)
private val BorderColor   = Color(0xFF2D333B)

sealed class RespuestaElemento {
    data class Abierta(val texto: String)                           : RespuestaElemento()
    data class Seleccion(val indice: Int, val texto: String)        : RespuestaElemento()
    data class Multiple(val indices: List<Int>, val textos: List<String>) : RespuestaElemento()
    data class Desplegable(val indice: Int, val texto: String)      : RespuestaElemento()
    object SinRespuesta : RespuestaElemento()
}

/*
@Composable
fun FormAnswerScreen(navController: NavController) {
    val context   = LocalContext.current
    val elementos = remember { PKMCache.elements ?: emptyList() }
    val respuestas = remember { mutableStateMapOf<Int, RespuestaElemento>() }

    Box(modifier = Modifier.fillMaxSize().background(BgDeep)
        .windowInsetsPadding(WindowInsets.systemBars)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Box(modifier = Modifier.fillMaxWidth().background(BgSurface)
                .drawBehind {
                    drawLine(PokeBall.copy(0.4f), Offset(0f, size.height),
                        Offset(size.width, size.height), 1.dp.toPx())
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("RESPONDER FORMULARIO", color = PokeYellow, fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp)
                        Text("${elementos.size} elemento(s)", color = TextSecondary,
                            fontSize = 9.sp, fontFamily = FontFamily.Monospace)
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
                }
            }

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(elementos) { idx, elemento ->
                    ElementoAnswerCard(elemento = elemento, indice = idx,
                        onRespuesta = { respuestas[idx] = it })
                }
            }

            // Botón guardar
            Box(modifier = Modifier.fillMaxWidth().background(BgSurface)
                .drawBehind {
                    drawLine(CyanDim, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx())
                }
                .padding(horizontal = 10.dp, vertical = 8.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GreenOk.copy(0.15f))
                    .border(1.dp, GreenOk.copy(0.5f), RoundedCornerShape(8.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null) {
                        guardarRespuestas(context, elementos, respuestas)
                    },
                    contentAlignment = Alignment.Center) {
                    Text("✓  Guardar respuestas", color = GreenOk, fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun ElementoAnswerCard(elemento: Any, indice: Int,
    onRespuesta: (RespuestaElemento) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
        .background(BgCard).border(1.dp, BorderColor, RoundedCornerShape(8.dp)).padding(12.dp)) {
        when (elemento) {
            is TextModel        -> TextoDisplay(elemento)
            is OpenQuestion     -> OpenAnswerUI(elemento, onRespuesta)
            is SelectQuestion   -> SelectAnswerUI(elemento, onRespuesta)
            is DropQuestion     -> DropAnswerUI(elemento, onRespuesta)
            is MultipleQuestion -> MultipleAnswerUI(elemento, onRespuesta)
            is SectionsModel    -> SeccionAnswerUI(elemento, indice, onRespuesta)
            is TableModel       -> TableUI(elemento)
            else -> Text("${elemento.javaClass.simpleName}", color = TextMuted,
                fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun TextoDisplay(t: TextModel) {
    Text(t.content ?: "", color = TextPrimary, fontSize = 13.sp,
        fontFamily = FontFamily.Monospace)
}

@Composable
private fun OpenAnswerUI(q: OpenQuestion, onRespuesta: (RespuestaElemento) -> Unit) {
    var texto by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        QuestionLabel(q.label)
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(BgDeep)
            .border(1.dp, CyanDim.copy(0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)) {
            if (texto.isEmpty()) Text("Escribe aquí...", color = TextMuted,
                fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            BasicTextField(value = texto, onValueChange = {
                texto = it; onRespuesta(RespuestaElemento.Abierta(it))
            }, textStyle = TextStyle(color = TextPrimary, fontSize = 12.sp,
                fontFamily = FontFamily.Monospace),
                cursorBrush = SolidColor(CyanGlow), modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SelectAnswerUI(q: SelectQuestion, onRespuesta: (RespuestaElemento) -> Unit) {
    var sel by remember { mutableStateOf(-1) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        QuestionLabel(q.label)
        q.options.forEachIndexed { idx, opcion ->
            val active = sel == idx
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                .background(if (active) CyanGlow.copy(0.1f) else BgDeep)
                .border(1.dp, if (active) CyanDim else BorderColor, RoundedCornerShape(6.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { sel = idx; onRespuesta(RespuestaElemento.Seleccion(idx, opcion)) }
                .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(if (active) CyanGlow else BorderColor))
                Text(opcion, color = if (active) TextPrimary else TextSecondary,
                    fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun DropAnswerUI(q: DropQuestion, onRespuesta: (RespuestaElemento) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var sel      by remember { mutableStateOf(-1) }
    val opciones = remember { mutableStateListOf<String>() }
    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope    = rememberCoroutineScope()
    val tienePoke = !q.optionsPoke.isNullOrEmpty()

    LaunchedEffect(q) {
        if (tienePoke) {
            cargando = true
            scope.launch {
                try {
                    val params = q.optionsPoke!!
                    val desde  = params.getOrNull(1)?.toIntOrNull() ?: 1
                    val hasta  = params.getOrNull(2)?.toIntOrNull() ?: 10
                    opciones.clear()
                    opciones.addAll(PokeRepository.obtenerPokemones(desde, hasta))
                } catch (e: Exception) { errorMsg = e.message }
                finally { cargando = false }
            }
        } else { opciones.clear(); opciones.addAll(q.options) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Header con chip pokémon
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
            if (!q.label.isNullOrBlank())
                Text(q.label, color = TextPrimary, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
            if (tienePoke)
                Box(modifier = Modifier.clip(RoundedCornerShape(3.dp))
                    .background(PokeBall.copy(0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("PokéAPI", color = PokeBall, fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                }
        }

        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(BgDeep)
            .border(1.dp, if (tienePoke) PokeYellow.copy(0.4f) else CyanDim.copy(0.5f),
                RoundedCornerShape(6.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null) { if (!cargando) expanded = !expanded }
            .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            if (cargando) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(12.dp), color = PokeYellow, strokeWidth = 1.5.dp)
                    Text("Cargando Pokémon...", color = TextMuted, fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace)
                }
            } else {
                Text(if (sel >= 0 && sel < opciones.size) opciones[sel] else "Seleccionar...",
                    color = if (sel >= 0) TextPrimary else TextMuted,
                    fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Text(if (expanded) "▲" else "▼",
                color = if (tienePoke) PokeYellow else CyanGlow,
                fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        if (errorMsg != null)
            Text("⚠ $errorMsg", color = RedErr, fontSize = 9.sp, fontFamily = FontFamily.Monospace)

        if (expanded && opciones.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                .background(BgCardAlt).border(1.dp, BorderColor, RoundedCornerShape(6.dp))) {
                opciones.forEachIndexed { idx, nombre ->
                    Row(modifier = Modifier.fillMaxWidth()
                        .background(if (sel == idx) PokeYellow.copy(0.08f) else Color.Transparent)
                        .clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null) {
                            sel = idx; expanded = false
                            onRespuesta(RespuestaElemento.Desplegable(idx, nombre))
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (tienePoke) {
                            val desde = q.optionsPoke!!.getOrNull(1)?.toIntOrNull() ?: 1
                            Box(modifier = Modifier.clip(RoundedCornerShape(3.dp))
                                .background(PokeBall.copy(0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)) {
                                Text("#${desde + idx}", color = PokeBall, fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            }
                        }
                        Text(nombre, color = if (sel == idx) TextPrimary else TextSecondary,
                            fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    if (idx < opciones.lastIndex)
                        Box(Modifier.fillMaxWidth().height(0.5.dp).background(BorderColor))
                }
            }
        }
    }
}

@Composable
private fun MultipleAnswerUI(q: MultipleQuestion, onRespuesta: (RespuestaElemento) -> Unit) {
    val sels = remember { mutableStateListOf<Int>() }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        QuestionLabel(q.label)
        q.options.forEachIndexed { idx, opcion ->
            val m = idx in sels
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                .background(if (m) GreenOk.copy(0.1f) else BgDeep)
                .border(1.dp, if (m) GreenOk.copy(0.4f) else BorderColor, RoundedCornerShape(6.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (m) sels.remove(idx) else sels.add(idx)
                    onRespuesta(RespuestaElemento.Multiple(sels.toList(),
                        sels.map { i -> q.options.getOrElse(i) { "" } }))
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(11.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (m) GreenOk else BorderColor), contentAlignment = Alignment.Center) {
                    if (m) Text("✓", color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.ExtraBold)
                }
                Text(opcion, color = if (m) TextPrimary else TextSecondary,
                    fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun SeccionAnswerUI(s: SectionsModel, base: Int, onRespuesta: (RespuestaElemento) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        s.elements?.forEachIndexed { subIdx, sub ->
            if (sub != null) ElementoAnswerCard(sub, base * 100 + subIdx, onRespuesta)
        }
    }
}

private fun guardarRespuestas(context: Context, elementos: List<Any>,
    respuestas: Map<Int, RespuestaElemento>) {
    try {
        val sb = StringBuilder()
        sb.appendLine("###")
        sb.appendLine("FormName: Respuestas")
        sb.appendLine("Date: ${java.time.LocalDate.now()}")
        sb.appendLine("Time: ${java.time.LocalTime.now().withNano(0)}")
        sb.appendLine("###")
        sb.appendLine()

        elementos.forEachIndexed { idx, elem ->
            val r = respuestas[idx]
            when (elem) {
                is OpenQuestion -> {
                    val txt = (r as? RespuestaElemento.Abierta)?.texto ?: ""
                    sb.appendLine("<open=${elem.width},${elem.height},\"${clean(elem.label ?: "")}\">")
                    sb.appendLine("  respuesta: \"${clean(txt)}\"")
                    sb.appendLine("</open>")
                }
                is SelectQuestion -> {
                    val s = r as? RespuestaElemento.Seleccion
                    val opts = elem.options.joinToString(",") { "\"${clean(it)}\"" }
                    sb.appendLine("<select=${elem.width},${elem.height},\"${clean(elem.label ?: "")}\",{$opts},${s?.indice ?: -1}/>")
                }
                is DropQuestion -> {
                    val s = r as? RespuestaElemento.Desplegable
                    // Spec: guardar el nombre real (texto), no el índice de la API
                    val opts = elem.options.joinToString(",") { "\"${clean(it)}\"" }
                    sb.appendLine("<drop=${elem.width},${elem.height},\"${clean(elem.label ?: "")}\",{$opts},${s?.indice ?: -1}/>")
                }
                is MultipleQuestion -> {
                    val s = r as? RespuestaElemento.Multiple
                    val opts  = elem.options.joinToString(",") { "\"${clean(it)}\"" }
                    val corrs = s?.indices?.joinToString(",") ?: ""
                    sb.appendLine("<multiple=${elem.width},${elem.height},\"${clean(elem.label ?: "")}\",{$opts},{$corrs}/>")
                }
            }
        }

        val carpeta = File(context.getExternalFilesDir(null), "respuestas")
        if (!carpeta.exists()) carpeta.mkdirs()
        val archivo = File(carpeta, "respuestas_${System.currentTimeMillis()}.pkm")
        archivo.writeText(sb.toString())
        Toast.makeText(context, "✓ Respuestas guardadas en ${archivo.name}", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun clean(s: String) = s.replace("\"","")
    .replace("😀","@[:smile:]").replace("🥲","@[:sad:]")
    .replace("😐","@[:serious:]").replace("❤️","@[:heart:]")
    .replace("⭐","@[:star:]").replace("😺","@[:cat:]").trim()

@Composable
private fun QuestionLabel(label: String?) {
    if (!label.isNullOrBlank())
        Text(label, color = TextPrimary, fontSize = 12.sp,
            fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, lineHeight = 16.sp)
}


 */
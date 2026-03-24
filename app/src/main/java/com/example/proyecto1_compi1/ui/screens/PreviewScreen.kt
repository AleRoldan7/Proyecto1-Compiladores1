package com.example.proyecto1_compi1.ui.screens

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.proyecto1_compi1.modelo.color_style.*
import com.example.proyecto1_compi1.modelo.forms.FormsModel
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.modelo.question.*
import com.example.proyecto1_compi1.modelo.table.TableModel
import com.example.proyecto1_compi1.ui.utils.RenderElement
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreviewScreen(navController: NavController) {

    val elementos = remember {
        try {
            ResultParser.currentForm?.getElements() ?: emptyList<Any>()
        } catch (e: Exception) {
            Log.e("PREVIEW", "Error: ${e.message}", e)
            emptyList<Any>()
        }
    }
    val formName = remember { ResultParser.currentForm?.getName() ?: "Formulario" }

    val context       = LocalContext.current
    var showDialog    by remember { mutableStateOf(false) }
    var autor         by remember { mutableStateOf("") }
    var description   by remember { mutableStateOf("") }
    var nameForm      by remember { mutableStateOf("") }

    // ── Estado de guardado ────────────────────────────────────────────────
    var saveStatus    by remember { mutableStateOf<SaveStatus>(SaveStatus.Idle) }

    LaunchedEffect(saveStatus) {
        if (saveStatus is SaveStatus.Success || saveStatus is SaveStatus.AlreadyExists) {
            kotlinx.coroutines.delay(3000)
            saveStatus = SaveStatus.Idle
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        // ── Top bar ───────────────────────────────────────────────────────
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
                    Text("PKM_FORMS", color = CyanGlow, fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp)
                    Text("vista previa", color = TextSecondary, fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chip nombre
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(CyanGlow.copy(0.1f))
                        .border(1.dp, CyanDim, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(formName, color = CyanGlow, fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace, maxLines = 1)
                    }
                    // Chip elementos
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(BgCardAlt)
                        .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)) {
                        Text("${elementos.size} elem.", color = TextSecondary,
                            fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                    // Home
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(BgCardAlt)
                        .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
                        .clickable { navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }}
                        .padding(horizontal = 9.dp, vertical = 4.dp)) {
                        Text("⌂ Home", color = TextSecondary, fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // ── Banner de estado guardado ─────────────────────────────────────
        AnimatedVisibility(
            visible = saveStatus != SaveStatus.Idle,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            val (bg, border, icon, msg, accent) = when (saveStatus) {
                is SaveStatus.Success -> listOf(
                    GreenOk.copy(0.1f), GreenOk.copy(0.4f),
                    "✓", "Formulario guardado correctamente", GreenOk)
                is SaveStatus.AlreadyExists -> listOf(
                    YellowWarn.copy(0.1f), YellowWarn.copy(0.4f),
                    "⚠", "Ya existe un formulario con ese nombre", YellowWarn)
                is SaveStatus.Error -> listOf(
                    RedErr.copy(0.1f), RedErr.copy(0.4f),
                    "✗", (saveStatus as SaveStatus.Error).msg, RedErr)
                else -> listOf(Color.Transparent, Color.Transparent, "", "", Color.Transparent)
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(bg as Color)
                    .drawBehind {
                        drawLine(border as Color, Offset(0f, size.height),
                            Offset(size.width, size.height), 1.dp.toPx())
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(icon as String, color = accent as Color, fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                Text(msg as String, color = TextPrimary, fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (elementos.isNotEmpty()) {
                elementos.forEach { element ->

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(7.dp))
                                .background(BgCard)
                                .border(1.dp, BorderColor, RoundedCornerShape(7.dp))
                                .padding(10.dp)
                        ) {
                            RenderElement(element)
                        }

                        Box(modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(RedErr.copy(0.08f))
                            .border(1.dp, RedErr.copy(0.3f), RoundedCornerShape(6.dp))
                            .padding(10.dp)) {
                            Text("⚠ Error: ${element?.javaClass?.simpleName}",
                                color = RedErr, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Sin elementos", color = TextSecondary,
                            fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        Text("Analiza el código primero", color = TextMuted,
                            fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // ── Botones inferiores ────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth().background(BgSurface)
                .drawBehind {
                    drawLine(CyanDim, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx())
                }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Guardar PKM
                Box(
                    modifier = Modifier
                        .weight(1f).height(38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyanDim.copy(0.3f))
                        .border(1.dp, CyanGlow.copy(0.5f), RoundedCornerShape(6.dp))
                        .clickable { showDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("↓  Guardar .pkm", color = CyanGlow, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                // Regresar al editor
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(BgCardAlt)
                        .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                        .clickable { navController.popBackStack() }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("← Editor", color = TextSecondary, fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace)
                }
            }
        }
    }

    // ── Dialog guardar ────────────────────────────────────────────────────
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgCard)
                    .border(1.dp, CyanDim, RoundedCornerShape(10.dp))
            ) {
                Box(modifier = Modifier.fillMaxWidth().background(BgSurface)
                    .padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Text("Guardar formulario", color = CyanGlow, fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                }
                HorizontalDivider(color = CyanDim.copy(0.3f))

                Column(modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DarkField(nameForm, { nameForm = it }, "Nombre del formulario")
                    DarkField(autor, { autor = it }, "Autor")
                    DarkField(description, { description = it }, "Descripción")
                }

                HorizontalDivider(color = BorderColor)

                Row(
                    modifier = Modifier.fillMaxWidth().background(BgSurface)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(BgCardAlt)
                        .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                        .clickable { showDialog = false }
                        .padding(horizontal = 12.dp, vertical = 7.dp)) {
                        Text("Cancelar", color = TextSecondary, fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
                        .background(CyanGlow.copy(0.15f))
                        .border(1.dp, CyanDim, RoundedCornerShape(6.dp))
                        .clickable {
                            if (nameForm.isBlank()) {
                                Toast.makeText(context, "El nombre no puede estar vacío",
                                    Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            try {
                                val form   = ResultParser.currentForm
                                val lista  = if (form != null) listOf(form) else emptyList()
                                val contenido = generarPKMCompleto(nameForm, autor, description, lista)
                                val carpeta   = File(context.getExternalFilesDir(null), "formularios")
                                if (!carpeta.exists()) carpeta.mkdirs()
                                val archivo = File(carpeta, "$nameForm.pkm")

                                if (archivo.exists()) {
                                    saveStatus = SaveStatus.AlreadyExists
                                } else {
                                    archivo.writeText(contenido)
                                    saveStatus = SaveStatus.Success
                                    Log.d("PREVIEW", "Guardado: ${archivo.absolutePath}")
                                }
                                showDialog = false
                            } catch (e: Exception) {
                                saveStatus = SaveStatus.Error(e.message ?: "Error desconocido")
                                showDialog = false
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)) {
                        Text("Guardar", color = CyanGlow, fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

sealed class SaveStatus {
    object Idle         : SaveStatus()
    object Success      : SaveStatus()
    object AlreadyExists: SaveStatus()
    data class Error(val msg: String) : SaveStatus()
}

@Composable
private fun DarkField(value: String, onValueChange: (String) -> Unit, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        Box(modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(6.dp)).background(BgEditor)
            .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)) {
            if (value.isEmpty()) {
                Text(label, color = TextMuted, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
            BasicTextField(
                value = value, onValueChange = onValueChange,
                textStyle = TextStyle(color = TextPrimary, fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace),
                cursorBrush = SolidColor(CyanGlow),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun guardarFormulario(context: Context, nombre: String, contenido: String) {

    val cartpeta = File(context.getExternalFilesDir(null), "formularios")

    if (!cartpeta.exists()) {
        cartpeta.mkdirs()
    }

    val archivo = File(cartpeta, nombre)

    if (archivo.exists()) {

        Toast.makeText(context, "Ya existe un formulario con ese nombre", Toast.LENGTH_LONG).show()
    }
    archivo.writeText(contenido)

    Log.d("ARCHIVO", "GUARDANDO EN: ${archivo.absolutePath}")
}

@RequiresApi(Build.VERSION_CODES.O)
fun generarPKMCompleto(
    nameForm:    String,
    autor:       String,
    descripcion: String,
    forms:       List<FormsModel>
): String {

    val fecha = LocalDate.now()
    val hora  = LocalTime.now().withNano(0)
    val elementos = mutableListOf<Any>()

    forms.forEach { form ->
        form.elements.forEach { el ->
            // Solo recolectar elementos estructurales (no variables ni ciclos)
            recolectarElementoEstructural(el, elementos)
        }
    }

    var totalSecciones = 0
    var totalPreguntas = 0
    var totalAbiertas  = 0
    var totalDrop      = 0
    var totalSelect    = 0
    var totalMultiple  = 0
    var totalTextos    = 0

    fun contarRecursivo(el: Any) {
        when (el) {
            is SectionsModel -> {
                totalSecciones++
                el.elements?.forEach { sub -> contarRecursivo(sub) }
            }
            is OpenQuestion    -> { totalPreguntas++; totalAbiertas++ }
            is SelectQuestion  -> { totalPreguntas++; totalSelect++   }
            is DropQuestion    -> { totalPreguntas++; totalDrop++     }
            is MultipleQuestion -> { totalPreguntas++; totalMultiple++ }
            is TextModel       -> { totalTextos++ }
        }
    }
    elementos.forEach { contarRecursivo(it) }

    val contenido = StringBuilder()
    elementos.forEach { el -> contenido.append(serializarElemento(el)) }

    return buildString {
        appendLine("###")
        appendLine("Author: $autor")
        appendLine("Fecha: $fecha")
        appendLine("Hora: $hora")
        appendLine("Descripcion: $descripcion")
        appendLine("Total de Secciones: $totalSecciones")
        appendLine("Total de Preguntas: $totalPreguntas")
        appendLine("    Abiertas: $totalAbiertas")
        appendLine("    Desplegables: $totalDrop")
        appendLine("    Seleccion: $totalSelect")
        appendLine("    Multiples: $totalMultiple")
        appendLine("    Textos (Abiertos): $totalTextos")
        appendLine("###")
        appendLine()
        append(contenido.toString())
    }
}


private fun recolectarElementoEstructural(el: Any?, dest: MutableList<Any>) {
    if (el == null) return

    when (el) {
        // Elementos que SÍ se guardan en PKM
        is OpenQuestion,
        is SelectQuestion,
        is DropQuestion,
        is MultipleQuestion,
        is TextModel,
        is TableModel -> {
            dest.add(el)
        }

        is SectionsModel -> {
            // Guardar la sección
            dest.add(el)
            el.elements?.forEach { sub ->
                recolectarElementoEstructural(sub, dest)
            }
        }

        // Si es una pregunta genérica
        is QuestionModel -> dest.add(el)

        else -> {
        }
    }
}

fun serializarElemento(el: Any): String = buildString {
    when (el) {
        is TextModel        -> append(serializarTexto(el))
        is OpenQuestion     -> append(serializarOpen(el))
        is SelectQuestion   -> append(serializarSelect(el))
        is DropQuestion     -> append(serializarDrop(el))
        is MultipleQuestion -> append(serializarMultiple(el))
        is SectionsModel    -> append(serializarSeccion(el))
        is TableModel       -> append(serializarTabla(el))
    }
}

private fun serializarTexto(t: TextModel): String = buildString {
    val label = cleanStr(t.content ?: "")
    if (!t.styles.isNullOrEmpty()) {
        appendLine("<text=${t.width},${t.height},\"$label\">")
        append(serializarEstilos(t.styles))
        appendLine("</text>")
    } else {
        appendLine("<text=${t.width},${t.height},\"$label\"/>")
    }
}

private fun serializarOpen(q: OpenQuestion): String = buildString {
    val label = cleanStr(q.label ?: "")
    if (!q.styles.isNullOrEmpty()) {
        appendLine("<open=${q.width},${q.height},\"$label\">")
        append(serializarEstilos(q.styles))
        appendLine("</open>")
    } else {
        appendLine("<open=${q.width},${q.height},\"$label\"/>")
    }
}

private fun serializarSelect(q: SelectQuestion): String = buildString {
    val label = cleanStr(q.label ?: "")
    val opts = q.options.joinToString(",") { "\"${cleanStr(it)}\"" }
    val correct = q.correct.firstOrNull() ?: -1

    if (!q.styles.isNullOrEmpty()) {
        appendLine("<select=${q.width},${q.height},\"$label\",{$opts},$correct>")
        append(serializarEstilos(q.styles))
        appendLine("</select>")
    } else {
        appendLine("<select=${q.width},${q.height},\"$label\",{$opts},$correct/>")
    }
}

private fun serializarDrop(q: DropQuestion): String = buildString {
    val label = cleanStr(q.label ?: "")
    val opts = q.options.joinToString(",") { "\"${cleanStr(it)}\"" }
    val correct = q.correct.firstOrNull() ?: -1

    if (!q.styles.isNullOrEmpty()) {
        appendLine("<drop=${q.width},${q.height},\"$label\",{$opts},$correct>")
        append(serializarEstilos(q.styles))
        appendLine("</drop>")
    } else {
        appendLine("<drop=${q.width},${q.height},\"$label\",{$opts},$correct/>")
    }
}

private fun serializarMultiple(q: MultipleQuestion): String = buildString {
    val label = cleanStr(q.label ?: "")
    val opts = q.options.joinToString(",") { "\"${cleanStr(it)}\"" }
    val correct = q.correct.joinToString(",")

    if (!q.styles.isNullOrEmpty()) {
        appendLine("<multiple=${q.width},${q.height},\"$label\",{$opts},{$correct}>")
        append(serializarEstilos(q.styles))
        appendLine("</multiple>")
    } else {
        appendLine("<multiple=${q.width},${q.height},\"$label\",{$opts},{$correct}/>")
    }
}

private fun serializarSeccion(s: SectionsModel): String = buildString {
    appendLine("<section=${s.width},${s.height},${s.pointX},${s.pointY},${s.orientation}>")

    if (!s.styles.isNullOrEmpty()) {
        append(serializarEstilos(s.styles))
    }

    appendLine("<content>")
    s.elements?.forEach { sub ->
        if (sub != null) {
            append(serializarElemento(sub))
        }
    }
    appendLine("</content>")
    appendLine("</section>")
    appendLine()
}

private fun serializarTabla(t: TableModel): String = buildString {
    appendLine("<table=${t.width},${t.height},${t.pointX},${t.pointY}>")

    if (!t.styles.isNullOrEmpty()) {
        append(serializarEstilos(t.styles))
    }

    appendLine("<content>")
    t.elements.forEach { row ->
        appendLine("<line>")
        (row as? List<*>)?.forEach { cell ->
            appendLine("<element>")
            if (cell != null) {
                append(serializarElemento(cell))
            }
            appendLine("</element>")
        }
        appendLine("</line>")
    }
    appendLine("</content>")
    appendLine("</table>")
    appendLine()
}

/**
 * Serializa estilos - SOLO el contenido, no el nombre de la clase
 */
fun serializarEstilos(styles: List<Any>?): String {
    if (styles.isNullOrEmpty()) return ""
    return buildString {
        appendLine("<style>")
        styles.forEach { style ->
            when (style) {
                is ColorStyle -> {
                    appendLine("<color=${serializarColor(style.color)}/>")
                }
                is BackgroundStyle -> {
                    appendLine("<background color=${serializarColor(style.color)}/>")
                }
                is TextSizeStyle -> {
                    appendLine("<text size=${style.size}>")
                }
                is FontStyle -> {
                    appendLine("<font family=${style.font}/>")
                }
                is BorderStyle -> {
                    appendLine("<border,${style.width},${style.type},color=${serializarColor(style.color)}/>")
                }
                is String -> {
                    // Formato esperado: "SOLID:2:#FF3C43"
                    val parts = style.split(":")
                    if (parts.size >= 3) {
                        val tipo = parts[0]
                        val grosor = parts[1]
                        val color = parts[2]
                        appendLine("<border,$grosor,$tipo,color=$color/>")
                    } else {
                        appendLine("<!-- $style -->")
                    }
                }
                else -> {

                }
            }
        }
        appendLine("</style>")
    }
}


fun serializarColor(color: ColorValue?): String = when (color) {
    is HexaColor -> color.toPKM()
    is RgbColor -> color.toPKM()
    is HslColor -> color.toPKM()
    is BaseColor -> color.name
    null -> "#000000"
    else -> "#000000"
}


private fun cleanStr(s: String): String =
    s.replace("\"", "")
        .replace("😀", "@[:smile:]")
        .replace("🥲", "@[:sad:]")
        .replace("😐", "@[:serious:]")
        .replace("❤️", "@[:heart:]")
        .replace("⭐", "@[:star:]")
        .replace("😺", "@[:cat:]")
        .replace("😺", "@[:^^:]")
        .trim()

// Funciones de utilidad
fun serializeElement(element: Any): String = serializarElemento(element)
fun serializeSection(element: SectionsModel): String = serializarSeccion(element)
fun serializeTable(table: TableModel): String = serializarTabla(table)
fun serializeStyles(styles: List<Any>?): String = serializarEstilos(styles)
fun serializeColorValue(color: ColorValue?): String = serializarColor(color)
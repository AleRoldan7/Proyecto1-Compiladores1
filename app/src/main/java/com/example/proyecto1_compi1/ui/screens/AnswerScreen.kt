package com.example.proyecto1_compi1.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.generate_lenguaje.PKMCache
import com.example.proyecto1_compi1.modelo.color_style.*
import com.example.proyecto1_compi1.modelo.question.*
import com.example.proyecto1_compi1.modelo.table.TableModel
import com.example.proyecto1_compi1.poke.PokeRepository
import kotlinx.coroutines.launch

// ── Paleta ────────────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF0F1117)
private val BgCard        = Color(0xFF1A1D27)
private val BgCardAlt     = Color(0xFF21253A)
private val BgInput       = Color(0xFF12151F)
private val AccentBlue    = Color(0xFF4F8EF7)
private val AccentPurple  = Color(0xFF9B6DFF)
private val AccentGreen   = Color(0xFF3DD68C)
private val AccentRed     = Color(0xFFFF5E6D)
private val AccentYellow  = Color(0xFFFFD166)
private val PokeRed       = Color(0xFFEE1515)
private val PokeYellow    = Color(0xFFFFCC00)
private val TextPrimary   = Color(0xFFE8EAF6)
private val TextSecondary = Color(0xFF8890B0)
private val DividerColor  = Color(0xFF2A2E42)
private val BorderNormal  = Color(0xFF2A2E42)

// ── Modelos de respuesta ──────────────────────────────────────────────────────
data class UserAnswer(
    val idx:         Int,
    var openText:    String    = "",
    var selIdx:      Int       = -1,
    var multiSel:    Set<Int>  = emptySet()
)

data class AnswerResult(
    val idx:       Int,
    val isCorrect: Boolean,
    val feedback:  String
)

// ── Elemento aplanado del formulario ─────────────────────────────────────────
sealed class FormElement {
    data class Question(val q: QuestionModel, val answerIdx: Int) : FormElement()
    data class Texto(val t: TextModel)                            : FormElement()
    data class Tabla(val t: TableModel)                           : FormElement()
    data class Seccion(val s: SectionsModel)                      : FormElement()
}

// ─────────────────────────────────────────────────────────────────────────────
//  AnswerScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AnswerScreen(navController: NavController) {

    // ── Aplana TODOS los elementos preservando el orden original ─────────────
    val elementos: List<FormElement> = remember {
        val result  = mutableListOf<FormElement>()
        var qIdx    = 0
        fun aplanar(elem: Any?) {
            when (elem) {
                is OpenQuestion, is SelectQuestion,
                is DropQuestion, is MultipleQuestion -> {
                    result.add(FormElement.Question(elem as QuestionModel, qIdx++))
                }
                is TextModel     -> result.add(FormElement.Texto(elem))
                is TableModel    -> result.add(FormElement.Tabla(elem))
                is SectionsModel -> {
                    // Sección: añade la sección completa (contiene tabla, texto, preguntas)
                    result.add(FormElement.Seccion(elem))
                    // Las preguntas dentro también necesitan índice de respuesta
                    elem.elements?.forEach { sub -> aplanar(sub) }
                }
                is QuestionModel -> {
                    result.add(FormElement.Question(elem, qIdx++))
                }
            }
        }
        PKMCache.elements?.forEach { aplanar(it) }
        result
    }

    // ── Solo las preguntas para el mapa de respuestas ─────────────────────────
    val preguntas: List<Pair<Int, QuestionModel>> = remember {
        elementos.filterIsInstance<FormElement.Question>()
            .map { it.answerIdx to it.q }
    }

    val answers = remember {
        mutableStateMapOf<Int, UserAnswer>().also { map ->
            preguntas.forEach { (idx, _) -> map[idx] = UserAnswer(idx) }
        }
    }

    var showResults by remember { mutableStateOf(false) }
    var results     by remember { mutableStateOf<List<AnswerResult>>(emptyList()) }

    // ── Caso vacío ────────────────────────────────────────────────────────────
    if (elementos.isEmpty()) {
        EmptyFormScreen(navController); return
    }

    // ── Resultados ────────────────────────────────────────────────────────────
    if (showResults) {
        ResultsScreen(
            preguntas = preguntas.map { it.second },
            answers   = answers,
            results   = results,
            onRetry   = {
                answers.keys.forEach { answers[it] = UserAnswer(it) }
                showResults = false
                results     = emptyList()
            },
            onExit = { navController.popBackStack() }
        )
        return
    }

    // ── Pantalla principal ────────────────────────────────────────────────────
    val totalPreguntas    = preguntas.size
    val respondidas       = answers.values.count { isAnswered(it) }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)
        .windowInsetsPadding(WindowInsets.systemBars)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            FormHeader(
                respondidas    = respondidas,
                total          = totalPreguntas,
                onBack         = { navController.popBackStack() }
            )

            // Barra de progreso
            if (totalPreguntas > 0) {
                ProgressBar(respondidas, totalPreguntas)
            }

            // Contenido scrolleable — muestra TODO en orden
            LazyColumn(
                modifier            = Modifier.weight(1f).fillMaxWidth(),
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Muestra cada elemento en orden
                // Las secciones ya están incluidas — solo mostramos elementos
                // que NO son secciones (para no duplicar preguntas)
                itemsIndexed(elementos) { _, elem ->
                    when (elem) {
                        is FormElement.Texto    -> TextoCard(elem.t)
                        is FormElement.Tabla    -> TablaCard(elem.t, answers)
                        is FormElement.Seccion  -> SeccionCard(elem.s, answers)
                        is FormElement.Question -> {
                            val ans = answers[elem.answerIdx] ?: UserAnswer(elem.answerIdx)
                            PreguntaCard(
                                q         = elem.q,
                                answer    = ans,
                                num       = elem.answerIdx + 1,
                                onChange  = { answers[elem.answerIdx] = it }
                            )
                        }
                    }
                }

                // Botón enviar al final
                item {
                    Spacer(Modifier.height(8.dp))
                    BotonEnviar(
                        respondidas    = respondidas,
                        total          = totalPreguntas,
                        onSubmit       = {
                            results     = verificarRespuestas(preguntas.map { it.second }, answers)
                            showResults = true
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FormHeader(respondidas: Int, total: Int, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()
        .background(Brush.horizontalGradient(listOf(BgCard, BgCardAlt)))
        .padding(horizontal = 16.dp, vertical = 12.dp)) {

        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Text("←", color = TextSecondary, fontSize = 18.sp)
        }
        Column(modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Formulario", color = TextPrimary, fontSize = 16.sp,
                fontWeight = FontWeight.Bold)
            if (total > 0)
                Text("$respondidas / $total respondidas", color = TextSecondary, fontSize = 11.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Barra de progreso
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProgressBar(current: Int, total: Int) {
    val progress by animateFloatAsState(
        targetValue   = if (total > 0) current.toFloat() / total else 0f,
        animationSpec = tween(400, easing = EaseInOutCubic),
        label         = "progress"
    )
    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(DividerColor)) {
        Box(modifier = Modifier.fillMaxWidth(progress).height(4.dp)
            .background(Brush.horizontalGradient(listOf(AccentBlue, AccentPurple))))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Card de texto (solo display)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TextoCard(t: TextModel) {
    val texto = t.content ?: return
    val color = resolveTextColor(t.styles) ?: TextSecondary
    val size  = (resolveTextSize(t.styles) ?: 13).sp

    Box(modifier = Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(resolveBackground(t.styles) ?: BgCard.copy(0.5f))
        .padding(horizontal = 16.dp, vertical = 10.dp)) {
        Text(texto, color = color, fontSize = size,
            fontFamily = FontFamily.SansSerif, lineHeight = (size.value * 1.5).sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tabla completa con preguntas respondibles
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TablaCard(table: TableModel, answers: MutableMap<Int, UserAnswer> = mutableMapOf()) {
    if (table.elements.isEmpty()) return

    val rows     = table.elements
    val numCols  = rows.maxOfOrNull { it.size } ?: 1

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Encabezado de tabla
            Box(modifier = Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(AccentBlue.copy(0.15f), AccentPurple.copy(0.1f))))
                .padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text("Tabla", color = AccentBlue, fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp)
            }

            HorizontalDivider(color = DividerColor)

            // Filas
            rows.forEachIndexed { rowIdx, row ->
                val isHeader = rowIdx == 0

                Row(modifier = Modifier.fillMaxWidth()
                    .background(
                        when {
                            isHeader    -> AccentBlue.copy(0.08f)
                            rowIdx % 2 == 0 -> BgCard
                            else        -> BgCardAlt
                        }
                    )
                ) {
                    repeat(numCols) { colIdx ->
                        val cell = row.getOrNull(colIdx)

                        Box(modifier = Modifier
                            .weight(1f)
                            .then(
                                if (colIdx < numCols - 1)
                                    Modifier.border(0.5.dp, DividerColor)
                                else Modifier
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            TableCellContent(
                                cell     = cell,
                                isHeader = isHeader,
                                answers  = answers
                            )
                        }
                    }
                }

                if (rowIdx < rows.lastIndex)
                    HorizontalDivider(color = DividerColor.copy(0.5f), thickness = 0.5.dp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Celda de tabla — puede contener cualquier elemento
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TableCellContent(
    cell:     Any?,
    isHeader: Boolean,
    answers:  MutableMap<Int, UserAnswer>
) {
    when (cell) {
        null -> Box(Modifier.fillMaxWidth().height(24.dp))

        is TextModel -> {
            val texto = cell.content ?: ""
            val color = resolveTextColor(cell.styles)
                ?: if (isHeader) AccentBlue else TextPrimary
            val size  = (resolveTextSize(cell.styles) ?: if (isHeader) 12 else 11).sp
            Text(texto, color = color, fontSize = size,
                fontWeight = if (isHeader) FontWeight.SemiBold else FontWeight.Normal,
                fontFamily = FontFamily.SansSerif, lineHeight = (size.value * 1.4).sp)
        }

        is OpenQuestion -> {
            CeldaOpenQuestion(cell)
        }

        is SelectQuestion -> {
            CeldaSelectQuestion(cell)
        }

        is DropQuestion -> {
            CeldaDropQuestion(cell)
        }

        is MultipleQuestion -> {
            CeldaMultipleQuestion(cell)
        }

        is SectionsModel -> {
            // Sección dentro de celda — muestra sus elementos
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                cell.elements?.forEach { sub ->
                    TableCellContent(cell = sub, isHeader = false, answers = answers)
                }
            }
        }

        is TableModel -> TablaCard(cell, answers) // tabla anidada

        else -> Text(cell.toString(), color = TextSecondary,
            fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

// ── Open dentro de celda ──────────────────────────────────────────────────────
@Composable
private fun CeldaOpenQuestion(q: OpenQuestion) {
    var texto by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (!q.label.isNullOrBlank())
            Text(q.label, color = TextPrimary, fontSize = 10.sp,
                fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif,
                lineHeight = 14.sp)
        Box(modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(BgInput)
            .border(1.dp, if (texto.isNotEmpty()) AccentBlue.copy(0.5f) else DividerColor,
                RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)) {
            if (texto.isEmpty())
                Text("Respuesta...", color = TextSecondary.copy(0.5f),
                    fontSize = 10.sp, fontFamily = FontFamily.SansSerif)
            BasicTextField(
                value = texto, onValueChange = { texto = it },
                textStyle = TextStyle(color = TextPrimary, fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif),
                cursorBrush = SolidColor(AccentBlue),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ── Select dentro de celda ────────────────────────────────────────────────────
@Composable
private fun CeldaSelectQuestion(q: SelectQuestion) {
    var sel by remember { mutableStateOf(-1) }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        if (!q.label.isNullOrBlank())
            Text(q.label, color = TextPrimary, fontSize = 10.sp,
                fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif,
                lineHeight = 14.sp)
        q.options.forEachIndexed { idx, op ->
            val active = sel == idx
            Row(modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .background(if (active) AccentBlue.copy(0.12f) else BgInput)
                .border(1.dp, if (active) AccentBlue.copy(0.5f) else DividerColor,
                    RoundedCornerShape(5.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { sel = idx }
                .padding(horizontal = 7.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(Modifier.size(7.dp).clip(CircleShape)
                    .background(if (active) AccentBlue else DividerColor))
                Text(op, color = if (active) TextPrimary else TextSecondary,
                    fontSize = 10.sp, fontFamily = FontFamily.SansSerif)
            }
        }
    }
}

// ── Drop dentro de celda ──────────────────────────────────────────────────────
@Composable
private fun CeldaDropQuestion(q: DropQuestion) {
    var expanded by remember { mutableStateOf(false) }
    var sel      by remember { mutableStateOf(-1) }
    val opciones = remember { mutableStateListOf<String>() }
    var cargando by remember { mutableStateOf(false) }
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
                } catch (_: Exception) {}
                finally { cargando = false }
            }
        } else { opciones.clear(); opciones.addAll(q.options) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        if (!q.label.isNullOrBlank())
            Text(q.label, color = TextPrimary, fontSize = 10.sp,
                fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif,
                lineHeight = 14.sp)

        // Chip PokéAPI
        if (tienePoke) Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(PokeRed))
            Text("PokéAPI", color = PokeYellow, fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
        }

        Box(modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(BgInput)
            .border(1.dp, if (tienePoke) PokeYellow.copy(0.4f) else DividerColor,
                RoundedCornerShape(6.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { if (!cargando) expanded = !expanded }
            .padding(horizontal = 8.dp, vertical = 5.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                if (cargando) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(10.dp),
                            color = PokeYellow, strokeWidth = 1.dp)
                        Text("Cargando...", color = TextSecondary,
                            fontSize = 10.sp, fontFamily = FontFamily.SansSerif)
                    }
                } else Text(
                    if (sel >= 0 && sel < opciones.size) opciones[sel] else "Seleccionar...",
                    color = if (sel >= 0) TextPrimary else TextSecondary,
                    fontSize = 10.sp, fontFamily = FontFamily.SansSerif
                )
                Text(if (expanded) "▲" else "▼",
                    color = if (tienePoke) PokeYellow else AccentBlue, fontSize = 8.sp)
            }
        }

        if (expanded && opciones.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(BgCardAlt)
                .border(1.dp, DividerColor, RoundedCornerShape(6.dp))) {
                opciones.forEachIndexed { idx, nombre ->
                    Row(modifier = Modifier.fillMaxWidth()
                        .background(if (sel == idx) AccentBlue.copy(0.1f) else Color.Transparent)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { sel = idx; expanded = false }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        if (tienePoke) {
                            val desde = q.optionsPoke!!.getOrNull(1)?.toIntOrNull() ?: 1
                            Text("#${desde + idx}", color = PokeRed, fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                        }
                        Text(nombre,
                            color = if (sel == idx) TextPrimary else TextSecondary,
                            fontSize = 10.sp, fontFamily = FontFamily.SansSerif)
                    }
                    if (idx < opciones.lastIndex)
                        HorizontalDivider(color = DividerColor.copy(0.3f), thickness = 0.5.dp)
                }
            }
        }
    }
}

// ── Multiple dentro de celda ──────────────────────────────────────────────────
@Composable
private fun CeldaMultipleQuestion(q: MultipleQuestion) {
    val sels = remember { mutableStateListOf<Int>() }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        if (!q.label.isNullOrBlank())
            Text(q.label, color = TextPrimary, fontSize = 10.sp,
                fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif,
                lineHeight = 14.sp)
        q.options.forEachIndexed { idx, op ->
            val checked = idx in sels
            Row(modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .background(if (checked) AccentGreen.copy(0.1f) else BgInput)
                .border(1.dp, if (checked) AccentGreen.copy(0.4f) else DividerColor,
                    RoundedCornerShape(5.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (checked) sels.remove(idx) else sels.add(idx)
                }
                .padding(horizontal = 7.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(Modifier.size(9.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (checked) AccentGreen else DividerColor),
                    contentAlignment = Alignment.Center) {
                    if (checked) Text("✓", color = Color.Black, fontSize = 6.sp,
                        fontWeight = FontWeight.ExtraBold)
                }
                Text(op, color = if (checked) TextPrimary else TextSecondary,
                    fontSize = 10.sp, fontFamily = FontFamily.SansSerif)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Sección completa como card colapsable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SeccionCard(s: SectionsModel, answers: MutableMap<Int, UserAnswer>) {
    var expandido by remember { mutableStateOf(true) }

    Card(modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard.copy(0.7f)),
        border = BorderStroke(0.5.dp, DividerColor),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            // Header de sección
            Row(modifier = Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(
                    listOf(AccentPurple.copy(0.1f), Color.Transparent)))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { expandido = !expandido }
                .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(AccentPurple))
                    Text("Sección ${s.orientation}", color = AccentPurple, fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    val numElems = s.elements?.size ?: 0
                    if (numElems > 0) {
                        Text("$numElems elemento(s)", color = TextSecondary, fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace)
                    }
                }
                Text(if (expandido) "▲" else "▼", color = TextSecondary, fontSize = 10.sp)
            }

            AnimatedVisibility(visible = expandido,
                enter = expandVertically() + fadeIn(),
                exit  = shrinkVertically() + fadeOut()) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    s.elements?.forEach { sub ->
                        if (sub != null) {
                            SeccionElemento(sub, answers)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeccionElemento(elem: Any, answers: MutableMap<Int, UserAnswer>) {
    when (elem) {
        is TextModel     -> TextoCard(elem)
        is TableModel    -> TablaCard(elem, answers)
        is SectionsModel -> SeccionCard(elem, answers)
        is OpenQuestion, is SelectQuestion,
        is DropQuestion, is MultipleQuestion -> {
            // Preguntas dentro de sección — tienen su propio estado local
            PreguntaEnSeccion(elem as QuestionModel)
        }
        is QuestionModel -> PreguntaEnSeccion(elem)
    }
}

@Composable
private fun PreguntaEnSeccion(q: QuestionModel) {
    // Estado local para preguntas dentro de secciones
    var ans by remember { mutableStateOf(UserAnswer(0)) }
    PreguntaCard(q = q, answer = ans, num = null,
        onChange = { ans = it })
}

// ─────────────────────────────────────────────────────────────────────────────
//  Card de pregunta principal
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun PreguntaCard(
    q:        QuestionModel,
    answer:   UserAnswer,
    num:      Int?,
    onChange: (UserAnswer) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(6.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {

            // Tipo + número
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (num != null) {
                    Box(Modifier.size(32.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(AccentBlue, AccentPurple))),
                        contentAlignment = Alignment.Center) {
                        Text("$num", color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 13.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val (label, color) = tipoPregunta(q)
                    Box(Modifier.clip(RoundedCornerShape(5.dp))
                        .background(color.copy(0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(label, color = color, fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold)
                    }
                    // PokeAPI chip
                    if (q is DropQuestion && !q.optionsPoke.isNullOrEmpty()) {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(PokeRed.copy(0.12f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)) {
                            Text("PokéAPI", color = PokeYellow, fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Enunciado
            if (!q.label.isNullOrBlank()) {
                Text(q.label, color = TextPrimary, fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, lineHeight = 22.sp)
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = DividerColor)
                Spacer(Modifier.height(14.dp))
            }

            // Campo de respuesta
            when (q) {
                is OpenQuestion -> OpenField(answer, onChange)
                is SelectQuestion -> OptionsField(
                    options = q.options ?: emptyList(),
                    multi = false, multiSel = answer.multiSel,
                    selIdx = answer.selIdx,
                    onSingle = { onChange(answer.copy(selIdx = it)) },
                    onMulti = {}
                )
                is DropQuestion -> DropField(
                    q = q,
                    selIdx = answer.selIdx,
                    onSelect = { onChange(answer.copy(selIdx = it)) }
                )
                is MultipleQuestion -> OptionsField(
                    options = q.options ?: emptyList(),
                    multi = true, multiSel = answer.multiSel,
                    selIdx = -1,
                    onSingle = {},
                    onMulti = { idx ->
                        val cur = answer.multiSel.toMutableSet()
                        if (idx in cur) cur.remove(idx) else cur.add(idx)
                        onChange(answer.copy(multiSel = cur))
                    }
                )
                else -> Text("Tipo no soportado", color = TextSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Campos de respuesta
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OpenField(answer: UserAnswer, onChange: (UserAnswer) -> Unit) {
    OutlinedTextField(
        value         = answer.openText,
        onValueChange = { onChange(answer.copy(openText = it)) },
        placeholder   = { Text("Escribe tu respuesta aquí...", color = TextSecondary) },
        modifier      = Modifier.fillMaxWidth().heightIn(min = 100.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AccentBlue,
            unfocusedBorderColor = DividerColor,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary,
            cursorColor          = AccentBlue
        ),
        shape    = RoundedCornerShape(12.dp),
        maxLines = 5
    )
}

@Composable
private fun OptionsField(
    options:  List<String>,
    multi:    Boolean,
    multiSel: Set<Int>,
    selIdx:   Int,
    onSingle: (Int) -> Unit,
    onMulti:  (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { idx, op ->
            val isSelected = if (multi) idx in multiSel else idx == selIdx
            val bgColor by animateColorAsState(
                if (isSelected) AccentBlue.copy(0.15f) else BgCardAlt, tween(200), "bg_$idx")
            val borderColor by animateColorAsState(
                if (isSelected) AccentBlue else DividerColor, tween(200), "border_$idx")

            Row(modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (multi) onMulti(idx) else onSingle(idx)
                }
                .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (multi) {
                    Checkbox(checked = isSelected, onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = AccentBlue, uncheckedColor = TextSecondary))
                } else {
                    RadioButton(selected = isSelected, onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AccentBlue, unselectedColor = TextSecondary))
                }
                Text(op, color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun DropField(q: DropQuestion, selIdx: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val opciones = remember { mutableStateListOf<String>() }
    var cargando by remember { mutableStateOf(false) }
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
                } catch (_: Exception) {}
                finally { cargando = false }
            }
        } else { opciones.clear(); opciones.addAll(q.options) }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCardAlt)
            .border(1.dp,
                when {
                    selIdx >= 0  -> AccentBlue
                    tienePoke    -> PokeYellow.copy(0.5f)
                    else         -> DividerColor
                },
                RoundedCornerShape(12.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { if (!cargando) expanded = true }
            .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            if (cargando) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(14.dp), color = PokeYellow, strokeWidth = 1.5.dp)
                    Text("Cargando Pokémon...", color = TextSecondary, fontSize = 14.sp)
                }
            } else {
                Text(
                    if (selIdx >= 0 && selIdx < opciones.size) opciones[selIdx]
                    else "Selecciona una opción",
                    color = if (selIdx >= 0) TextPrimary else TextSecondary, fontSize = 14.sp
                )
            }
            Text("▼", color = if (tienePoke) PokeYellow else AccentBlue)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.background(BgCard)) {
            opciones.forEachIndexed { idx, op ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (tienePoke) {
                                val desde = q.optionsPoke!!.getOrNull(1)?.toIntOrNull() ?: 1
                                Box(Modifier.clip(RoundedCornerShape(3.dp))
                                    .background(PokeRed.copy(0.15f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)) {
                                    Text("#${desde + idx}", color = PokeRed, fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                                }
                            }
                            Text(op, color = if (idx == selIdx) AccentBlue else TextPrimary,
                                fontSize = 14.sp)
                        }
                    },
                    onClick = { onSelect(idx); expanded = false }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Botón enviar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BotonEnviar(respondidas: Int, total: Int, onSubmit: () -> Unit) {
    val allAnswered = respondidas == total && total > 0
    Button(
        onClick  = onSubmit,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = if (allAnswered) AccentGreen else AccentBlue
        )
    ) {
        Text(
            text       = if (allAnswered) "✓  Enviar formulario" else "Enviar ($respondidas/$total respondidas)",
            color      = Color.White,
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Pantalla de resultados
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultsScreen(
    preguntas: List<QuestionModel>,
    answers:   Map<Int, UserAnswer>,
    results:   List<AnswerResult>,
    onRetry:   () -> Unit,
    onExit:    () -> Unit
) {
    val correctas = results.count { it.isCorrect }
    val total     = results.size
    val score     = if (total > 0) (correctas * 100) / total else 0
    val scoreColor = when {
        score >= 80 -> AccentGreen
        score >= 50 -> AccentYellow
        else        -> AccentRed
    }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)
        .windowInsetsPadding(WindowInsets.systemBars)) {
        LazyColumn(modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {

            item {
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard)) {
                    Column(modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Resultados", color = TextPrimary, fontSize = 20.sp,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(20.dp))
                        Box(Modifier.size(100.dp).clip(CircleShape)
                            .background(scoreColor.copy(0.15f))
                            .border(3.dp, scoreColor, CircleShape),
                            contentAlignment = Alignment.Center) {
                            Text("$score%", color = scoreColor, fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("$correctas de $total correctas", color = TextSecondary, fontSize = 14.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(resultadoMensaje(score), color = scoreColor, fontSize = 13.sp,
                            textAlign = TextAlign.Center)
                    }
                }
            }

            item { Text("Detalle por pregunta", color = TextPrimary, fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold) }

            itemsIndexed(results) { _, result ->
                val q = preguntas.getOrNull(result.idx)
                ResultItemCard(q, answers[result.idx], result)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onExit, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DividerColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)) {
                        Text("Salir")
                    }
                    Button(onClick = onRetry, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) {
                        Text("↺  Reintentar", color = Color.White)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ResultItemCard(q: QuestionModel?, answer: UserAnswer?, result: AnswerResult) {
    val borderColor = if (result.isCorrect) AccentGreen else AccentRed
    Card(modifier = Modifier.fillMaxWidth()
        .border(1.dp, borderColor.copy(0.4f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isCorrect) AccentGreen.copy(0.06f) else AccentRed.copy(0.06f)
        )) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pregunta ${result.idx + 1}", color = TextPrimary, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold)
                Text(if (result.isCorrect) "✓" else "✗", color = borderColor,
                    fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
            if (!q?.label.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(q!!.label, color = TextSecondary, fontSize = 12.sp, maxLines = 2)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))
            Text(result.feedback, color = borderColor, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EmptyFormScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(BgDark),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("No hay preguntas en este formulario", color = TextSecondary, fontSize = 14.sp)
            Button(onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) {
                Text("Volver", color = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Lógica de verificación
// ─────────────────────────────────────────────────────────────────────────────
fun verificarRespuestas(
    preguntas: List<QuestionModel>,
    answers:   Map<Int, UserAnswer>
): List<AnswerResult> = preguntas.mapIndexed { idx, q ->
    val a = answers[idx] ?: UserAnswer(idx)
    when (q) {
        is OpenQuestion -> AnswerResult(idx, a.openText.isNotBlank(),
            if (a.openText.isNotBlank()) "Respuesta: \"${a.openText.take(50)}\"" else "Sin respuesta.")
        is SelectQuestion -> {
            val c = q.correct?.firstOrNull() ?: -1
            val ok = c >= 0 && a.selIdx == c
            val ct = q.options?.getOrNull(c) ?: "?"
            val ut = q.options?.getOrNull(a.selIdx) ?: "Sin respuesta"
            AnswerResult(idx, ok,
                if (c == -1) "Sin respuesta correcta definida. Elegiste: $ut"
                else if (ok) "✓ Correcto: $ct"
                else "✗ Tu respuesta: $ut | Correcta: $ct")
        }
        is DropQuestion -> {
            val c = q.correct?.firstOrNull() ?: -1
            val ok = c == -1 || a.selIdx == c
            val ct = q.options?.getOrNull(c) ?: "Cualquiera"
            val ut = q.options?.getOrNull(a.selIdx) ?: "Sin respuesta"
            AnswerResult(idx, ok,
                if (c == -1) "Elegiste: $ut"
                else if (ok) "✓ Correcto: $ct"
                else "✗ Tu respuesta: $ut | Correcta: $ct")
        }
        is MultipleQuestion -> {
            val cs = q.correct?.toSet() ?: emptySet()
            val ok = cs.isEmpty() || a.multiSel == cs
            val cl = cs.mapNotNull { q.options?.getOrNull(it) }.joinToString(", ")
            val ul = a.multiSel.mapNotNull { q.options?.getOrNull(it) }.joinToString(", ")
            AnswerResult(idx, ok,
                if (ok) "✓ Correcto${if (cl.isNotEmpty()) ": $cl" else ""}"
                else "✗ Tus opciones: $ul | Correctas: $cl")
        }
        else -> AnswerResult(idx, false, "Tipo no evaluable.")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helpers de estilos
// ─────────────────────────────────────────────────────────────────────────────
private fun resolveTextColor(styles: List<Any>?): Color? {
    styles?.forEach { if (it is ColorStyle) return it.color?.toComposeColor() }
    return null
}
private fun resolveBackground(styles: List<Any>?): Color? {
    styles?.forEach { if (it is BackgroundStyle) return it.color?.toComposeColor() }
    return null
}
private fun resolveTextSize(styles: List<Any>?): Int? {
    styles?.forEach { if (it is TextSizeStyle) return it.size }
    return null
}
private fun tipoPregunta(q: QuestionModel): Pair<String, Color> = when (q) {
    is OpenQuestion    -> "Abierta" to AccentBlue
    is SelectQuestion  -> "Selección única" to AccentPurple
    is DropQuestion    -> "Desplegable" to AccentYellow
    is MultipleQuestion -> "Múltiple" to AccentGreen
    else               -> "Pregunta" to AccentBlue
}
private fun isAnswered(a: UserAnswer) =
    a.openText.isNotBlank() || a.selIdx >= 0 || a.multiSel.isNotEmpty()
private fun resultadoMensaje(score: Int) = when {
    score == 100 -> "¡Perfecto! Todas correctas 🎉"
    score >= 80  -> "¡Muy bien! Casi perfecto"
    score >= 60  -> "Buen trabajo, sigue practicando"
    score >= 40  -> "Puedes mejorar, ¡inténtalo de nuevo!"
    else         -> "Sigue estudiando, ¡tú puedes!"
}
fun ColorValue.toComposeColor(): Color = when (this) {
    is HexaColor -> colorCompose()
    is RgbColor  -> colorCompose()
    is HslColor  -> colorCompose()
    is BaseColor -> when (this) {
        BaseColor.RED    -> Color(0xFFEF5350)
        BaseColor.BLUE   -> Color(0xFF42A5F5)
        BaseColor.GREEN  -> Color(0xFF66BB6A)
        BaseColor.PURPLE -> Color(0xFFAB47BC)
        BaseColor.SKY    -> Color(0xFF26C6DA)
        BaseColor.YELLOW -> Color(0xFFFFEE58)
        BaseColor.BLACK  -> Color(0xFF212121)
        BaseColor.WHITE  -> Color(0xFFFAFAFA)
        else             -> Color.Gray
    }
    else -> Color.Gray
}
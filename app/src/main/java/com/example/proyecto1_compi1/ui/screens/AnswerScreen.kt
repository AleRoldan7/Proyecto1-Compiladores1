package com.example.proyecto1_compi1.ui.screens


import android.R.attr.contentDescription
import android.R.attr.tint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto1_compi1.generate_lenguaje.PKMCache
import com.example.proyecto1_compi1.modelo.question.*


private val BgDark       = Color(0xFF0F1117)
private val BgCard       = Color(0xFF1A1D27)
private val BgCardAlt    = Color(0xFF21253A)
private val AccentBlue   = Color(0xFF4F8EF7)
private val AccentPurple = Color(0xFF9B6DFF)
private val AccentGreen  = Color(0xFF3DD68C)
private val AccentRed    = Color(0xFFFF5E6D)
private val AccentYellow = Color(0xFFFFD166)
private val TextPrimary  = Color(0xFFE8EAF6)
private val TextSecondary= Color(0xFF8890B0)
private val DividerColor = Color(0xFF2A2E42)

data class UserAnswer(
    val questionIndex: Int,
    var openText:       String         = "",
    var selectedIndex:  Int            = -1,
    var multiSelected:  Set<Int>       = emptySet()
)


data class AnswerResult(
    val questionIndex: Int,
    val isCorrect:     Boolean,
    val feedback:      String
)

@Composable()
fun AnswerScreen(navController: NavController) {
    // ── 1. Extraer SOLO preguntas de PKMCache (excluye secciones, textos, etc.) ──
    val allQuestions: List<QuestionModel> = remember {
        PKMCache.elements.filterIsInstance<QuestionModel>()
            .also { list ->
                // También buscar dentro de secciones
            } +
                PKMCache.elements
                    .filterIsInstance<SectionsModel>()
                    .flatMap { sec ->
                        sec.elements?.filterIsInstance<QuestionModel>() ?: emptyList()
                    }
    }

    // ── 2. Paginación: una pregunta por página ──
    val totalPages  = allQuestions.size
    var currentPage by remember { mutableStateOf(0) }

    // ── 3. Mapa de respuestas: índice → UserAnswer ──
    val answers = remember {
        mutableStateMapOf<Int, UserAnswer>().also { map ->
            allQuestions.forEachIndexed { i, _ -> map[i] = UserAnswer(i) }
        }
    }

    // ── 4. Estado del resultado final ──
    var showResults  by remember { mutableStateOf(false) }
    var results      by remember { mutableStateOf<List<AnswerResult>>(emptyList()) }

    // ─── Caso: no hay preguntas ───────────────────────────────────────────────
    if (totalPages == 0) {
        EmptyFormScreen(navController)
        return
    }

    // ─── Caso: mostrar resultados ─────────────────────────────────────────────
    if (showResults) {
        ResultsScreen(
            questions = allQuestions,
            answers   = answers,
            results   = results,
            onRetry   = {
                answers.keys.forEach { answers[it] = UserAnswer(it) }
                currentPage  = 0
                showResults  = false
                results      = emptyList()
            },
            onExit    = { navController.popBackStack() }
        )
        return
    }

    // ─── Pantalla de respuesta ────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──────────────────────────────────────────────────────
            FormHeader(
                currentPage = currentPage,
                totalPages  = totalPages,
                onBack      = { navController.popBackStack() }
            )

            // ── Barra de progreso ────────────────────────────────────────────
            ProgressBar(current = currentPage + 1, total = totalPages)

            // ── Pregunta actual ──────────────────────────────────────────────
            val question = allQuestions[currentPage]
            val answer   = answers[currentPage] ?: UserAnswer(currentPage)

            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f),
                label    = "question_anim"
            ) { page ->
                val q = allQuestions[page]
                val a = answers[page] ?: UserAnswer(page)

                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        QuestionCard(
                            question     = q,
                            answer       = a,
                            questionNum  = page + 1,
                            onAnswerChange = { updated -> answers[page] = updated }
                        )
                    }
                }
            }

            // ── Navegación inferior ──────────────────────────────────────────
            NavigationBar(
                currentPage  = currentPage,
                totalPages   = totalPages,
                answers      = answers,
                onPrev       = { if (currentPage > 0) currentPage-- },
                onNext       = { if (currentPage < totalPages - 1) currentPage++ },
                onSubmit     = {
                    results     = verificarRespuestas(allQuestions, answers)
                    showResults = true
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FormHeader(currentPage: Int, totalPages: Int, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(BgCard, BgCardAlt))
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Text("←")
        }

        Column(
            modifier          = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "Formulario",
                color      = TextPrimary,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text     = "Pregunta ${currentPage + 1} de $totalPages",
                color    = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Barra de progreso animada
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProgressBar(current: Int, total: Int) {
    val progress by animateFloatAsState(
        targetValue    = current.toFloat() / total.toFloat(),
        animationSpec  = tween(400, easing = EaseInOutCubic),
        label          = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(DividerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(listOf(AccentBlue, AccentPurple))
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tarjeta de pregunta (despacha al tipo correcto)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun QuestionCard(
    question:       QuestionModel,
    answer:         UserAnswer,
    questionNum:    Int,
    onAnswerChange: (UserAnswer) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // Número + tipo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier           = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(AccentBlue, AccentPurple))
                        ),
                    contentAlignment   = Alignment.Center
                ) {
                    Text(
                        text       = "$questionNum",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp
                    )
                }

                Text(
                    text     = questionTypeLabel(question),
                    color    = AccentBlue,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentBlue.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Etiqueta / enunciado
            Text(
                text       = question.label ?: "Pregunta $questionNum",
                color      = TextPrimary,
                fontSize   = 17.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(20.dp))

            // Respuesta según tipo
            when (question) {
                is OpenQuestion -> OpenAnswerField(answer, onAnswerChange)
                is SelectQuestion -> OptionsList(
                    options      = question.options ?: emptyList(),
                    selectedIdx  = answer.selectedIndex,
                    multi        = false,
                    multiSelected = answer.multiSelected,
                    onSingle     = { idx -> onAnswerChange(answer.copy(selectedIndex = idx)) },
                    onMulti      = {}
                )
                is DropQuestion -> DropAnswerField(
                    options     = question.options ?: emptyList(),
                    selectedIdx = answer.selectedIndex,
                    onSelect    = { idx -> onAnswerChange(answer.copy(selectedIndex = idx)) }
                )
                is MultipleQuestion -> OptionsList(
                    options       = question.options ?: emptyList(),
                    selectedIdx   = -1,
                    multi         = true,
                    multiSelected = answer.multiSelected,
                    onSingle      = {},
                    onMulti       = { idx ->
                        val current = answer.multiSelected.toMutableSet()
                        if (idx in current) current.remove(idx) else current.add(idx)
                        onAnswerChange(answer.copy(multiSelected = current))
                    }
                )
                else -> Text("Tipo no soportado", color = TextSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Campo de texto para pregunta abierta
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OpenAnswerField(answer: UserAnswer, onAnswerChange: (UserAnswer) -> Unit) {
    OutlinedTextField(
        value         = answer.openText,
        onValueChange = { onAnswerChange(answer.copy(openText = it)) },
        placeholder   = { Text("Escribe tu respuesta aquí...", color = TextSecondary) },
        modifier      = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AccentBlue,
            unfocusedBorderColor = DividerColor,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary,
            cursorColor          = AccentBlue
        ),
        shape         = RoundedCornerShape(14.dp),
        maxLines      = 6
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Lista de opciones (select / multiple)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OptionsList(
    options:       List<String>,
    selectedIdx:   Int,
    multi:         Boolean,
    multiSelected: Set<Int>,
    onSingle:      (Int) -> Unit,
    onMulti:       (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEachIndexed { idx, option ->
            val isSelected = if (multi) idx in multiSelected else idx == selectedIdx
            OptionItem(
                text       = option,
                selected   = isSelected,
                multi      = multi,
                onClick    = { if (multi) onMulti(idx) else onSingle(idx) }
            )
        }
    }
}

@Composable
private fun OptionItem(
    text:     String,
    selected: Boolean,
    multi:    Boolean,
    onClick:  () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue   = if (selected) AccentBlue.copy(alpha = 0.18f) else BgCardAlt,
        animationSpec = tween(200),
        label         = "option_bg"
    )
    val borderColor by animateColorAsState(
        targetValue   = if (selected) AccentBlue else DividerColor,
        animationSpec = tween(200),
        label         = "option_border"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (multi) {
            Checkbox(
                checked = selected,
                onCheckedChange = null,
                colors  = CheckboxDefaults.colors(
                    checkedColor   = AccentBlue,
                    uncheckedColor = TextSecondary
                )
            )
        } else {
            RadioButton(
                selected = selected,
                onClick  = null,
                colors   = RadioButtonDefaults.colors(
                    selectedColor   = AccentBlue,
                    unselectedColor = TextSecondary
                )
            )
        }

        Text(
            text       = text,
            color      = if (selected) TextPrimary else TextSecondary,
            fontSize   = 15.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Dropdown para pregunta desplegable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DropAnswerField(
    options:     List<String>,
    selectedIdx: Int,
    onSelect:    (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = if (selectedIdx >= 0 && selectedIdx < options.size)
        options[selectedIdx] else "Selecciona una opción"

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BgCardAlt)
                .border(
                    1.dp,
                    if (selectedIdx >= 0) AccentBlue else DividerColor,
                    RoundedCornerShape(12.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text  = selectedText,
                color = if (selectedIdx >= 0) TextPrimary else TextSecondary,
                fontSize = 15.sp
            )
            Text("▲")
            Text("▼")
        }

        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(BgCard)
        ) {
            options.forEachIndexed { idx, option ->
                DropdownMenuItem(
                    text   = {
                        Text(
                            option,
                            color = if (idx == selectedIdx) AccentBlue else TextPrimary
                        )
                    },
                    onClick = {
                        onSelect(idx)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Barra de navegación inferior
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NavigationBar(
    currentPage:  Int,
    totalPages:   Int,
    answers:      Map<Int, UserAnswer>,
    onPrev:       () -> Unit,
    onNext:       () -> Unit,
    onSubmit:     () -> Unit
) {
    val answeredCount = answers.values.count { isAnswered(it) }
    val isLastPage    = currentPage == totalPages - 1
    val allAnswered   = answeredCount == totalPages

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color    = BgCard,
        shadowElevation = 16.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Indicador de respondidas
            Text(
                text     = "$answeredCount de $totalPages respondidas",
                color    = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Botón Anterior
                OutlinedButton(
                    onClick  = onPrev,
                    enabled  = currentPage > 0,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor         = TextPrimary,
                        disabledContentColor = TextSecondary
                    ),
                    border   = BorderStroke(1.dp, if (currentPage > 0) AccentBlue else DividerColor)
                ) {
                    Text("←")
                    Spacer(Modifier.width(4.dp))
                    Text("Anterior")
                }

                // Botón Siguiente o Enviar
                if (!isLastPage) {
                    Button(
                        onClick  = onNext,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue
                        )
                    ) {
                        Text("Siguiente", color = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("→")
                    }
                } else {
                    Button(
                        onClick  = onSubmit,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (allAnswered) AccentGreen else AccentBlue
                        )
                    ) {
                        Text("✓")
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (allAnswered) "Enviar" else "Enviar igualmente",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Pantalla de resultados
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultsScreen(
    questions: List<QuestionModel>,
    answers:   Map<Int, UserAnswer>,
    results:   List<AnswerResult>,
    onRetry:   () -> Unit,
    onExit:    () -> Unit
) {
    val correctCount = results.count { it.isCorrect }
    val total        = results.size
    val score        = if (total > 0) (correctCount * 100) / total else 0

    val scoreColor = when {
        score >= 80 -> AccentGreen
        score >= 50 -> AccentYellow
        else        -> AccentRed
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        LazyColumn(
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Tarjeta resumen ──────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(24.dp),
                    colors   = CardDefaults.cardColors(containerColor = BgCard)
                ) {
                    Column(
                        modifier            = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = "Resultados",
                            color      = TextPrimary,
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(24.dp))

                        Box(
                            modifier         = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(scoreColor.copy(alpha = 0.15f))
                                .border(3.dp, scoreColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = "$score%",
                                color      = scoreColor,
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text     = "$correctCount de $total correctas",
                            color    = TextSecondary,
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text       = resultadoMensaje(score),
                            color      = scoreColor,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign  = TextAlign.Center
                        )
                    }
                }
            }

            // ── Detalle por pregunta ─────────────────────────────────────────
            item {
                Text(
                    text       = "Detalle",
                    color      = TextPrimary,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            itemsIndexed(results) { _, result ->
                val question = questions[result.questionIndex]
                val answer   = answers[result.questionIndex]

                ResultItemCard(
                    question    = question,
                    answer      = answer,
                    result      = result,
                    questionNum = result.questionIndex + 1
                )
            }

            // ── Botones finales ──────────────────────────────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick  = onExit,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        border   = BorderStroke(1.dp, DividerColor),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                    ) {
                        Text("Salir")
                    }

                    Button(
                        onClick  = onRetry,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("↺")
                        Spacer(Modifier.width(6.dp))
                        Text("Reintentar", color = Color.White)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Tarjeta de resultado individual
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultItemCard(
    question:    QuestionModel,
    answer:      UserAnswer?,
    result:      AnswerResult,
    questionNum: Int
) {
    val borderColor = if (result.isCorrect) AccentGreen else AccentRed
    val iconColor   = if (result.isCorrect) AccentGreen else AccentRed
    val icon        = if (result.isCorrect) Text("↺") else Text("✗")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = if (result.isCorrect)
                AccentGreen.copy(alpha = 0.07f)
            else
                AccentRed.copy(alpha = 0.07f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = "Pregunta $questionNum",
                    color      = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )

            }

            Spacer(Modifier.height(8.dp))
            Text(
                text     = question.label ?: "",
                color    = TextSecondary,
                fontSize = 13.sp,
                maxLines = 2
            )
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(10.dp))
            Text(
                text     = result.feedback,
                color    = if (result.isCorrect) AccentGreen else AccentRed,
                fontSize = 13.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Pantalla vacía
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyFormScreen(navController: NavController) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No hay preguntas en este formulario", color = TextSecondary)
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors  = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Volver", color = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Lógica de verificación
// ─────────────────────────────────────────────────────────────────────────────
fun verificarRespuestas(
    questions: List<QuestionModel>,
    answers:   Map<Int, UserAnswer>
): List<AnswerResult> {
    return questions.mapIndexed { idx, question ->
        val answer = answers[idx] ?: UserAnswer(idx)

        when (question) {
            // Pregunta abierta: siempre "correcta" si no está vacía
            is OpenQuestion -> {
                val responded = answer.openText.isNotBlank()
                AnswerResult(
                    questionIndex = idx,
                    isCorrect     = responded,
                    feedback      = if (responded)
                        "Respuesta registrada: \"${answer.openText.take(60)}...\""
                    else
                        "No respondiste esta pregunta."
                )
            }

            // Select: compara índice seleccionado vs correct
            is SelectQuestion -> {
                val correctIdx  = question.correct?.firstOrNull() ?: -1
                val isCorrect   = correctIdx >= 0 && answer.selectedIndex == correctIdx
                val correctText = question.options?.getOrNull(correctIdx) ?: "?"
                val userText    = question.options?.getOrNull(answer.selectedIndex) ?: "Sin respuesta"
                AnswerResult(
                    questionIndex = idx,
                    isCorrect     = isCorrect,
                    feedback      = if (isCorrect)
                        "✓ Correcto: $correctText"
                    else if (correctIdx == -1)
                        "Sin respuesta correcta definida. Respondiste: $userText"
                    else
                        "✗ Incorrecto. Tu respuesta: $userText | Correcta: $correctText"
                )
            }

            // Drop: mismo mecanismo que select
            is DropQuestion -> {
                val correctIdx  = question.correct?.firstOrNull() ?: -1
                val isCorrect   = correctIdx == -1 || answer.selectedIndex == correctIdx
                val correctText = question.options?.getOrNull(correctIdx) ?: "Cualquiera"
                val userText    = question.options?.getOrNull(answer.selectedIndex) ?: "Sin respuesta"
                AnswerResult(
                    questionIndex = idx,
                    isCorrect     = isCorrect,
                    feedback      = if (correctIdx == -1)
                        "Pregunta sin respuesta correcta. Elegiste: $userText"
                    else if (isCorrect)
                        "✓ Correcto: $correctText"
                    else
                        "✗ Incorrecto. Tu respuesta: $userText | Correcta: $correctText"
                )
            }

            // Multiple: compara conjuntos
            is MultipleQuestion -> {
                val correctSet  = question.correct?.toSet() ?: emptySet()
                val isCorrect   = correctSet.isEmpty() || answer.multiSelected == correctSet
                val correctLabels = correctSet.mapNotNull { question.options?.getOrNull(it) }
                val userLabels    = answer.multiSelected.mapNotNull { question.options?.getOrNull(it) }
                AnswerResult(
                    questionIndex = idx,
                    isCorrect     = isCorrect,
                    feedback      = if (isCorrect)
                        "✓ Correcto: ${correctLabels.joinToString(", ")}"
                    else
                        "✗ Tus opciones: ${userLabels.joinToString(", ")} | " +
                                "Correctas: ${correctLabels.joinToString(", ")}"
                )
            }

            else -> AnswerResult(idx, false, "Tipo de pregunta no evaluable.")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────────────────────
private fun isAnswered(answer: UserAnswer): Boolean =
    answer.openText.isNotBlank() ||
            answer.selectedIndex >= 0   ||
            answer.multiSelected.isNotEmpty()

private fun questionTypeLabel(question: QuestionModel): String = when (question) {
    is OpenQuestion    -> "Abierta"
    is SelectQuestion  -> "Selección única"
    is DropQuestion    -> "Desplegable"
    is MultipleQuestion -> "Múltiple"
    else               -> "Pregunta"
}

private fun resultadoMensaje(score: Int): String = when {
    score == 100 -> "¡Perfecto! Todas correctas 🎉"
    score >= 80  -> "¡Muy bien! Casi perfecto"
    score >= 60  -> "Buen trabajo, sigue practicando"
    score >= 40  -> "Puedes mejorar, ¡inténtalo de nuevo!"
    else         -> "Sigue estudiando, ¡tú puedes!"
}

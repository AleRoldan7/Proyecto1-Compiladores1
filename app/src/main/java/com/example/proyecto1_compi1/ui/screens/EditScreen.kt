package com.example.proyecto1_compi1.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto1_compi1.analizador.form.Lexer
import com.example.proyecto1_compi1.analizador.form.Parser
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.token.Token
import com.example.proyecto1_compi1.ui.ViewModel.EditViewModel
import java.io.StringReader
import kotlin.math.max


private val BgDeep       = Color(0xFF090C10)
private val BgEditor     = Color(0xFF0D1117)
private val BgSurface    = Color(0xFF161B22)
private val BgCard       = Color(0xFF1C2128)
private val BgCardAlt    = Color(0xFF21262D)
private val CyanGlow     = Color(0xFF39D0D8)
private val CyanDim      = Color(0xFF1A6E73)
private val GreenOk      = Color(0xFF3FB950)
private val RedErr       = Color(0xFFF85149)
private val OrangeWarn   = Color(0xFFDB6D28)
private val PurpleSyn    = Color(0xFFBC8CFF)
private val YellowWarn   = Color(0xFFE3B341)
private val TextPrimary  = Color(0xFFCDD9E5)
private val TextSecondary= Color(0xFF636E7B)
private val TextMuted    = Color(0xFF3D444D)
private val BorderColor  = Color(0xFF2D333B)

/* Estado del análisis */
data class AnalysisState(
    val done:         Boolean     = false,
    val success:      Boolean     = false,
    val errores:      List<Token> = emptyList(),
    val advertencias: List<Token> = emptyList()
)

/* Pantalla principal */
@Composable
fun EditScreen(
    navController: NavController? = null,
    viewModel: EditViewModel = viewModel()
) {
    var editorText    by viewModel::editText
    var analysisState by remember { mutableStateOf(AnalysisState()) }
    var showReport    by remember { mutableStateOf(false) }
    val context       = LocalContext.current
    val editorScroll  = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            EditorTopBar(
                analysisState = analysisState,
                onHome    = { navController?.navigate("home") },
                onPreview = {
                    when {
                        !analysisState.done -> Toast.makeText(
                            context, "Analiza el formulario primero", Toast.LENGTH_SHORT
                        ).show()
                        !analysisState.success -> Toast.makeText(
                            context, "Corrige los errores antes de continuar", Toast.LENGTH_SHORT
                        ).show()
                        else -> navController?.navigate("preview")
                    }
                }
            )

            FileStatusBar(lineCount = editorText.text.count { it == '\n' } + 1)

            CodeEditor(
                text         = editorText,
                onTextChange = { editorText = it },
                scrollState  = editorScroll,
                modifier     = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            BottomPanel(
                analysisState = analysisState,
                onAnalyze     = {
                    analysisState = runAnalysis(editorText.text)
                    if (analysisState.success)
                        Toast.makeText(context, "✓ Sin errores", Toast.LENGTH_SHORT).show()
                },
                onShowReport  = { showReport = true },
                onPreview     = {
                    if (analysisState.success)
                        navController?.navigate("preview")
                    else
                        Toast.makeText(
                            context,
                            if (!analysisState.done) "Analiza primero"
                            else "Corrige los errores",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            )
        }

        if (showReport) {
            ErrorReportDialog(
                errores      = analysisState.errores,
                advertencias = analysisState.advertencias,
                onDismiss    = { showReport = false }
            )
        }
    }
}


@Composable
private fun EditorTopBar(
    analysisState: AnalysisState,
    onHome:    () -> Unit,
    onPreview: () -> Unit
) {
    val statusColor = when {
        !analysisState.done       -> TextMuted
        analysisState.success     -> GreenOk
        else                      -> RedErr
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSurface)
            .drawBehind {
                drawLine(
                    color       = CyanDim,
                    start       = Offset(0f, size.height),
                    end         = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Logo
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(GreenOk))
                    Box(Modifier.size(10.dp).clip(CircleShape).background(YellowWarn))
                    Box(Modifier.size(10.dp).clip(CircleShape).background(RedErr))
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        text          = "PKM_FORMS",
                        color         = CyanGlow,
                        fontSize      = 13.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        fontFamily    = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text       = "editor.form",
                        color      = TextSecondary,
                        fontSize   = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Text(
                        text = when {
                            !analysisState.done   -> "sin analizar"
                            analysisState.success -> "ok"
                            else -> "${analysisState.errores.size} error(es)"
                        },
                        color      = statusColor,
                        fontSize   = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (analysisState.success)
                                CyanGlow.copy(alpha = 0.15f)
                            else BgCardAlt
                        )
                        .border(
                            1.dp,
                            if (analysisState.success) CyanDim else BorderColor,
                            RoundedCornerShape(5.dp)
                        )
                        .clickable(onClick = onPreview)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text       = "▶  Vista Previa",
                        color      = if (analysisState.success) CyanGlow else TextSecondary,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(BgCardAlt)
                        .border(1.dp, BorderColor, RoundedCornerShape(5.dp))
                        .clickable(onClick = onHome)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text       = "⌂  Home",
                        color      = TextSecondary,
                        fontSize   = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}


@Composable
private fun FileStatusBar(lineCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgCard)
            .padding(horizontal = 14.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = "PKM_FORMS  ·  lenguaje de formularios",
            color      = TextSecondary,
            fontSize   = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text       = "Ln $lineCount",
            color      = TextSecondary,
            fontSize   = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}


@Composable
private fun CodeEditor(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val lineHeightPx = with(density) { 24.sp.toPx() }

    val codeStyle = TextStyle(
        fontFamily    = FontFamily.Monospace,
        fontSize      = 14.sp,
        lineHeight    = 24.sp,
        color         = TextPrimary,
        letterSpacing = 0.3.sp
    )

    val numberStyle = codeStyle.copy(
        color     = TextMuted,
        fontSize  = 12.sp,
        lineHeight = 24.sp
    )

    val lineCount = remember(text.text) {
        max(1, text.text.count { it == '\n' } + if (text.text.endsWith('\n')) 0 else 1)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .background(BgEditor)
    ) {
        Row(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {

            Column(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .background(BgDeep)
                    .padding(top = 12.dp, bottom = 12.dp)
            ) {
                repeat(lineCount) { index ->
                    Box(
                        modifier = Modifier
                            .height(with(density) { 24.sp.toDp() })
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text      = "${index + 1}",
                            style     = numberStyle,
                            maxLines  = 1,
                            modifier  = Modifier.padding(end = 12.dp)
                        )
                    }
                }
            }

            // Separador
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(CyanDim.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                if (text.text.isEmpty()) {
                    Text(
                        text  = "$ Escribe aquí...\n\nSECTION [\n    width: 400,\n    height: 600,\n    elements: {\n        ...\n    }\n]",
                        style = codeStyle.copy(color = TextMuted),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }

                BasicTextField(
                    value         = text,
                    onValueChange = onTextChange,
                    textStyle     = codeStyle,
                    cursorBrush   = SolidColor(CyanGlow),
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }
        }
    }
}
/* Panel inferior */
@Composable
private fun BottomPanel(
    analysisState: AnalysisState,
    onAnalyze:     () -> Unit,
    onShowReport:  () -> Unit,
    onPreview:     () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSurface)
            .drawBehind {
                drawLine(
                    color       = CyanDim,
                    start       = Offset(0f, 0f),
                    end         = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {

        AnimatedVisibility(
            visible = analysisState.done,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            AnalysisResultBadge(
                state        = analysisState,
                onShowReport = onShowReport,
                modifier     = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(listOf(CyanDim, Color(0xFF1D4A6E)))
                    )
                    .border(1.dp, CyanGlow.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .clickable(onClick = onAnalyze),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "▷  Analizar",
                    color      = CyanGlow,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            AnimatedVisibility(visible = analysisState.done && !analysisState.success) {
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(RedErr.copy(alpha = 0.12f))
                        .border(1.dp, RedErr.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable(onClick = onShowReport)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "⚠ ${analysisState.errores.size}",
                        color      = RedErr,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            AnimatedVisibility(visible = analysisState.success) {
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(GreenOk.copy(alpha = 0.12f))
                        .border(1.dp, GreenOk.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable(onClick = onPreview)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "▶ Preview",
                        color      = GreenOk,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}


@Composable
private fun AnalysisResultBadge(
    state:        AnalysisState,
    onShowReport: () -> Unit,
    modifier:     Modifier = Modifier
) {
    val accent = when {
        state.success              -> GreenOk
        state.errores.isNotEmpty() -> RedErr
        else                       -> YellowWarn
    }
    val icon = when {
        state.success              -> "✓"
        state.errores.isNotEmpty() -> "✗"
        else                       -> "⚠"
    }
    val msg = when {
        state.success              -> "Análisis completado — formulario válido"
        state.errores.isNotEmpty() -> "${state.errores.size} error(es)  ·  ${state.advertencias.size} advertencia(s)"
        else                       -> "${state.advertencias.size} advertencia(s)"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text       = icon,
                color      = accent,
                fontSize   = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text       = msg,
                color      = TextPrimary,
                fontSize   = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        if (!state.success && (state.errores + state.advertencias).isNotEmpty()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(accent.copy(alpha = 0.15f))
                    .clickable(onClick = onShowReport)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text       = "ver reporte →",
                    color      = accent,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/* Dialog reporte de errores */
@Composable
fun ErrorReportDialog(
    errores:      List<Token>,
    advertencias: List<Token>,
    onDismiss:    () -> Unit
) {
    val allItems   = errores + advertencias
    var filterTipo by remember { mutableStateOf("Todos") }

    val filtrados = when (filterTipo) {
        "Léxico"      -> allItems.filter { it.type.contains("Léx",  ignoreCase = true) }
        "Sintáctico"  -> allItems.filter { it.type.contains("Sint", ignoreCase = true) }
        "Semántico"   -> allItems.filter { it.type.contains("Sem",  ignoreCase = true) }
        "Advertencia" -> allItems.filter { it.type.contains("Adv",  ignoreCase = true) }
        else          -> allItems
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside   = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BgCard)
                .border(1.dp, CyanDim, RoundedCornerShape(10.dp))
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text          = "REPORTE DE ANÁLISIS",
                        color         = CyanGlow,
                        fontSize      = 13.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        fontFamily    = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (errores.isNotEmpty())
                            MiniChip("${errores.size} errores", RedErr)
                        if (advertencias.isNotEmpty())
                            MiniChip("${advertencias.size} advertencias", YellowWarn)
                        if (errores.isEmpty() && advertencias.isEmpty())
                            MiniChip("sin problemas", GreenOk)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BgCardAlt)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text("×", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = CyanDim.copy(alpha = 0.3f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Todos", "Léxico", "Sintáctico", "Semántico", "Advertencia").forEach { f ->
                    val sel = f == filterTipo
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (sel) CyanGlow.copy(alpha = 0.15f) else BgCardAlt)
                            .border(
                                1.dp,
                                if (sel) CyanGlow.copy(alpha = 0.5f) else BorderColor,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { filterTipo = f }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text          = f,
                            color         = if (sel) CyanGlow else TextSecondary,
                            fontSize      = 11.sp,
                            fontWeight    = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                            fontFamily    = FontFamily.Monospace
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDeep)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                ReportHeaderCell("#",            0.17f)
                ReportHeaderCell("LEXEMA",       0.15f)
                ReportHeaderCell("LÍNEA",        0.19f)
                ReportHeaderCell("COLUMNA",      0.10f)
                ReportHeaderCell("TIPO",         0.17f)
                ReportHeaderCell("DESCRIPCIÓN",  1f)
            }

            HorizontalDivider(color = BorderColor)

            if (filtrados.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "— sin resultados —",
                        color      = TextSecondary,
                        fontSize   = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(filtrados) { index, token ->
                        ErrorRowItem(token, index)
                        if (index < filtrados.lastIndex)
                            HorizontalDivider(
                                color     = BorderColor.copy(alpha = 0.4f),
                                thickness = 0.5.dp
                            )
                    }
                }
            }

            HorizontalDivider(color = BorderColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "${filtrados.size} elemento(s) mostrado(s)",
                    color      = TextSecondary,
                    fontSize   = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(BgCardAlt)
                        .border(1.dp, BorderColor, RoundedCornerShape(5.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text       = "cerrar",
                        color      = TextPrimary,
                        fontSize   = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}


/* Fila de error */

@Composable
private fun ErrorRowItem(token: Token, index: Int) {
    val (tipoColor, tipoLabel) = when {
        token.type.contains("Léx",  ignoreCase = true) -> PurpleSyn  to "léxico"
        token.type.contains("Sint", ignoreCase = true) -> OrangeWarn to "sintáctico"
        token.type.contains("Sem",  ignoreCase = true) -> RedErr     to "semántico"
        token.type.contains("Adv",  ignoreCase = true) -> YellowWarn to "advertencia"
        else                                            -> TextSecondary to token.type
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (index % 2 == 0) BgCard else BgEditor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${index + 1}", color = TextMuted,    fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.07f))
        Text(token.lexema,   color = CyanGlow,     fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(0.15f))
        Text("${token.line}", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.09f))
        Text("${token.column}", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.10f))

        Box(modifier = Modifier.weight(0.17f).padding(end = 4.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(tipoColor.copy(alpha = 0.14f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(tipoLabel, color = tipoColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
            }
        }

        Text(
            text       = token.description,
            color      = TextPrimary,
            fontSize   = 10.sp,
            maxLines   = 2,
            overflow   = TextOverflow.Ellipsis,
            fontFamily = FontFamily.Monospace,
            lineHeight = 14.sp,
            modifier   = Modifier.weight(0.42f)
        )
    }
}

/* Helpers */
@Composable
private fun MiniChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun RowScope.ReportHeaderCell(text: String, weight: Float) {
    Text(
        text          = text,
        color         = TextSecondary,
        fontSize      = 9.sp,
        fontWeight    = FontWeight.ExtraBold,
        fontFamily    = FontFamily.Monospace,
        letterSpacing = 0.8.sp,
        modifier      = Modifier.weight(weight)
    )
}

/* Lógica de análisis */
private fun runAnalysis(codigo: String): AnalysisState {
    return try {
        Lexer.listaError.clear()
        Parser.listaErrores.clear()
        Parser.semantico.reset()
        ResultParser.reset()

        val lexer  = Lexer(StringReader(codigo))
        val parser = Parser(lexer)
        parser.parse()

        val lexErrors  = Lexer.listaError.map {
            Token(it.lexema, it.line, it.column, "Léxico", it.description)
        }
        val sintErrors = Parser.listaErrores.toList()
        val semErrors  = Parser.semantico.getErrores()
            .filter { !it.type.contains("Adv", ignoreCase = true) }
        val warnings   = Parser.semantico.getErrores()
            .filter { it.type.contains("Adv", ignoreCase = true) }

        val allErrors = lexErrors + sintErrors + semErrors

        AnalysisState(
            done         = true,
            success      = allErrors.isEmpty(),
            errores      = allErrors,
            advertencias = warnings
        )
    } catch (e: Exception) {
        AnalysisState(
            done    = true,
            success = false,
            errores = listOf(Token("fatal", 0, 0, "Sintáctico", e.message ?: "Error fatal"))
        )
    }
}
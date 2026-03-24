package com.example.proyecto1_compi1.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
import com.example.proyecto1_compi1.ui.utils.CodeTemplate
import com.example.proyecto1_compi1.ui.utils.CodeTemplates
import com.example.proyecto1_compi1.ui.utils.ColorPickerDialog
import com.example.proyecto1_compi1.ui.utils.SyntaxColor
import com.example.proyecto1_compi1.ui.utils.SyntaxHighlighter
import java.io.StringReader


private val BgDeep = Color(0xFF090C10)
private val BgEditor = Color(0xFF0D1117)
private val BgSurface = Color(0xFF161B22)
private val BgCard = Color(0xFF1C2128)
private val BgCardAlt = Color(0xFF21262D)
private val CyanGlow = Color(0xFF39D0D8)
private val CyanDim = Color(0xFF1A6E73)
private val GreenOk = Color(0xFF3FB950)
private val RedErr = Color(0xFFF85149)
private val OrangeWarn = Color(0xFFDB6D28)
private val PurpleSyn = Color(0xFFBC8CFF)
private val YellowWarn = Color(0xFFE3B341)
private val TextPrimary = Color(0xFFCDD9E5)
private val TextSecondary = Color(0xFF636E7B)
private val TextMuted = Color(0xFF3D444D)
private val BorderColor = Color(0xFF2D333B)

/* Estado del análisis */
data class AnalysisState(
    val done: Boolean = false,
    val success: Boolean = false,
    val errores: List<Token> = emptyList(),
    val advertencias: List<Token> = emptyList()
)

/* Pantalla principal */
@Composable
fun EditScreen(
    navController: NavController? = null,
    viewModel: EditViewModel = viewModel()
) {
    val editorText = viewModel.editText
    val onTextChange: (TextFieldValue) -> Unit = { newText ->
        viewModel.updateText(newText)
    }

    var analysisState by remember { mutableStateOf(AnalysisState()) }
    var showReport by remember { mutableStateOf(false) }
    var showTemplates by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val editorScroll = rememberScrollState()

    val onClear = {
        viewModel.updateText(TextFieldValue(""))
        analysisState = AnalysisState()
        Toast.makeText(context, "Editor limpiado", Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            EditorTopBar(
                analysisState = analysisState,
                onHome = { navController?.navigate("home") }
            )

            FileStatusBar(lineCount = editorText.text.count { it == '\n' } + 1)

            CodeEditor(
                text = editorText,
                onTextChange = onTextChange,
                scrollState = editorScroll,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            BottomPanel(
                analysisState = analysisState,
                onAnalyze = {
                    analysisState = runAnalysis(editorText.text)
                    if (analysisState.success)
                        Toast.makeText(context, "✓ Sin errores", Toast.LENGTH_SHORT).show()
                },
                onShowReport = { showReport = true },
                onPreview = {
                    if (analysisState.success)
                        navController?.navigate("preview")
                    else
                        Toast.makeText(
                            context,
                            if (!analysisState.done) "Analiza primero"
                            else "Corrige los errores",
                            Toast.LENGTH_SHORT
                        ).show()
                },
                onClear = onClear,
                onTemplates = { showTemplates = true },
                onColorPicker = { showColorPicker = true }
            )
        }

        if (showReport) {
            ErrorReportDialog(
                errores = analysisState.errores,
                advertencias = analysisState.advertencias,
                onDismiss = { showReport = false }
            )
        }

        if (showTemplates) {
            TemplatesDialog(
                onDismiss = { showTemplates = false },
                onSelectTemplate = { template ->
                    val currentText = editorText.text
                    val cursorPosition = editorText.selection.start

                    val newText = currentText.substring(0, cursorPosition) +
                            template.code +
                            currentText.substring(cursorPosition)

                    val newCursorPosition = cursorPosition + template.code.length

                    viewModel.updateText(
                        TextFieldValue(
                            text = newText,
                            selection = androidx.compose.ui.text.TextRange(newCursorPosition)
                        )
                    )
                    showTemplates = false
                    Toast.makeText(context, "Plantilla insertada: ${template.name}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        if (showColorPicker) {
            ColorPickerDialog(
                onDismiss = { showColorPicker = false },
                onColorSelected = { colorCode ->
                    val currentText = editorText.text
                    val cursorPosition = editorText.selection.start

                    val newText = currentText.substring(0, cursorPosition) +
                            colorCode +
                            currentText.substring(cursorPosition)

                    val newCursorPosition = cursorPosition + colorCode.length

                    viewModel.updateText(
                        TextFieldValue(
                            text = newText,
                            selection = androidx.compose.ui.text.TextRange(newCursorPosition)
                        )
                    )
                    showColorPicker = false
                    Toast.makeText(context, "Color insertado: $colorCode", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}


@Composable
fun TemplatesDialog(
    onDismiss: () -> Unit,
    onSelectTemplate: (CodeTemplate) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }

    val categories = listOf("Todas", "Básicas", "Preguntas", "Estructuras", "Lógica")

    val filteredTemplates = remember(searchQuery, selectedCategory) {
        CodeTemplates.templates.filter { template ->
            val matchesSearch = searchQuery.isEmpty() ||
                    template.name.contains(searchQuery, ignoreCase = true) ||
                    template.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategory) {
                "Básicas" -> template.name.contains("Básica") || template.name.contains("Simple")
                "Preguntas" -> template.name.contains("Pregunta")
                "Estructuras" -> template.name.contains("Sección") || template.name.contains("Tabla")
                "Lógica" -> template.name.contains("FOR") || template.name.contains("IF")
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(12.dp))
                .background(BgCard)
                .border(1.dp, CyanDim, RoundedCornerShape(12.dp))
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PLANTILLAS DE CÓDIGO",
                        color = CyanGlow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Selecciona una plantilla para insertar en el editor",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BgCardAlt)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = TextSecondary, fontSize = 16.sp)
                }
            }

            HorizontalDivider(color = BorderColor)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .padding(12.dp)
            ) {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar plantillas...", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderColor,
                        cursorColor = CyanGlow
                    ),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanGlow.copy(alpha = 0.2f),
                                selectedLabelColor = CyanGlow
                            )
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderColor)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTemplates) { template ->
                    TemplateCard(
                        template = template,
                        onClick = { onSelectTemplate(template) }
                    )
                }

                if (filteredTemplates.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No se encontraron plantillas",
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = BorderColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun TemplateCard(
    template: CodeTemplate,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = BgEditor
        ),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(template.icon, fontSize = 20.sp)
                    Column {
                        Text(
                            text = template.name,
                            color = CyanGlow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = template.description,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Text(
                        text = if (expanded) "▲" else "▼",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = BorderColor)
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgDeep)
                            .clip(RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = template.code.take(200) +
                                    if (template.code.length > 200) "..." else "",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanDim,
                            contentColor = CyanGlow
                        )
                    ) {
                        Text("Insertar plantilla", fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}


@Composable
private fun EditorTopBar(
    analysisState: AnalysisState,
    onHome: () -> Unit
) {
    val statusColor = when {
        !analysisState.done -> TextMuted
        analysisState.success -> GreenOk
        else -> RedErr
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSurface)
            .drawBehind {
                drawLine(
                    color = CyanDim,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(GreenOk))
                    Box(Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(YellowWarn))
                    Box(Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(RedErr))
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        text = "PKM_FORMS",
                        color = CyanGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "editor.form",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Indicador de estado
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
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
                            !analysisState.done -> "sin analizar"
                            analysisState.success -> "ok"
                            else -> "${analysisState.errores.size} error(es)"
                        },
                        color = statusColor,
                        fontSize = 10.sp,
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
                        text = "⌂  Home",
                        color = TextSecondary,
                        fontSize = 11.sp,
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PKM_FORMS  ·  lenguaje de formularios",
            color = TextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Ln $lineCount",
            color = TextSecondary,
            fontSize = 10.sp,
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
    val syntaxHighlighter = remember { SyntaxHighlighter() }


    val syntaxTransformation = remember(syntaxHighlighter) {
        object : androidx.compose.ui.text.input.VisualTransformation {
            override fun filter(
                text: AnnotatedString
            ): androidx.compose.ui.text.input.TransformedText {
                val highlighted = syntaxHighlighter.highlight(text.text)
                return androidx.compose.ui.text.input.TransformedText(
                    highlighted,

                    androidx.compose.ui.text.input.OffsetMapping.Identity
                )
            }
        }
    }

    val codeStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp,
        color = SyntaxColor.DEFAULT
    )

    val numberStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        color = TextMuted,
        fontSize = 12.sp,
        lineHeight = 24.sp
    )

    val lineCount = remember(text.text) {
        maxOf(1, text.text.count { it == '\n' } + 1)
    }

    val lineHeightDp = with(LocalDensity.current) { 24.sp.toDp() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .background(BgEditor)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .width(52.dp)
                    .fillMaxHeight()
                    .background(BgDeep)
                    .verticalScroll(scrollState)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                repeat(lineCount) { idx ->
                    Box(
                        modifier = Modifier
                            .height(lineHeightDp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "${idx + 1}",
                            style = numberStyle,
                            maxLines = 1,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(CyanDim.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = codeStyle,
                    cursorBrush = SolidColor(CyanGlow),
                    visualTransformation = syntaxTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    decorationBox = { innerTextField ->
                        if (text.text.isEmpty()) {
                            Text(
                                text = "$ Escribe tu formulario aquí...",
                                style = codeStyle.copy(color = TextMuted),
                                modifier = Modifier.padding(0.dp)
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
    }
}

/* Panel inferior */
@Composable
private fun BottomPanel(
    analysisState: AnalysisState,
    onAnalyze: () -> Unit,
    onShowReport: () -> Unit,
    onPreview: () -> Unit,
    onClear: () -> Unit,
    onTemplates: () -> Unit,
    onColorPicker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSurface)
            .drawBehind {
                drawLine(
                    color = CyanDim,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {

        // Badge de resultados
        AnimatedVisibility(
            visible = analysisState.done,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            AnalysisResultBadge(
                state = analysisState,
                onShowReport = onShowReport,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(listOf(CyanDim, Color(0xFF1D4A6E)))
                    )
                    .border(1.dp, CyanGlow.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .clickable(onClick = onAnalyze),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Analizar",
                    color = CyanGlow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BgCardAlt)
                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                    .clickable(onClick = onClear),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Limpiar",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BgCardAlt)
                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                    .clickable(onClick = onTemplates),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Plantillas",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Botón Color
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BgCardAlt)
                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                    .clickable(onClick = onColorPicker),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Color",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        AnimatedVisibility(
            visible = analysisState.done,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = !analysisState.success && analysisState.errores.isNotEmpty()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(RedErr.copy(alpha = 0.12f))
                            .border(1.dp, RedErr.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .clickable(onClick = onShowReport),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚠  Ver errores (${analysisState.errores.size})",
                            color = RedErr,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                AnimatedVisibility(visible = analysisState.success) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GreenOk.copy(alpha = 0.12f))
                            .border(1.dp, GreenOk.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .clickable(onClick = onPreview),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▶  Vista Previa",
                            color = GreenOk,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisResultBadge(
    state: AnalysisState,
    onShowReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = when {
        state.success -> GreenOk
        state.errores.isNotEmpty() -> RedErr
        else -> YellowWarn
    }
    val icon = when {
        state.success -> "✓"
        state.errores.isNotEmpty() -> "✗"
        else -> "⚠"
    }
    val msg = when {
        state.success -> "Análisis completado — formulario válido"
        state.errores.isNotEmpty() -> "${state.errores.size} error(es)  ·  ${state.advertencias.size} advertencia(s)"
        else -> "${state.advertencias.size} advertencia(s)"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = icon,
                color = accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = msg,
                color = TextPrimary,
                fontSize = 12.sp,
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
                    text = "ver reporte →",
                    color = accent,
                    fontSize = 10.sp,
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
    errores: List<Token>,
    advertencias: List<Token>,
    onDismiss: () -> Unit
) {
    val allItems = errores + advertencias
    var filterTipo by remember { mutableStateOf("Todos") }

    val filtrados = when (filterTipo) {
        "Léxico" -> allItems.filter { it.type.contains("Léx", ignoreCase = true) }
        "Sintáctico" -> allItems.filter { it.type.contains("Sint", ignoreCase = true) }
        "Semántico" -> allItems.filter { it.type.contains("Sem", ignoreCase = true) }
        "Advertencia" -> allItems.filter { it.type.contains("Adv", ignoreCase = true) }
        else -> allItems
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "REPORTE DE ANÁLISIS",
                        color = CyanGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
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

            // Filtros
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
                            text = f,
                            color = if (sel) CyanGlow else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
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
                ReportHeaderCell("#", 0.06f)
                ReportHeaderCell("LEXEMA", 0.12f)
                ReportHeaderCell("LÍNEA", 0.07f)
                ReportHeaderCell("COLUMNA", 0.08f)
                ReportHeaderCell("TIPO", 0.12f)
                ReportHeaderCell("DESCRIPCIÓN", 0.55f)
            }

            HorizontalDivider(color = BorderColor)

            if (filtrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "— sin resultados —",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(filtrados) { index, token ->
                        ErrorRowItemMejorado(token, index)
                        if (index < filtrados.lastIndex)
                            HorizontalDivider(
                                color = BorderColor.copy(alpha = 0.4f),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filtrados.size} / ${allItems.size} elemento(s) mostrado(s)",
                    color = TextSecondary,
                    fontSize = 11.sp,
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
                        text = "cerrar",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorRowItemMejorado(token: Token, index: Int) {
    var expanded by remember { mutableStateOf(false) }

    val (tipoColor, tipoLabel) = when {
        token.type.contains("Léx", ignoreCase = true) -> PurpleSyn to "léxico"
        token.type.contains("Sint", ignoreCase = true) -> OrangeWarn to "sintáctico"
        token.type.contains("Sem", ignoreCase = true) -> RedErr to "semántico"
        token.type.contains("Adv", ignoreCase = true) -> YellowWarn to "advertencia"
        else -> TextSecondary to token.type.lowercase()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (index % 2 == 0) BgCard else BgEditor)
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "${index + 1}",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(32.dp)
            )

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(tipoColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    token.lexema.ifEmpty { "[EOF]" },
                    color = tipoColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                "${token.line}",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(48.dp)
            )

            Text(
                "${token.column}",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(56.dp)
            )

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(tipoColor.copy(alpha = 0.14f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    tipoLabel,
                    color = tipoColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = token.description.take(80) + if (token.description.length > 80) "..." else "",
                color = TextPrimary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (expanded) "▲" else "▼",
                color = TextSecondary,
                fontSize = 10.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDeep.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "📋 DESCRIPCIÓN COMPLETA:",
                    color = CyanGlow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDeep)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, tipoColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = token.description,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
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
        Text(
            label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun RowScope.ReportHeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 9.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 0.8.sp,
        modifier = Modifier.weight(weight)
    )
}

/* Lógica de análisis */
private fun runAnalysis(codigo: String): AnalysisState {
    return try {
        Lexer.listaError.clear()
        Parser.listaErrores.clear()
        Parser.semantico.reset()
        ResultParser.reset()

        val lexer = Lexer(StringReader(codigo))
        val parser = Parser(lexer)
        parser.parse()

        val lexErrors = Lexer.listaError.map {
            Token(it.lexema, it.line, it.column, "Léxico", it.description)
        }
        val sintErrors = Parser.listaErrores.toList()
        val semErrors = Parser.semantico.getErrores()
            .filter { !it.type.contains("Adv", ignoreCase = true) }
        val warnings = Parser.semantico.getErrores()
            .filter { it.type.contains("Adv", ignoreCase = true) }

        val allErrors = lexErrors + sintErrors + semErrors

        AnalysisState(
            done = true,
            success = allErrors.isEmpty(),
            errores = allErrors,
            advertencias = warnings
        )
    } catch (e: Exception) {
        AnalysisState(
            done = true,
            success = false,
            errores = listOf(Token("fatal", 0, 0, "Sintáctico", e.message ?: "Error fatal"))
        )
    }
}
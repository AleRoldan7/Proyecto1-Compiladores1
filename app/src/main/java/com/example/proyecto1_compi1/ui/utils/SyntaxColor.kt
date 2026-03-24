package com.example.proyecto1_compi1.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

object SyntaxColor {
    val OPERATORS = Color(0xFF3FB950)
    val VARIABLES = Color(0xFFCDD9E5)
    val STRINGS   = Color(0xFFE3B341)
    val NUMBERS   = Color(0xFF39D0D8)
    val KEYWORDS  = Color(0xFFBC8CFF)
    val BRACKETS  = Color(0xFF6CB6FF)
    val EMOJIS    = Color(0xFFFFD166)
    val COMMENTS  = Color(0xFF6A737D)
    val DEFAULT   = Color(0xFFCDD9E5)
    val STYLES    = Color(0xFFFF9B50)   // naranja para "color", "text size", etc.
    val HEXCOLOR  = Color(0xFF79C0FF)   // azul para #RRGGBB
}

class SyntaxHighlighter {

    private val keywords = setOf(
        "SECTION", "TABLE", "TEXT",
        "OPEN_QUESTION", "DROP_QUESTION", "SELECT_QUESTION", "MULTIPLE_QUESTION",
        "number", "string", "special", "draw",
        "IF", "ELSE", "WHILE", "DO", "FOR", "in",
        "who_is_that_pokemon",
        "VERTICAL", "HORIZONTAL",
        "SOLID", "DOUBLE", "DOTTED", "LINE",
        "MONO", "SANS_SERIF", "CURSIVE",
        "RED", "BLUE", "GREEN", "PURPLE", "SKY", "YELLOW", "BLACK", "WHITE",
        "width", "height", "pointX", "pointY", "orientation",
        "elements", "styles", "label", "content",
        "options", "correct", "NUMBER"
    )

    private val styleKeywords = setOf(
        "color", "background color", "text size", "font family", "border"
    )

    fun highlight(code: String): AnnotatedString = buildAnnotatedString {
        var i = 0
        val len = code.length

        while (i < len) {
            val c = code[i]

            when {
                // ── Comentario de línea $
                c == '$' -> {
                    val start = i
                    while (i < len && code[i] != '\n') i++
                    withStyle(SpanStyle(color = SyntaxColor.COMMENTS)) {
                        append(code.substring(start, i))
                    }
                }

                // ── Comentario de bloque /* ... */
                code.startsWith("/*", i) -> {
                    val start = i; i += 2
                    while (i < len - 1 && !(code[i] == '*' && code[i+1] == '/')) i++
                    if (i < len - 1) i += 2
                    withStyle(SpanStyle(color = SyntaxColor.COMMENTS)) {
                        append(code.substring(start, i))
                    }
                }

                // ── String literal "..."
                c == '"' -> {
                    val start = i; i++
                    while (i < len && code[i] != '"' && code[i] != '\n') {
                        if (code[i] == '\\' && i + 1 < len) i++
                        i++
                    }
                    if (i < len && code[i] == '"') i++
                    val str = code.substring(start, i)

                    // Detectar si es una clave de estilo: "color", "text size", etc.
                    val inner = str.removeSurrounding("\"")
                    val color = when {
                        styleKeywords.any { inner == it } -> SyntaxColor.STYLES
                        else -> SyntaxColor.STRINGS
                    }
                    withStyle(SpanStyle(color = color)) { append(str) }
                }

                // ── Emoji @[:...]
                c == '@' && i + 1 < len && code[i+1] == '[' -> {
                    val start = i
                    while (i < len && code[i] != ']') i++
                    if (i < len) i++
                    withStyle(SpanStyle(color = SyntaxColor.EMOJIS)) {
                        append(code.substring(start, i))
                    }
                }

                // ── Color hex #RRGGBB
                c == '#' && i + 1 < len && isHexChar(code[i+1]) -> {
                    val start = i; i++
                    while (i < len && isHexChar(code[i])) i++
                    withStyle(SpanStyle(color = SyntaxColor.HEXCOLOR)) {
                        append(code.substring(start, i))
                    }
                }

                // ── Número (int o decimal)
                c.isDigit() || (c == '.' && i + 1 < len && code[i+1].isDigit()) -> {
                    val start = i
                    while (i < len && (code[i].isDigit() || code[i] == '.')) i++
                    withStyle(SpanStyle(color = SyntaxColor.NUMBERS)) {
                        append(code.substring(start, i))
                    }
                }

                // ── Identificador o keyword
                c.isLetter() || c == '_' -> {
                    val start = i
                    while (i < len && (code[i].isLetterOrDigit() || code[i] == '_')) i++
                    val word = code.substring(start, i)
                    val col = if (keywords.contains(word)) SyntaxColor.KEYWORDS
                    else SyntaxColor.VARIABLES
                    withStyle(SpanStyle(color = col)) { append(word) }
                }

                // ── Operadores dobles ==, !=, >=, <=, &&, ||
                code.startsWith("==", i) || code.startsWith("!=", i) ||
                        code.startsWith(">=", i) || code.startsWith("<=", i) ||
                        code.startsWith("&&", i) || code.startsWith("||", i) -> {
                    withStyle(SpanStyle(color = SyntaxColor.OPERATORS)) {
                        append(code.substring(i, i + 2))
                    }
                    i += 2
                }

                // ── Operadores simples
                c == '+' || c == '-' || c == '*' || c == '/' ||
                        c == '^' || c == '%' || c == '=' || c == '>' ||
                        c == '<' || c == '~' || c == '.' -> {
                    withStyle(SpanStyle(color = SyntaxColor.OPERATORS)) { append(c) }
                    i++
                }

                // ── Paréntesis, llaves, corchetes, puntuación
                c == '{' || c == '}' || c == '[' || c == ']' ||
                        c == '(' || c == ')' || c == ':' || c == ',' || c == ';' -> {
                    withStyle(SpanStyle(color = SyntaxColor.BRACKETS)) { append(c) }
                    i++
                }

                // ── Resto (espacios, saltos de línea, etc.)
                else -> {
                    withStyle(SpanStyle(color = SyntaxColor.DEFAULT)) { append(c) }
                    i++
                }
            }
        }
    }

    private fun isHexChar(c: Char) = c.isDigit() || c in 'a'..'f' || c in 'A'..'F'
}
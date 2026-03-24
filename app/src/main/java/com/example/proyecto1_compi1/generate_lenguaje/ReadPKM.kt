package com.example.proyecto1_compi1.generate_lenguaje

import android.content.Context
import android.util.Log
import com.example.proyecto1_compi1.analizador.pkm.LexerPKM
import com.example.proyecto1_compi1.analizador.pkm.ParserPKM
import com.example.proyecto1_compi1.analizador.pkm.sym
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import java_cup.runtime.Symbol
import java.io.File
import java.io.FileReader
import java.io.StringReader

class ReadPKM(private val context: Context) {

    fun loadPKM(file: File): List<Any> {
        val elements = mutableListOf<Any>()
        val fileContent = file.readText()

        Log.d("PKMLOADER", "=== INICIANDO CARGA DEL ARCHIVO: ${file.name} ===")
        Log.d("PKMLOADER", "Tamaño del archivo: ${file.length()} bytes")

        val tokens = analyzeLexerDetailed(fileContent)

        if (tokens.isEmpty()) {
            Log.e("PKMLOADER", "No se generaron tokens")
            return elements
        }

        if (tokens.any { it.sym == sym.error }) {
            Log.e("PKMLOADER", "Tokens de error detectados")
            return elements
        }

        Log.d("PKMLOADER", "--- INICIANDO PARSEO ---")

        runCatching {

            // ✅ FIX 1: inicializar ResultParser ANTES de parsear
            ResultParser.reset()
            Log.d("PKMLOADER", "ResultParser.reset() OK - currentForm: ${ResultParser.currentForm}")

            val lexer = LexerPKM(StringReader(fileContent))
            val parser = ParserPKM(lexer)

            parser.parse()

            Log.d("PKMLOADER", "Parser completado")

            val form = ResultParser.currentForm
            if (form == null) {
                Log.e("PKMLOADER", "currentForm es null después del parse")
                return@runCatching
            }

            Log.d("PKMLOADER", "currentForm tiene ${form.getElements().size} elementos")

            form.getElements().forEach { item ->
                elements.add(item)
                Log.d("PKMLOADER", "Elemento cargado: ${item.javaClass.simpleName}")
            }

        }.onFailure { e ->
            Log.e("PKMLOADER", "Error durante parseo: ${e.message}", e)
            logDetailedError(e, fileContent, tokens)
        }

        Log.d("PKMLOADER", "Total elementos retornados: ${elements.size}")
        return elements
    }


    private fun analyzeLexerDetailed(content: String): List<TokenInfo> {
        Log.d("PKMLOADER", "=== ANÁLISIS LÉXICO DETALLADO ===")

        val tokens = mutableListOf<TokenInfo>()

        try {
            val lexer = LexerPKM(StringReader(content))
            var token = lexer.next_token()
            var tokenCount = 0
            var errorCount = 0

            Log.d(
                "PKMLOADER", String.format(
                    "%-6s %-20s %-30s %-10s %-10s %s",
                    "#", "TIPO", "VALOR", "LÍNEA", "COLUMNA", "DESCRIPCIÓN"
                )
            )
            Log.d("PKMLOADER", "-".repeat(100))

            while (token.sym != sym.EOF) {
                tokenCount++

                val tokenType = getTokenTypeName(token.sym)
                val tokenValue = token.value?.toString() ?: "null"
                val line = token.left + 1
                val column = token.right + 1
                val description = getTokenDescription(token.sym, token.value)

                // Guardar información del token
                tokens.add(
                    TokenInfo(
                        sym = token.sym,
                        type = tokenType,
                        value = token.value,
                        line = line,
                        column = column,
                        description = description
                    )
                )

                val isError = token.sym == sym.error
                if (isError) errorCount++

                val errorMarker = if (isError) " ⚠️ ERROR" else ""

                Log.d(
                    "PKMLOADER", String.format(
                        "%-6d %-20s %-30s %-10d %-10d %s%s",
                        tokenCount,
                        tokenType,
                        if (tokenValue.length > 27) tokenValue.take(24) + "..." else tokenValue,
                        line,
                        column,
                        description,
                        errorMarker
                    )
                )

                token = lexer.next_token()
            }

            Log.d("PKMLOADER", "-".repeat(100))
            Log.d("PKMLOADER", "RESUMEN LÉXICO:")
            Log.d("PKMLOADER", "  - Total tokens: $tokenCount")
            Log.d("PKMLOADER", "  - Tokens de error: $errorCount")
            Log.d("PKMLOADER", "  - Tokens válidos: ${tokenCount - errorCount}")

            showTokenSequence(tokens)

        } catch (e: Exception) {
            Log.e("PKMLOADER", "Error en análisis léxico: ${e.message}")
            e.printStackTrace()
        }

        return tokens
    }

    /**
     * Muestra la secuencia de tokens de forma resumida
     */
    private fun showTokenSequence(tokens: List<TokenInfo>) {
        Log.d("PKMLOADER", "--- SECUENCIA DE TOKENS ---")

        val sequence = tokens.joinToString(" ") { token ->
            when (token.sym) {
                sym.METADATA_BLOCK -> "###"
                sym.SECTION_OPEN -> "<section"
                sym.SECTION_CLOSE -> "</section>"
                sym.CONTENT_OPEN -> "<content>"
                sym.CONTENT_CLOSE -> "</content>"
                sym.OPEN_TAG -> "<open="
                sym.OPEN_CLOSE -> "/open>"
                sym.SELECT_TAG -> "<select="
                sym.SELECT_CLOSE -> "/select>"
                sym.MULTIPLE_TAG -> "<multiple="
                sym.MULTIPLE_CLOSE -> "/multiple>"
                sym.DROP_TAG -> "<drop="
                sym.DROP_CLOSE -> "/drop>"
                sym.SLASH_CLOSE -> "/>"
                sym.COMMA -> ","
                sym.LBRACE -> "{"
                sym.RBRACE -> "}"
                sym.MAYOR_QUE -> ">"
                sym.GUION -> "-"
                sym.COLON -> ":"
                sym.TEXT_ELEMENT -> "<text="

                sym.STRING -> "\"${token.value}\""
                sym.NUMBER -> token.value.toString()
                sym.ID -> token.value.toString()
                else -> "[${token.type}]"
            }
        }

        sequence.chunked(100).forEachIndexed { index, chunk ->
            Log.d("PKMLOADER", "Secuencia ${index + 1}: $chunk")
        }
    }


    private fun getTokenTypeName(tokenSym: Int): String {
        return when (tokenSym) {
            sym.METADATA_BLOCK -> "HASHES"
            /* Tokens de estilo - DESCOMENTADOS */
            sym.STYLE_OPEN -> "STYLE_OPEN"
            sym.STYLE_CLOSE -> "STYLE_CLOSE"
            sym.STYLE_COLOR -> "STYLE_COLOR"
            sym.STYLE_BG -> "STYLE_BG"
            sym.STYLE_FONT -> "STYLE_FONT"
            sym.STYLE_TEXT -> "STYLE_TEXT"
            sym.STYLE_BORDER -> "STYLE_BORDER"
            sym.FAMILY_EQ -> "FAMILY_EQ"
            sym.SIZE_EQ -> "SIZE_EQ"
            sym.COLOR_EQ -> "COLOR_EQ"

            /* Tokens de estructura */
            sym.SECTION_OPEN -> "SECTION_OPEN"
            sym.SECTION_CLOSE -> "SECTION_CLOSE"
            sym.CONTENT_OPEN -> "CONTENT_OPEN"
            sym.CONTENT_CLOSE -> "CONTENT_CLOSE"
            sym.TEXT_ELEMENT -> "TEXT_ELEMENT"
            sym.TEXT_CLOSE -> "TEXT_CLOSE"

            /* Tokens de preguntas */
            sym.OPEN_TAG -> "OPEN_TAG"
            sym.OPEN_CLOSE -> "OPEN_CLOSE"
            sym.SELECT_TAG -> "SELECT_TAG"
            sym.SELECT_CLOSE -> "SELECT_CLOSE"
            sym.MULTIPLE_TAG -> "MULTIPLE_TAG"
            sym.MULTIPLE_CLOSE -> "MULTIPLE_CLOSE"
            sym.DROP_TAG -> "DROP_TAG"
            sym.DROP_CLOSE -> "DROP_CLOSE"

            /* Tokens de tablas */
            sym.TABLE_OPEN -> "TABLE_OPEN"
            sym.TABLE_CLOSE -> "TABLE_CLOSE"
            sym.LINE_OPEN -> "LINE_OPEN"
            sym.LINE_CLOSE -> "LINE_CLOSE"
            sym.ELEMENT_OPEN -> "ELEMENT_OPEN"
            sym.ELEMENT_CLOSE -> "ELEMENT_CLOSE"

            /* Tokens de orientación y tipos */

            sym.MONO -> "MONO"
            sym.SANS_SERIF -> "SANS_SERIF"
            sym.CURSIVE -> "CURSIVE"
            sym.SOLID -> "SOLID"
            sym.DASHED -> "DASHED"
            sym.DOTTED -> "DOTTED"

            /* Tokens de colores */
            sym.COLOR_NAME -> "COLOR_NAME"
            sym.HEX_COLOR -> "HEX_COLOR"
            sym.RGB_COLOR -> "RGB_COLOR"
            sym.HSL_COLOR -> "HSL_COLOR"

            /* Tokens de símbolos */
            sym.SLASH_CLOSE -> "SLASH_CLOSE"
            sym.MAYOR_QUE -> "MAYOR_QUE"
            sym.MENOR_QUE -> "MENOR_QUE"
            sym.COMMA -> "COMMA"
            sym.COLON -> "COLON"
            sym.GUION -> "GUION"
            sym.LBRACE -> "LBRACE"
            sym.RBRACE -> "RBRACE"
            sym.IGUAL -> "IGUAL"

            /* Tokens de valores */
            sym.STRING -> "STRING"
            sym.NUMBER -> "NUMBER"
            sym.ID -> "ID"

            sym.error -> "ERROR"
            else -> "DESCONOCIDO($tokenSym)"
        }
    }

    private fun getTokenDescription(tokenSym: Int, value: Any?): String {
        return when (tokenSym) {
            sym.METADATA_BLOCK -> "Inicio/Fin de metadatos"

            /* Descripciones de estilo */
            sym.STYLE_OPEN -> "Inicio de bloque de estilo"
            sym.STYLE_CLOSE -> "Fin de bloque de estilo"
            sym.STYLE_COLOR -> "Definición de color"
            sym.STYLE_BG -> "Definición de fondo"
            sym.STYLE_FONT -> "Definición de fuente"
            sym.STYLE_TEXT -> "Definición de tamaño de texto"
            sym.STYLE_BORDER -> "Definición de borde"
            sym.FAMILY_EQ -> "Atributo family"
            sym.SIZE_EQ -> "Atributo size"
            sym.COLOR_EQ -> "Atributo color"

            /* Descripciones de estructura */
            sym.SECTION_OPEN -> "Apertura de sección"
            sym.SECTION_CLOSE -> "Cierre de sección"
            sym.CONTENT_OPEN -> "Apertura de contenido"
            sym.CONTENT_CLOSE -> "Cierre de contenido"
            sym.TEXT_ELEMENT -> "Elemento de texto"
            sym.TEXT_CLOSE -> "Cierre de elemento de texto"

            /* Descripciones de preguntas */
            sym.OPEN_TAG -> "Tag de pregunta abierta"
            sym.OPEN_CLOSE -> "Cierre de pregunta abierta"
            sym.SELECT_TAG -> "Tag de selección"
            sym.SELECT_CLOSE -> "Cierre de selección"
            sym.MULTIPLE_TAG -> "Tag de opción múltiple"
            sym.MULTIPLE_CLOSE -> "Cierre de opción múltiple"
            sym.DROP_TAG -> "Tag de lista desplegable"
            sym.DROP_CLOSE -> "Cierre de lista desplegable"

            /* Descripciones de tablas */
            sym.TABLE_OPEN -> "Apertura de tabla"
            sym.TABLE_CLOSE -> "Cierre de tabla"
            sym.LINE_OPEN -> "Apertura de línea"
            sym.LINE_CLOSE -> "Cierre de línea"
            sym.ELEMENT_OPEN -> "Apertura de elemento"
            sym.ELEMENT_CLOSE -> "Cierre de elemento"

            /* Orientación y tipos */

            sym.MONO -> "Fuente monoespaciada"
            sym.SANS_SERIF -> "Fuente sans-serif"
            sym.CURSIVE -> "Fuente cursiva"
            sym.SOLID -> "Borde sólido"
            sym.DASHED -> "Borde discontinuo"
            sym.DOTTED -> "Borde punteado"

            /* Colores */
            sym.COLOR_NAME -> "Nombre de color"
            sym.HEX_COLOR -> "Color hexadecimal"
            sym.RGB_COLOR -> "Color RGB"
            sym.HSL_COLOR -> "Color HSL"

            /* Símbolos */
            sym.SLASH_CLOSE -> "Cierre de tag"
            sym.MAYOR_QUE -> "Mayor que"
            sym.MENOR_QUE -> "Menor que"
            sym.COMMA -> "Separador"
            sym.COLON -> "Dos puntos"
            sym.GUION -> "Guión"
            sym.LBRACE -> "Inicio de lista"
            sym.RBRACE -> "Fin de lista"
            sym.IGUAL -> "Igual"

            /* Valores */
            sym.STRING -> "String literal"
            sym.NUMBER -> "Número"
            sym.ID -> "Identificador"

            sym.error -> "TOKEN DE ERROR"
            else -> ""
        }
    }

    private fun logDetailedError(ex: Throwable, content: String, tokens: List<TokenInfo> = emptyList()) {
        Log.e("PKMLOADER", "--- DETALLES DEL ERROR ---")
        Log.e("PKMLOADER", "Mensaje: ${ex.message}")

        // Mostrar últimos tokens antes del error
        if (tokens.isNotEmpty()) {
            val lastTokens = tokens.takeLast(10)
            Log.e("PKMLOADER", "Últimos 10 tokens antes del error:")
            lastTokens.forEachIndexed { index, token ->
                Log.e(
                    "PKMLOADER",
                    "  ${tokens.size - 10 + index + 1}: ${token.type} = '${token.value}' (línea ${token.line})"
                )
            }
        }

        // Intentar identificar la posición del error
        if (ex.message?.contains("line", ignoreCase = true) == true) {
            val lineMatch = Regex("line (\\d+)").find(ex.message ?: "")
            val columnMatch = Regex("column (\\d+)").find(ex.message ?: "")

            if (lineMatch != null && columnMatch != null) {
                val line = lineMatch.groupValues[1].toInt()
                val column = columnMatch.groupValues[1].toInt()

                val lines = content.lines()
                if (line <= lines.size) {
                    val errorLine = lines[line - 1]
                    Log.e("PKMLOADER", "Error en línea $line, columna $column:")
                    Log.e("PKMLOADER", "Línea: $errorLine")

                    // Marcar la posición del error
                    val marker = " ".repeat(column - 1) + "^"
                    Log.e("PKMLOADER", "       $marker")

                    // Mostrar contexto
                    val startLine = maxOf(0, line - 2)
                    val endLine = minOf(lines.size, line + 2)

                    Log.e("PKMLOADER", "Contexto:")
                    for (i in startLine until endLine) {
                        val prefix = if (i == line - 1) ">> " else "   "
                        Log.e("PKMLOADER", "$prefix${i + 1}: ${lines[i]}")
                    }
                }
            }
        }

        // Buscar caracteres problemáticos
        findProblematicChars(content)
    }

    private fun findProblematicChars(content: String) {
        Log.d("PKMLOADER", "--- BÚSQUEDA DE CARACTERES PROBLEMÁTICOS ---")

        val problematicChars = mutableMapOf<Char, Int>()

        content.forEachIndexed { index, char ->
            val code = char.code
            if (code < 32 && code != 9 && code != 10 && code != 13) {
                problematicChars[char] = problematicChars.getOrDefault(char, 0) + 1
                Log.w(
                    "PKMLOADER",
                    "Caracter de control en posición $index: '${char.toPrintable()}' (U+${
                        code.toString(16).padStart(4, '0')
                    })"
                )
            } else if (code > 127) {
                problematicChars[char] = problematicChars.getOrDefault(char, 0) + 1
                Log.w(
                    "PKMLOADER",
                    "Caracter no ASCII en posición $index: '${char}' (U+${code.toString(16).padStart(4, '0')})"
                )
            }
        }

        if (problematicChars.isNotEmpty()) {
            Log.e("PKMLOADER", "Caracteres problemáticos encontrados:")
            problematicChars.forEach { (char, count) ->
                Log.e(
                    "PKMLOADER",
                    "  '${char.toPrintable()}' (U+${char.code.toString(16).padStart(4, '0')}): $count ocurrencia(s)"
                )
            }
        } else {
            Log.d("PKMLOADER", "No se encontraron caracteres problemáticos")
        }
    }

    private fun Char.toPrintable(): String {
        return when (this) {
            '\n' -> "\\n"
            '\r' -> "\\r"
            '\t' -> "\\t"
            else -> this.toString()
        }
    }


    data class TokenInfo(
        val sym: Int,
        val type: String,
        val value: Any?,
        val line: Int,
        val column: Int,
        val description: String
    )
}

private fun showErrorLine(content: String, errorMsg: String?) {

    val regex = Regex("línea (\\d+), columna (\\d+)")
    val match = regex.find(errorMsg ?: "")

    if (match != null) {
        val line = match.groupValues[1].toInt()
        val column = match.groupValues[2].toInt()

        val lines = content.lines()

        if (line <= lines.size) {
            val errorLine = lines[line - 1]

            Log.e("PKMLOADER", " ERROR EN LÍNEA $line:")
            Log.e("PKMLOADER", errorLine)

            val pointer = " ".repeat(column - 1) + "^"
            Log.e("PKMLOADER", pointer)
        }
    }
}
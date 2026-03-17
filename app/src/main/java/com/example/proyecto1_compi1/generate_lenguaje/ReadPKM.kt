package com.example.proyecto1_compi1.generate_lenguaje

import android.content.Context
import android.util.Log
import com.example.proyecto1_compi1.analizador.pkm.LexerPKM
import com.example.proyecto1_compi1.analizador.pkm.ParserPKM
import com.example.proyecto1_compi1.analizador.pkm.sym
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
        Log.d("PKMLOADER", "Primeros 500 caracteres:\n${fileContent.take(500)}")


        val tokens = analyzeLexerDetailed(fileContent)


        if (tokens.isEmpty()) {
            Log.e("PKMLOADER", "No se generaron tokens")
            return elements
        }

        val hasErrorTokens = tokens.any { it.sym == sym.error }
        if (hasErrorTokens) {
            Log.e("PKMLOADER", "Tokens de error detectados")
            return elements
        }


        Log.d("PKMLOADER", "--- INICIANDO PARSEO ---")

        runCatching {
            val lexer = LexerPKM(StringReader(fileContent))
            val first = lexer.next_token()
            Log.d("DEBUGPKM", "PRIMER TOKEN: ${first.sym} VALUE: ${first.value}")
            val parser = ParserPKM(lexer)


            //Log.d("PKMLOADER", "Ejecutando parser.parse()")

            val result = parser.parse().value

            Log.d("PKMLOADER", "Parser completado con exito. Resultado tipo: ${result}")

            when (result) {
                is List<*> -> {
                    result.filterNotNull().forEach { item ->
                        elements.add(item)
                        Log.d("PKMLOADER", "Elemento agregado: ${item.javaClass.simpleName}")
                    }
                }
                else -> {
                    //Log.w("PKMLOADER", "Resultado no es lista: $result")
                }
            }

            //Log.d("PKMLOADER", "Cargados ${elements.size} elementos")
        }.onFailure { e ->
            Log.e("PKMLOADER", "Error durante parseo: ${e.message}", e)
            logDetailedError(e, fileContent, tokens)
        }

        return elements
    }

    /**
     * Analiza el lexer en detalle y muestra TODOS los tokens con información completa
     */
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

                // Marcar tokens de error
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

            // Mostrar secuencia de tokens para debugging
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

        // Dividir en líneas más pequeñas para mejor lectura
        sequence.chunked(100).forEachIndexed { index, chunk ->
            Log.d("PKMLOADER", "Secuencia ${index + 1}: $chunk")
        }
    }

    /**
     * Obtiene el nombre del tipo de token - CORREGIDO: usando when con sym directamente
     */
    private fun getTokenTypeName(tokenSym: Int): String {
        return when (tokenSym) {
            sym.METADATA_BLOCK -> "HASHES"
            //sym.STYLE_OPEN -> "STYLE_OPEN"
            //sym.STYLE_CLOSE -> "STYLE_CLOSE"
            sym.SECTION_OPEN -> "SECTION_OPEN"
            sym.SECTION_CLOSE -> "SECTION_CLOSE"
            sym.CONTENT_OPEN -> "CONTENT_OPEN"
            sym.CONTENT_CLOSE -> "CONTENT_CLOSE"
            sym.OPEN_TAG -> "OPEN_TAG"
            sym.OPEN_CLOSE -> "OPEN_CLOSE"
            sym.SELECT_TAG -> "SELECT_TAG"
            sym.SELECT_CLOSE -> "SELECT_CLOSE"
            sym.MULTIPLE_TAG -> "MULTIPLE_TAG"
            sym.MULTIPLE_CLOSE -> "MULTIPLE_CLOSE"
            sym.DROP_TAG -> "DROP_TAG"
            sym.DROP_CLOSE -> "DROP_CLOSE"
            sym.STRING -> "STRING"
            sym.NUMBER -> "NUMBER"
            sym.ID -> "ID"
            sym.SLASH_CLOSE -> "SLASH_CLOSE"
            sym.MAYOR_QUE -> "MAYOR_QUE"
            sym.COMMA -> "COMMA"
            sym.LBRACE -> "LBRACE"
            sym.RBRACE -> "RBRACE"
            //sym.LPAREN -> "LPAREN"
            //sym.RPAREN -> "RPAREN"
            sym.COLON -> "COLON"
            sym.GUION -> "GUION"
            sym.TEXT_ELEMENT -> "TEXT"
            //sym.AT -> "AT"

            sym.error -> "ERROR"
            else -> "DESCONOCIDO($tokenSym)"
        }
    }

    /**
     * Obtiene una descripción del token - CORREGIDO: usando when con sym directamente
     */
    private fun getTokenDescription(tokenSym: Int, value: Any?): String {
        return when (tokenSym) {
            sym.METADATA_BLOCK -> "Inicio/Fin de metadatos"
            sym.SECTION_OPEN -> "Apertura de sección"
            sym.SECTION_CLOSE -> "Cierre de sección"
            sym.CONTENT_OPEN -> "Apertura de contenido"
            sym.CONTENT_CLOSE -> "Cierre de contenido"
            sym.OPEN_TAG -> "Tag de pregunta abierta"
            sym.OPEN_CLOSE -> "Cierre de pregunta abierta"
            sym.SELECT_TAG -> "Tag de selección"
            sym.SELECT_CLOSE -> "Cierre de selección"
            sym.MULTIPLE_TAG -> "Tag de opción múltiple"
            sym.MULTIPLE_CLOSE -> "Cierre de opción múltiple"
            sym.DROP_TAG -> "Tag de lista desplegable"
            sym.DROP_CLOSE -> "Cierre de lista desplegable"
            sym.STRING -> "String literal"
            sym.NUMBER -> "Número"
            sym.ID -> "Identificador"
            sym.COMMA -> "Separador"
            sym.LBRACE -> "Inicio de lista"
            sym.RBRACE -> "Fin de lista"
            //sym.LPAREN -> "Paréntesis izquierdo"
            //sym.RPAREN -> "Paréntesis derecho"
            sym.COLON -> "Dos puntos"
            //sym.AT -> "Arroba"

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

    /**
     * Clase para almacenar información detallada de tokens
     */
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

            Log.e("PKMLOADER", "👉 ERROR EN LÍNEA $line:")
            Log.e("PKMLOADER", errorLine)

            val pointer = " ".repeat(column - 1) + "^"
            Log.e("PKMLOADER", pointer)
        }
    }
}
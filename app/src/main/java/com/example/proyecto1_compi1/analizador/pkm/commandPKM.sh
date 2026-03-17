#!/usr/bin/env bash

PROJECT_ROOT="$HOME/NetBeansProjects/Proyecto1Compi1"
RESOURCES_DIR="$PROJECT_ROOT/compilador_resources"

JFLEX_JAR="$RESOURCES_DIR/jflex-full-1.9.1.jar"
CUP_JAR="$RESOURCES_DIR/java-cup-11b.jar"

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

LEXER_FILE="$SCRIPT_DIR/LexerPKM.jflex"
CUP_FILE="$SCRIPT_DIR/ParserPKM.cup"

for file in "$JFLEX_JAR" "$CUP_JAR" "$LEXER_FILE" "$CUP_FILE"; do
    if [[ ! -f "$file" ]]; then
        echo "ERROR: No se encuentra el archivo:"
        echo "  → $file"
        exit 1
    fi
done

echo ""
echo "  → JFlex ...................................."
java -jar "$JFLEX_JAR" "$LEXER_FILE"
echo "Archivo creado"

echo ""
echo "  → CUP ......................................"
java -jar "$CUP_JAR" -parser ParserPKM -symbols sym "$CUP_FILE"

echo ""
echo "  Proceso terminado"
echo ""
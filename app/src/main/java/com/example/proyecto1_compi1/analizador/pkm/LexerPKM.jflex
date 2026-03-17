package com.example.proyecto1_compi1.analizador.pkm;

import java_cup.runtime.Symbol;

%%

%public
%class LexerPKM
%cup
%unicode
%line
%column
%ignorecase

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

DIGIT = [0-9]
NUMBER = {DIGIT}+
STRING = \"([^\"\\]|\\.)*\"
ID = [a-zA-Z_][a-zA-Z0-9_]*

%%

/* Bloque completo de metadatos (desde ### hasta ###) */
"###"[^#]*"###"       { return symbol(sym.METADATA_BLOCK, yytext()); }

/* Tags */
"<section="           { return symbol(sym.SECTION_OPEN); }
"</section>"          { return symbol(sym.SECTION_CLOSE); }
"<content>"           { return symbol(sym.CONTENT_OPEN); }
"</content>"          { return symbol(sym.CONTENT_CLOSE); }
"<text="              { return symbol(sym.TEXT_ELEMENT); }
"<open="              { return symbol(sym.OPEN_TAG); }
"</open>"             { return symbol(sym.OPEN_CLOSE); }
"<select="            { return symbol(sym.SELECT_TAG); }
"</select>"           { return symbol(sym.SELECT_CLOSE); }
"<multiple="          { return symbol(sym.MULTIPLE_TAG); }
"</multiple>"         { return symbol(sym.MULTIPLE_CLOSE); }
"<drop="              { return symbol(sym.DROP_TAG); }
"</drop>"             { return symbol(sym.DROP_CLOSE); }

/* Símbolos */
","                   { return symbol(sym.COMMA); }
":"                   { return symbol(sym.COLON); }
"-"                   { return symbol(sym.GUION); }
"{"                   { return symbol(sym.LBRACE); }
"}"                   { return symbol(sym.RBRACE); }
"="                   { return symbol(sym.IGUAL); }
">"                   { return symbol(sym.MAYOR_QUE); }
"/>"                  { return symbol(sym.SLASH_CLOSE); }

/* Valores */
{STRING}              { return symbol(sym.STRING, yytext().substring(1, yytext().length()-1)); }
{NUMBER}              { return symbol(sym.NUMBER, Integer.parseInt(yytext())); }
{ID}                  { return symbol(sym.ID, yytext()); }

/* Espacios */
[ \t\r\n]+            { /* ignorar */ }

/* Error */
.                     { return symbol(sym.error, yytext()); }
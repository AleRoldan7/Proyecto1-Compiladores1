package com.example.proyecto1_compi1.analizador.pkm;

import java_cup.runtime.Symbol;

%%

%public
%class LexerPKM
%cup
%unicode
%line
%column
/* ── QUITAMOS %ignorecase — causaba el problema con strings y Unicode ── */

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }

    private StringBuilder stringBuffer = new StringBuilder();
%}

DIGIT       = [0-9]
NUMBER      = {DIGIT}+
/* ── ID acepta letras, tildes y ñ para nombres en español ── */
ID          = [a-zA-ZáéíóúÁÉÍÓÚüÜñÑ_][a-zA-ZáéíóúÁÉÍÓÚüÜñÑ0-9_]*

/* ── Estados ── */
%state STRING_STATE

%%

/* ════════════════════════════════════════════
   ESTADO INICIAL
   ════════════════════════════════════════════ */
<YYINITIAL> {

    /* ── Metadatos ── */
    "###"[^#]*"###"       { return symbol(sym.METADATA_BLOCK, yytext()); }

    /* ── Tags de estructura ── */
    "<section="           { return symbol(sym.SECTION_OPEN);    }
    "</section>"          { return symbol(sym.SECTION_CLOSE);   }
    "<content>"           { return symbol(sym.CONTENT_OPEN);    }
    "</content>"          { return symbol(sym.CONTENT_CLOSE);   }
    "<text="              { return symbol(sym.TEXT_ELEMENT);     }

    /* ── Tags de preguntas ── */
    "<open="              { return symbol(sym.OPEN_TAG);        }
    "</open>"             { return symbol(sym.OPEN_CLOSE);      }
    "<select="            { return symbol(sym.SELECT_TAG);      }
    "</select>"           { return symbol(sym.SELECT_CLOSE);    }
    "<multiple="          { return symbol(sym.MULTIPLE_TAG);    }
    "</multiple>"         { return symbol(sym.MULTIPLE_CLOSE);  }
    "<drop="              { return symbol(sym.DROP_TAG);        }
    "</drop>"             { return symbol(sym.DROP_CLOSE);      }

    /* ── Cierre de tags ── */
    "/>"                  { return symbol(sym.SLASH_CLOSE);     }
    ">"                   { return symbol(sym.MAYOR_QUE);       }

    /* ── Símbolos ── */
    ","                   { return symbol(sym.COMMA);           }
    ":"                   { return symbol(sym.COLON);           }
    "-"                   { return symbol(sym.GUION);           }
    "{"                   { return symbol(sym.LBRACE);          }
    "}"                   { return symbol(sym.RBRACE);          }
    "="                   { return symbol(sym.IGUAL);           }

    /* ── Número negativo (-1 para correct sin respuesta) ── */
    "-"{NUMBER}           {
        return symbol(sym.NUMBER, Integer.parseInt(yytext()));
    }

    /* ── Número positivo ── */
    {NUMBER}              {
        return symbol(sym.NUMBER, Integer.parseInt(yytext()));
    }

    /* ── Inicio de string — entra al estado STRING_STATE ── */
    \"                    {
        stringBuffer.setLength(0);
        yybegin(STRING_STATE);
    }

    /* ── Identificadores (VERTICAL, HORIZONTAL, nombres) ── */
    {ID}                  { return symbol(sym.ID, yytext()); }

    /* ── Espacios y saltos de línea ── */
    [ \t\r\n]+            { /* ignorar */ }

    /* ── Cualquier otro carácter ── */
    .                     { /* ignorar silenciosamente */ }
}

/* ════════════════════════════════════════════
   ESTADO STRING — acumula hasta la comilla
   ════════════════════════════════════════════ */
<STRING_STATE> {

    /* Comilla de cierre → retorna el string acumulado */
    \"                    {
        yybegin(YYINITIAL);
        return symbol(sym.STRING, stringBuffer.toString());
    }

    /* Secuencia de escape \" dentro del string */
    "\\\""                {
        stringBuffer.append('"');
    }

    /* Cualquier otro escape \x */
    "\\" .                {
        stringBuffer.append(yytext());
    }

    /* Contenido normal — acepta espacios, tildes, ¿, !, etc. */
    [^\"\\\n\r]+          {
        stringBuffer.append(yytext());
    }

    /* Salto de línea — cierra el string forzadamente */
    \n | \r | \r\n        {
        yybegin(YYINITIAL);
        return symbol(sym.STRING, stringBuffer.toString());
    }
}
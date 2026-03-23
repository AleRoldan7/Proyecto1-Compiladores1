package com.example.proyecto1_compi1.analizador.pkm;

import java_cup.runtime.Symbol;

%%

%public
%class LexerPKM
%cup
%unicode
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
    private StringBuilder stringBuffer = new StringBuilder();
%}

DIGIT    = [0-9]
ID       = [a-zA-ZáéíóúÁÉÍÓÚüÜñÑ_][a-zA-ZáéíóúÁÉÍÓÚüÜñÑ0-9_]*
HEXCOLOR = \#[0-9a-fA-F]{6}

%state STRING_STATE

%%


<YYINITIAL> {

    "###"[^#]*"###"     { return symbol(sym.METADATA_BLOCK, yytext()); }

        /* ── Comentarios — SOLO al inicio de línea o con espacio antes ── */
        /* El # de hex color siempre viene después de = o espacio dentro de tag */
        "$"[^\r\n]*         { /* comentario $ */ }
        [ \t\r\n]+          { /* ignorar */ }

        /* ── Hex ANTES que cualquier otra regla con < o # ── */
        {HEXCOLOR}          { return symbol(sym.HEX_COLOR, yytext()); }

    "</section>"        { return symbol(sym.SECTION_CLOSE);  }
    "</content>"        { return symbol(sym.CONTENT_CLOSE);  }
    "</table>"          { return symbol(sym.TABLE_CLOSE);    }
    "</line>"           { return symbol(sym.LINE_CLOSE);     }
    "</element>"        { return symbol(sym.ELEMENT_CLOSE);  }
    "</style>"          { return symbol(sym.STYLE_CLOSE);    }
    "</text>"           { return symbol(sym.TEXT_CLOSE);     }
    "</open>"           { return symbol(sym.OPEN_CLOSE);     }
    "</select>"         { return symbol(sym.SELECT_CLOSE);   }
    "</multiple>"       { return symbol(sym.MULTIPLE_CLOSE); }
    "</drop>"           { return symbol(sym.DROP_CLOSE);     }


    /* Estilo texto — PRIMERO porque "<text size=" es más largo que "<text=" */
    "<text size="       { return symbol(sym.STYLE_TEXT);     }

    /* Apertura de texto — SEGUNDO */
    "<text="            { return symbol(sym.TEXT_ELEMENT);   }

    /* Sección */
    "<section="         { return symbol(sym.SECTION_OPEN);   }

    /* Tabla */
    "<table="           { return symbol(sym.TABLE_OPEN);     }

    /* Preguntas */
    "<open="            { return symbol(sym.OPEN_TAG);       }
    "<select="          { return symbol(sym.SELECT_TAG);     }
    "<multiple="        { return symbol(sym.MULTIPLE_TAG);   }
    "<drop="            { return symbol(sym.DROP_TAG);       }

    /* Estilos con = */
    "<color="           { return symbol(sym.STYLE_COLOR);    }
    "<background"       { return symbol(sym.STYLE_BG);       }
    "<font"             { return symbol(sym.STYLE_FONT);     }
    "<border,"          { return symbol(sym.STYLE_BORDER);   }

    /* ── Aperturas sin = ── */
    "<content>"         { return symbol(sym.CONTENT_OPEN);   }
    "<line>"            { return symbol(sym.LINE_OPEN);      }
    "<element>"         { return symbol(sym.ELEMENT_OPEN);   }
    "<style>"           { return symbol(sym.STYLE_OPEN);     }

    /* ── Atributos de estilo ── */
    "family="           { return symbol(sym.FAMILY_EQ);  }
    "size="             { return symbol(sym.SIZE_EQ);    }
    "color="            { return symbol(sym.COLOR_EQ);   }

    /* ══════════════════════════════════════════
       KEYWORDS — antes que {ID}
       VERTICAL y HORIZONTAL se tratan como ID
       para que el parser pueda usar ID:id
       ══════════════════════════════════════════ */
    "MONO"              { return symbol(sym.MONO);       }
    "SANS_SERIF"        { return symbol(sym.SANS_SERIF); }
    "CURSIVE"           { return symbol(sym.CURSIVE);    }
    "SOLID"             { return symbol(sym.SOLID);      }
    "DASHED"            { return symbol(sym.DASHED);     }
    "DOTTED"            { return symbol(sym.DOTTED);     }

    /* ── Colores base — como COLOR_NAME ── */
    "RED"               { return symbol(sym.COLOR_NAME, "RED");    }
    "BLUE"              { return symbol(sym.COLOR_NAME, "BLUE");   }
    "GREEN"             { return symbol(sym.COLOR_NAME, "GREEN");  }
    "PURPLE"            { return symbol(sym.COLOR_NAME, "PURPLE"); }
    "SKY"               { return symbol(sym.COLOR_NAME, "SKY");    }
    "YELLOW"            { return symbol(sym.COLOR_NAME, "YELLOW"); }
    "BLACK"             { return symbol(sym.COLOR_NAME, "BLACK");  }
    "WHITE"             { return symbol(sym.COLOR_NAME, "WHITE");  }


    /* ── RGB completo (10,10,10) — antes que "(" suelto ── */
    "("{DIGIT}+","{DIGIT}+","{DIGIT}+")"
                        { return symbol(sym.RGB_COLOR, yytext()); }

    /* ── HSL completo <10,0.5,0.5> — antes que "<" suelto ── */
    "<"{DIGIT}+("."{DIGIT}+)?","{DIGIT}+("."{DIGIT}+)?","{DIGIT}+("."{DIGIT}+)?">"
                        { return symbol(sym.HSL_COLOR, yytext()); }

    /* ── Número negativo — ANTES que "-" suelto ── */
    "-"{DIGIT}+         { return symbol(sym.NUMBER, Integer.parseInt(yytext())); }

    /* ── Número positivo ── */
    {DIGIT}+            { return symbol(sym.NUMBER, Integer.parseInt(yytext())); }

    /* ── Cierres de tag ── */
    "/>"                { return symbol(sym.SLASH_CLOSE); }
    ">"                 { return symbol(sym.MAYOR_QUE);   }

    /* ── Símbolos ── */
    ","                 { return symbol(sym.COMMA);  }
    ":"                 { return symbol(sym.COLON);  }
    "-"                 { return symbol(sym.GUION);  }
    "{"                 { return symbol(sym.LBRACE); }
    "}"                 { return symbol(sym.RBRACE); }
    "="                 { return symbol(sym.IGUAL);  }

    /* ── String ── */
    \"                  { stringBuffer.setLength(0); yybegin(STRING_STATE); }

    /* ── ID — captura VERTICAL, HORIZONTAL y cualquier nombre ── */
    {ID}                { return symbol(sym.ID, yytext()); }

    /* ── Ignorar el resto ── */
    .                   { /* ignorar caracteres desconocidos */ }
}


<STRING_STATE> {

    \"  {
        yybegin(YYINITIAL);
        return symbol(sym.STRING, stringBuffer.toString());
    }

    /* ── Emojis — igual que antes ── */
    "@[:smile:]"    { stringBuffer.append("😀"); }
    "@[:sad:]"      { stringBuffer.append("🥲"); }
    "@[:serious:]"  { stringBuffer.append("😐"); }
    "@[:heart:]"    { stringBuffer.append("❤️"); }
    "@[:cat:]"      { stringBuffer.append("😺"); }
    "@[:^^:]"       { stringBuffer.append("😺"); }
    "@[:star:]"     { stringBuffer.append("⭐"); }

    "@[:star:" {DIGIT}+ ":]"  {
        String txt = yytext();
        int n = Integer.parseInt(txt.substring(txt.indexOf("star:")+5, txt.lastIndexOf(":")));
        for (int i = 0; i < n; i++) stringBuffer.append("⭐");
    }
    "@[:star-" {DIGIT}+ "]"  {
        String txt = yytext();
        int n = Integer.parseInt(txt.substring(txt.indexOf("star-")+5, txt.lastIndexOf("]")));
        for (int i = 0; i < n; i++) stringBuffer.append("⭐");
    }
    "@[" "<"+ "3"+ "]"    { stringBuffer.append("❤️"); }
    "@[:" ")"+ "]"        { stringBuffer.append("😀"); }
    "@[:" "("+ "]"        { stringBuffer.append("🥲"); }
    "@[:" "|"+ "]"        { stringBuffer.append("😐"); }
    "@"                    { stringBuffer.append('@'); }

    /* ── Escape ── */
    "\\\""  { stringBuffer.append('"'); }
    "\\" .  { stringBuffer.append(yytext()); }

    /* ── Salto de línea — cierra el string ── */
    \n | \r | \r\n  {
        yybegin(YYINITIAL);
        return symbol(sym.STRING, stringBuffer.toString());
    }


    [^\"\\\n\r@]    { stringBuffer.append(yytext()); }
}
package com.example.proyecto1_compi1.analizador.form;

import java_cup.runtime.Symbol;
import java.util.ArrayList;
import com.example.proyecto1_compi1.token.Token;

%%

%public
%unicode
%cup
%class Lexer
%line
%column
%char

%{
    public static ArrayList<Token> listaError = new ArrayList<>();
    private StringBuilder stringBuffer = new StringBuilder();

    private void errorLexer(String lexema) {
        listaError.add(new Token(
            lexema, yyline + 1, yycolumn + 1, lexema, "Error Léxico"
        ));
    }

    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1, yytext());
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

/* ── Definiciones ── */
DIGITO      = [0-9]
LETTER      = [a-zA-Z]
SEPARADORES = [ \n\r\t]+
ENTERO      = {DIGITO}+
DECIMAL     = {DIGITO}+ \. {DIGITO}+
ID          = {LETTER} ({LETTER} | {DIGITO} | "_")*
COMENTARIO  = "$" [^\r\n]*
HEXCOLOR    = \# [0-9a-fA-F]{6}

%state STRING_STATE
%state COMMENT_BLOCK

%%

/* ════════════════════════════════════════════
   ESTADO INICIAL
   ════════════════════════════════════════════ */
<YYINITIAL> {

    /* ── Comentarios ── */
    {COMENTARIO}            { /* ignorar */ }
    "/*"                    { yybegin(COMMENT_BLOCK); }

    /* ── Espacios ── */
    {SEPARADORES}           { /* ignorar */ }

    /*
     * ── STRINGS ──
     * La comilla SIEMPRE va al STRING_STATE.
     * Las claves de estilo como "color", "font family" etc.
     * se reconocen DENTRO del STRING_STATE como casos especiales
     * y retornan su token correspondiente.
     */
    \"                      {
        stringBuffer.setLength(0);
        yybegin(STRING_STATE);
    }

    /* ── Variables ── */
    "number"                { return symbol(sym.VARIABLENUMBER); }
    "string"                { return symbol(sym.VARIABLESTRING); }
    "special"               { return symbol(sym.SPECIAL);        }

    /* ── Preguntas ── */
    "OPEN_QUESTION"         { return symbol(sym.OPENQUESTION);     }
    "DROP_QUESTION"         { return symbol(sym.DROPQUESTION);     }
    "SELECT_QUESTION"       { return symbol(sym.SELECTQUESTION);   }
    "MULTIPLE_QUESTION"     { return symbol(sym.MULTIPLEQUESTION); }

    /* ── Elementos ── */
    "SECTION"               { return symbol(sym.SECTION);  }
    "TABLE"                 { return symbol(sym.TABLE);    }
    "TEXT"                  { return symbol(sym.TEXT);     }
    "draw"                  { return symbol(sym.DRAW);     }

    /* ── Atributos ── */
    "elements"              { return symbol(sym.ELEMENTS);    }
    "styles"                { return symbol(sym.STYLES);      }
    "label"                 { return symbol(sym.LABEL);       }
    "content"               { return symbol(sym.CONTENT);     }
    "width"                 { return symbol(sym.WIDTH);       }
    "height"                { return symbol(sym.HEIGHT);      }
    "pointX"                { return symbol(sym.POINTX);      }
    "pointY"                { return symbol(sym.POINTY);      }
    "options"               { return symbol(sym.OPTIONS);     }
    "correct"               { return symbol(sym.CORRECT);     }
    "orientation"           { return symbol(sym.ORIENTATION); }

    /* ── Estilos tipográficos ── */
    "SOLID"                 { return symbol(sym.SOLID);      }
    "DOUBLE"                { return symbol(sym.DOUBLE);     }
    "DOTTED"                { return symbol(sym.DOTTED);     }
    "LINE"                  { return symbol(sym.LINE);       }
    "MONO"                  { return symbol(sym.MONO);       }
    "SANS_SERIF"            { return symbol(sym.SANS_SERIF); }
    "CURSIVE"               { return symbol(sym.CURSIVE);    }

    /* ── Orientación ── */
    "VERTICAL"              { return symbol(sym.VERTICAL);   }
    "HORIZONTAL"            { return symbol(sym.HORIZONTAL); }

    /* ── Colores base ── */
    "RED"                   { return symbol(sym.RED);    }
    "BLUE"                  { return symbol(sym.BLUE);   }
    "GREEN"                 { return symbol(sym.GREEN);  }
    "PURPLE"                { return symbol(sym.PURPLE); }
    "SKY"                   { return symbol(sym.SKY);    }
    "YELLOW"                { return symbol(sym.YELLOW); }
    "BLACK"                 { return symbol(sym.BLACK);  }
    "WHITE"                 { return symbol(sym.WHITE);  }

    /* ── Bloques de código ── */
    "IF"                    { return symbol(sym.IF);    }
    "ELSE"                  { return symbol(sym.ELSE);  }
    "WHILE"                 { return symbol(sym.WHILE); }
    "DO"                    { return symbol(sym.DO);    }
    "FOR"                   { return symbol(sym.FOR);   }
    "in"                    { return symbol(sym.IN);    }

    /* ── PokeAPI ── */
    "who_is_that_pokemon"   { return symbol(sym.WHO_IS_THAT_POKEMON); }

    /* ── Hex color ── */
    {HEXCOLOR}              { return symbol(sym.HEXCOLOR, yytext()); }

    /* ── RGB completo como un solo token — va ANTES que PARENTESIS_ABRE ── */
    "(" {DIGITO}+ "," {DIGITO}+ "," {DIGITO}+ ")"
        {
            return symbol(sym.RGB_COLOR, yytext());
        }

    /* ── HSL completo como un solo token ── */
    "<" {DIGITO}+ ("." {DIGITO}+)? "," {DIGITO}+ ("." {DIGITO}+)? "," {DIGITO}+ ("." {DIGITO}+)? ">"
        {
            return symbol(sym.HSL_COLOR, yytext());
        }

    /* ── Operadores ── */
    "=="    { return symbol(sym.IGUALIGUAL); }
    "!!"    { return symbol(sym.DIFERENTE);  }
    ">="    { return symbol(sym.MAYORIGUAL); }
    "<="    { return symbol(sym.MENORIGUAL); }
    "&&"    { return symbol(sym.AND);        }
    "||"    { return symbol(sym.OR);         }
    "~"     { return symbol(sym.NOT);        }
    "="     { return symbol(sym.IGUAL);      }
    ">"     { return symbol(sym.MAYOR);      }
    "<"     { return symbol(sym.MENOR);      }
    "+"     { return symbol(sym.SUMA);       }
    "-"     { return symbol(sym.RESTA);      }
    "*"     { return symbol(sym.MULTI);      }
    "/"     { return symbol(sym.DIVISION);   }
    "^"     { return symbol(sym.POTENCIA);   }
    "%"     { return symbol(sym.MODULO);     }
    ".."    { return symbol(sym.RANGO);      }

    /* ── Delimitadores ── */
    "("     { return symbol(sym.PARENTESIS_ABRE);   }
    ")"     { return symbol(sym.PARENTESIS_CIERRA); }
    "["     { return symbol(sym.CORCHETE_ABRE);     }
    "]"     { return symbol(sym.CORCHETE_CIERRA);   }
    "{"     { return symbol(sym.LLAVE_ABRE);        }
    "}"     { return symbol(sym.LLAVE_CIERRA);      }
    ";"     { return symbol(sym.PUNTOCOMA);         }
    ","     { return symbol(sym.COMA);              }
    ":"     { return symbol(sym.DOSPUNTOS);         }
    "?"     { return symbol(sym.INTERROGACION);     }
    "."     { return symbol(sym.PUNTO);             }

    /* ── Números ── */
    {DECIMAL}   { return symbol(sym.DECIMAL, yytext()); }
    {ENTERO}    { return symbol(sym.ENTERO,  yytext()); }

    /* ── Identificadores ── */
    {ID}        { return symbol(sym.VARIABLE, yytext()); }

    /* ── Error ── */
    .           { errorLexer(yytext()); }
}

/* ════════════════════════════════════════════
   ESTADO STRING_STATE
   ════════════════════════════════════════════ */
<STRING_STATE> {

    /* ── Cierre del string ── */
    \"  {
        yybegin(YYINITIAL);
        String resultado = stringBuffer.toString();
        switch (resultado) {
            case "color":            return symbol(sym.STYLE_COLOR);
            case "background color": return symbol(sym.STYLE_BACKGROUND);
            case "font family":      return symbol(sym.STYLE_FONT);
            case "text size":        return symbol(sym.STYLE_SIZE);
            case "border":           return symbol(sym.STYLE_BORDER);
            default:
                return symbol(sym.CADENA, resultado);
        }
    }

    /* ── Emojis ── */
    "@[:smile:]"                { stringBuffer.append("😀"); }
    "@[:sad:]"                  { stringBuffer.append("🥲"); }
    "@[:serious:]"              { stringBuffer.append("😐"); }
    "@[:heart:]"                { stringBuffer.append("❤️"); }
    "@[:cat:]"                  { stringBuffer.append("😺"); }
    "@[:^^:]"                   { stringBuffer.append("😺"); }
    "@[:star:]"                 { stringBuffer.append("⭐"); }

    "@[:star:" {DIGITO}+ ":]"   {
        String txt   = yytext();
        int    start = txt.indexOf("star:") + 5;
        int    end   = txt.lastIndexOf(":");
        int    n     = Integer.parseInt(txt.substring(start, end));
        for (int i = 0; i < n; i++) stringBuffer.append("⭐");
    }

    "@[:star-" {DIGITO}+ "]"    {
        String txt   = yytext();
        int    start = txt.indexOf("star-") + 5;
        int    end   = txt.lastIndexOf("]");
        int    n     = Integer.parseInt(txt.substring(start, end));
        for (int i = 0; i < n; i++) stringBuffer.append("⭐");
    }

    "@[" "<"+ "3"+ "]"          { stringBuffer.append("❤️"); }
    "@[:" ")"+  "]"             { stringBuffer.append("😀"); }
    "@[:" "("+ "]"              { stringBuffer.append("🥲"); }
    "@[:" "|"+ "]"              { stringBuffer.append("😐"); }
    "@"                         { stringBuffer.append('@');  }

    /* ── Signos de interrogación españoles — se guardan como texto ── */
    /* ¿ y ? dentro de strings son texto normal, NO comodines          */
    "¿"                         { stringBuffer.append('¿'); }

    /*
     * CLAVE: el ? dentro de un string se guarda como el carácter
     * Unicode \u003F (representación interna), NO como "?"
     * Así contieneComodin("¿Cuántos años tienes\u003F") != true
     * porque buscamos exactamente "?" y no \u003F
     *
     * Usamos un marcador especial que no sea "?" para que
     * contieneComodin no lo detecte como comodín.
     */


    /* ── ¿ y ? dentro de strings — se guardan como marcador \uFFFE ── */
    "¿"   { stringBuffer.append('¿');    }
     "?"   { stringBuffer.append('\uFFFE'); }  /* ← marcador temporal, NO es "?" */

     /* ── Escape ── */
     "\\\""   { stringBuffer.append('"');  }
     "\\" .   { stringBuffer.append(yytext()); }

     /* ── Contenido normal — ahora excluye ? y ¿ explícitamente ── */
     [^\"\\\n\r@¿?]+   { stringBuffer.append(yytext()); }

    \n | \r | \r\n    {
        yybegin(YYINITIAL);
        String res = stringBuffer.toString().replace('\uFFFE', '?');
        return symbol(sym.CADENA, res);
    }
}
/* ════════════════════════════════════════════
   ESTADO COMMENT_BLOCK  /* ... */
   ════════════════════════════════════════════ */
<COMMENT_BLOCK> {
    "*/"        { yybegin(YYINITIAL); }
    [^*]+       { /* ignorar */ }
    "*"         { /* ignorar */ }
}
package com.example.proyecto1_compi1.analizador;


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

     private void errorLexer(String lexema) {

        listaError.add(new Token(lexema, yyline + 1, yycolumn + 1, yytext(), "Error Lexico"));

     }
     private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1, yytext());
     }

     private Symbol symbol(int type, Object value) {
          return new Symbol(type, yyline + 1, yycolumn + 1, value);
     }
%}


/*EXPRESIONES A UTILIZAR*/

DIGITO = [0-9]
LETTER = [a-zA-Z]
SEPARADORES = [ \n\r\t]+

ENTERO = {DIGITO}+
DECIMAL = {DIGITO}+\.{DIGITO}+

ID = {LETTER}({LETTER}|{DIGITO}|"_")*

COMENTARIO = "$"[^(\r\n)]*
CADENA = \"[^\"]*\"


%%

/*  IGNORAR  */

{SEPARADORES}   { }
{COMENTARIO}    { }

/*  VARIABLES  */

"number"     { return symbol(sym.VARIABLENUMBER); }
"string"     { return symbol(sym.VARIABLESTRING); }
"special"  { return symbol(sym.VARIABLESPECIAL); }


/*  FORMA DE CREAR LAS PREGUNTAS */

"OPEN_QUESTION"          { return symbol(sym.OPENQUESTION); }
"DROP_QUESTION"          { return symbol(sym.DROPQUESTION); }
"SELECT_QUESTION"        { return symbol(sym.SELECTQUESTION); }
"MULTIPLE_QUESTION"      { return symbol(sym.MULTIPLEQUESTION); }


/* PALABRAS RESERVADAS */

"SECTION"                { return symbol(sym.SECTION); }
"TABLE"                  { return symbol(sym.TABLE); }
"TEXT"                   { return symbol(sym.TEXT); }




/* OPERADORES  */

"=="            { return symbol(sym.IGUALIGUAL); }
"!!"            { return symbol(sym.DIFERENTE); }
">="            { return symbol(sym.MAYORIGUAL); }
"<="            { return symbol(sym.MENORIGUAL); }

"&&"            { return symbol(sym.AND); }
"||"            { return symbol(sym.OR); }
"~"             { return symbol(sym.NOT); }

"="             { return symbol(sym.IGUAL); }
">"             { return symbol(sym.MAYOR); }
"<"             { return symbol(sym.MENOR); }

"+"             { return symbol(sym.SUMA); }
"-"             { return symbol(sym.RESTA); }
"*"             { return symbol(sym.MULTI); }
"/"             { return symbol(sym.DIVISION); }
"^"             { return symbol(sym.POTENCIA); }
"%"             { return symbol(sym.MODULO); }

"("             { return symbol(sym.PARENTESIS_ABRE); }
")"             { return symbol(sym.PARENTESIS_CIERRA); }

"["             { return symbol(sym.CORCHETE_ABRE); }
"]"             { return symbol(sym.CORCHETE_CIERRA); }

";"             { return symbol(sym.PUNTOCOMA); }
","             { return symbol(sym.COMA); }
":"             { return symbol(sym.DOSPUNTOS); }
"{"             { return symbol(sym.LLAVE_ABRE); }
"}"             { return symbol(sym.LLAVE_CIERRA); }


/*  LITERALES  */

{DECIMAL}       { return symbol(sym.DECIMAL, yytext()); }
{ENTERO}        { return symbol(sym.ENTERO, yytext()); }



/* IDENTIFICADORES  */

{ID}            { return symbol(sym.VARIABLE, yytext()); }
{CADENA}        { return symbol(sym.CADENA, yytext()); }

/* ERRORES */

. { errorLexer(yytext()); }
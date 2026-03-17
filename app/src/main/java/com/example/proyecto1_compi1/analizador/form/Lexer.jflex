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

     private void errorLexer(String lexema) {

         listaError.add(
             new Token(
                 lexema,
                 yyline + 1,
                 yycolumn + 1,
                 lexema,
                 "Error Léxico"
             )
         );

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

/* COLORES */

HEXCOLOR = \#[0-9a-fA-F]{6}
RGB = \({DIGITO}+,{DIGITO}+,{DIGITO}+\)
HSL = \<{DIGITO}+\,{DECIMAL}|{DIGITO}+\,{DECIMAL}|{DIGITO}+\>

%%

/* PARA LOS ESTILOS*/

\"color\"                { return symbol(sym.STYLE_COLOR); }

\"background color\"     { return symbol(sym.STYLE_BACKGROUND); }

\"font family\"          { return symbol(sym.STYLE_FONT); }

\"text size\"            { return symbol(sym.STYLE_SIZE); }


/*  IGNORAR  */

{SEPARADORES}   { }
{COMENTARIO}    { }

/*  VARIABLES  */

"number"    { return new Symbol(sym.VARIABLENUMBER, yyline, yycolumn, yytext()); }
"string"    { return new Symbol(sym.VARIABLESTRING, yyline, yycolumn, yytext()); }



/*  FORMA DE CREAR LAS PREGUNTAS */

"OPEN_QUESTION"          { return symbol(sym.OPENQUESTION); }
"DROP_QUESTION"          { return symbol(sym.DROPQUESTION); }
"SELECT_QUESTION"        { return symbol(sym.SELECTQUESTION); }
"MULTIPLE_QUESTION"      { return symbol(sym.MULTIPLEQUESTION); }


/* PALABRAS RESERVADAS */

"SECTION"                { return symbol(sym.SECTION); }
"TABLE"                  { return symbol(sym.TABLE); }
"TEXT"                   { return symbol(sym.TEXT); }

"special"     { return symbol(sym.SPECIAL); }
 "draw"        { return symbol(sym.DRAW); }

 /* CONFIGURACIONES */

"elements"    { return symbol(sym.ELEMENTS); }
"styles"      { return symbol(sym.STYLES); }

"label"       { return symbol(sym.LABEL); }
"content"     { return symbol(sym.CONTENT); }
"width"       { return symbol(sym.WIDTH); }
"height"      { return symbol(sym.HEIGHT); }
"pointX"      { return symbol(sym.POINTX); }
"pointY"      { return symbol(sym.POINTY); }
"options"      { return symbol(sym.OPTIONS); }
"correct"      { return symbol(sym.CORRECT); }
"orientation"  { return symbol(sym.ORIENTATION); }




"SOLID"   { return symbol(sym.SOLID); }
"DASHED"  { return symbol(sym.DASHED); }
"DOTTED"  { return symbol(sym.DOTTED); }
"MONO"    { return symbol(sym.MONO); }
"SANS_SERIF" { return symbol(sym.SANS_SERIF); }
"CURSIVE" { return symbol(sym.CURSIVE); }

/* ORIENTACIONES */

"VERTICAL"     { return symbol(sym.VERTICAL); }
"HORIZONTAL"   { return symbol(sym.HORIZONTAL); }

/* COLORES BASE */

"RED"      { return symbol(sym.RED); }
"BLUE"     { return symbol(sym.BLUE); }
"GREEN"    { return symbol(sym.GREEN); }
"PURPLE"   { return symbol(sym.PURPLE); }
"SKY"      { return symbol(sym.SKY); }
"YELLOW"   { return symbol(sym.YELLOW); }
"BLACK"    { return symbol(sym.BLACK); }
"WHITE"    { return symbol(sym.WHITE); }

{HEXCOLOR} { return symbol(sym.HEXCOLOR, yytext()); }




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

"?"             { return new Symbol(sym.INTERROGACION, yyline+1, yycolumn+1, yytext()); }

"."             { return new Symbol(sym.PUNTO, yyline+1, yycolumn+1, yytext()); }

/*  LITERALES  */

{DECIMAL}       { return symbol(sym.DECIMAL, yytext()); }
{ENTERO}        { return symbol(sym.ENTERO, yytext()); }



/* IDENTIFICADORES  */

{ID}    { return new Symbol(sym.VARIABLE, yyline, yycolumn, yytext()); }
{CADENA}        { return symbol(sym.CADENA, yytext()); }

/* ERRORES */

. { errorLexer(yytext()); }
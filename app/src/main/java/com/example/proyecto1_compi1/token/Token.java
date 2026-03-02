package com.example.proyecto1_compi1.token;

public class Token {

    private String lexema;
    private int line;
    private int column;
    private String type;
    private String description;

    public Token() {
    }

    public Token(String lexema, int line, int column, String type, String description) {
        this.lexema = lexema;
        this.line = line;
        this.column = column;
        this.type = type;
        this.description = description;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Lexema: " + lexema + ", Linea: " + line +
                ", Columna: " + column + ", Tipo: " + type +
                ", Descripcion: " + description;
    }
}

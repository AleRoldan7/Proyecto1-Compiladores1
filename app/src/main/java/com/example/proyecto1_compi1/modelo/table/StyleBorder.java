package com.example.proyecto1_compi1.modelo.table;

import com.example.proyecto1_compi1.enums.TypeBorder;

public class StyleBorder implements Style{

    private Object grosor;
    private TypeBorder type;
    private String color;

    public StyleBorder(Object grosor, TypeBorder type, String color) {
        this.grosor = grosor;
        this.type = type;
        this.color = color;
    }

    @Override
    public void apply(TableModel table) {
        System.out.println("Applying border: " + grosor + " " + type + " " + color);
    }


    public TypeBorder getType() {
        return type;
    }

    public void setType(TypeBorder type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

package com.example.proyecto1_compi1.modelo.table;

public class StyleColor implements Style{

    private String color;

    public StyleColor(String color) {
        this.color = color;
    }

    @Override
    public void apply(TableModel tableModel) {
        System.out.println("Aplicar color: " + color);
    }

    public String getColor() {
        return color;
    }
}

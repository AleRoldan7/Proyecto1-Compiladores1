package com.example.proyecto1_compi1.modelo.table;

import java.util.List;

public class PropertyStyle implements PropertyTable{

    public List<Style> styles;

    public PropertyStyle(List<Style> styles) {
        this.styles = styles;
    }

    @Override
    public void apply(TableModel table) {
        for(Style style : styles) {
            //table.addStyle(style);
            style.apply(table);
        }
    }
}

package com.example.proyecto1_compi1.modelo.table;

public class PropertyHeigth implements PropertyTable{

    public Object value;

    public PropertyHeigth(Object value) {
        this.value = value;
    }

    @Override
    public void apply(TableModel table) {
        if(value instanceof Integer) {
            table.setHeight((Integer) value);
        }
    }
}

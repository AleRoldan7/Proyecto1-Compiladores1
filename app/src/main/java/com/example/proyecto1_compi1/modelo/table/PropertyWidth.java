package com.example.proyecto1_compi1.modelo.table;

public class PropertyWidth implements PropertyTable{

    public Object value;

    public PropertyWidth(Object value) {
        this.value = value;
    }

    @Override
    public void apply(TableModel tableModel) {
        if (value instanceof Integer) {
            tableModel.setWith((Integer) value);
        }
    }
}

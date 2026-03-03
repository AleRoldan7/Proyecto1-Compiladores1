package com.example.proyecto1_compi1.modelo.variable;

public class VariableModel {

    private String type;
    private String name;
    private Object value;

    public VariableModel(String type, String name, Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}

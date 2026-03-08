package com.example.proyecto1_compi1.modelo.question;

public class PropertyItem {

    public String key;
    public Object value;

    public PropertyItem(String key, Object value) {
        this.key = key.toLowerCase();
        this.value = value;
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }
}

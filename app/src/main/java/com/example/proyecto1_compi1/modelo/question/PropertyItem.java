package com.example.proyecto1_compi1.modelo.question;

public class PropertyItem {

    public String key;
    public Object value;

    public PropertyItem(String key, Object value) {
        this.key = key.toLowerCase();
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }
}

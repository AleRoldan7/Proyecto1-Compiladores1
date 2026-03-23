package com.example.proyecto1_compi1.modelo.question;

import java.util.ArrayList;

public abstract class QuestionModel {

    protected String label;
    protected int    width;
    protected int    height;
    protected ArrayList<Object> styles;

    public QuestionModel() {
        styles = new ArrayList<>();
    }

    public void addProperty(PropertyItem prop) {

        if (prop == null || prop.getKey() == null) return;

        switch (prop.getKey()) {

            case "label":
                if (prop.getValue() != null) {
                    label = prop.getValue().toString()
                            .replace("\"", "")
                            .trim();
                }
                break;

            case "width":
                width = toInt(prop.getValue());
                break;

            case "height":
                height = toInt(prop.getValue());
                break;

            case "styles":
                if (prop.getValue() instanceof ArrayList) {
                    styles = (ArrayList<Object>) prop.getValue();
                }
                break;
        }
    }

    /**
     * Convierte cualquier tipo numérico a int.
     * Cubre: Integer, Double, Float, Long, String numérico.
     */
    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double)  return ((Double) value).intValue();
        if (value instanceof Float)   return ((Float) value).intValue();
        if (value instanceof Long)    return ((Long) value).intValue();
        if (value instanceof Number)  return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return (int) Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public String getLabel()             { return label;  }
    public int    getWidth()             { return width;  }
    public int    getHeight()            { return height; }
    public ArrayList<Object> getStyles() { return styles; }
}
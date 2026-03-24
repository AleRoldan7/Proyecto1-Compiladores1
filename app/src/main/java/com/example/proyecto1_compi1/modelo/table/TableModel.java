package com.example.proyecto1_compi1.modelo.table;

import com.example.proyecto1_compi1.analizador.form.Parser;
import com.example.proyecto1_compi1.modelo.color_style.*;
import com.example.proyecto1_compi1.modelo.question.PropertyItem;

import java.util.ArrayList;

public class TableModel {

    private int width;
    private int height;
    private int pointX;
    private int pointY;

    private ArrayList<ArrayList<Object>> elements = new ArrayList<>();
    private ArrayList<Object> styles = new ArrayList<>();
    private Parser parser = new Parser();

    public TableModel(ArrayList<PropertyItem> props) {
        for (PropertyItem prop : props) {
            addProperty(prop);
        }
    }

    public void addProperty(PropertyItem prop){
        switch(prop.getKey()){

            case "width":
                width = toInt(prop.getValue());
                break;

            case "height":
                height = toInt(prop.getValue());
                break;

            case "pointX":
                pointX = toInt(prop.getValue());
                break;

            case "pointY":
                pointY = toInt(prop.getValue());
                break;

            case "elements":
                elements = (ArrayList<ArrayList<Object>>) prop.getValue();
                break;

            case "styles":
                if (prop.getValue() instanceof ArrayList) {
                    ArrayList<Object> rawStyles = (ArrayList<Object>) prop.getValue();
                    ArrayList<Object> evaluatedStyles = new ArrayList<>();

                    for (Object style : rawStyles) {
                        if (style instanceof RgbStyleWithWildcard) {
                            RgbColor color = ((RgbStyleWithWildcard) style).evaluar(parser.semantico);
                            evaluatedStyles.add(new ColorStyle(color));
                        } else if (style instanceof BackgroundStyleWithWildcard) {
                            RgbColor color = ((BackgroundStyleWithWildcard) style).evaluar(parser.semantico);
                            evaluatedStyles.add(new BackgroundStyle(color));
                        } else if (style instanceof HslStyleWithWildcard) {
                            HslColor color = ((HslStyleWithWildcard) style).evaluar(parser.semantico);
                            evaluatedStyles.add(new ColorStyle(color));
                        } else if (style instanceof HslBackgroundStyleWithWildcard) {
                            HslColor color = ((HslBackgroundStyleWithWildcard) style).evaluar(parser.semantico);
                            evaluatedStyles.add(new BackgroundStyle(color));
                        } else {
                            evaluatedStyles.add(style);
                        }
                    }

                    styles = evaluatedStyles;
                }
                break;
        }
    }

    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof Float) return ((Float) value).intValue();
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return (int) Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPointX() {
        return pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public ArrayList<Object> getStyles() {
        return styles;
    }

    public ArrayList<ArrayList<Object>> getElements() {
        return elements;
    }
}
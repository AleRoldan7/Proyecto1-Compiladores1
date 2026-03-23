package com.example.proyecto1_compi1.modelo.question;

import com.example.proyecto1_compi1.analizador.form.Parser;
import com.example.proyecto1_compi1.enums.Orientation;
import com.example.proyecto1_compi1.modelo.color_style.*;

import java.util.ArrayList;

public class SectionsModel {

    private int width;
    private int height;
    private int pointX;
    private int pointY;
    private Orientation orientation = Orientation.VERTICAL;
    private ArrayList<Object> elements = new ArrayList<>();
    private ArrayList<Object> styles = new ArrayList<>();
    private Parser parser = new Parser();

    public SectionsModel(ArrayList<PropertyItem> props) {
        if (props != null) {
            for (PropertyItem prop : props) {
                addProperty(prop);
            }
        }
    }

    public void addProperty(PropertyItem prop) {
        if (prop == null || prop.getKey() == null) return;

        switch (prop.getKey()) {

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

            case "orientation":
                if (prop.getValue() != null) {
                    try {
                        orientation = Orientation.valueOf(
                                prop.getValue().toString().trim().toUpperCase()
                        );
                    } catch (IllegalArgumentException e) {
                        System.out.println("[SectionsModel] Orientación inválida: '"
                                + prop.getValue() + "' → VERTICAL");
                        orientation = Orientation.VERTICAL;
                    }
                }
                break;

            case "elements":
                if (prop.getValue() instanceof ArrayList) {
                    elements = (ArrayList<Object>) prop.getValue();
                }
                break;

            // En SectionsModel.addProperty()
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

    public void addElement(Object element) {
        if (elements == null) elements = new ArrayList<>();
        elements.add(element);
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

    public Orientation getOrientation() {
        return orientation;
    }

    public ArrayList<Object> getElements() {
        return elements;
    }

    public ArrayList<Object> getStyles() {
        return styles;
    }
}
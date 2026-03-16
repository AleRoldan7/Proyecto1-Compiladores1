package com.example.proyecto1_compi1.modelo.question;

import com.example.proyecto1_compi1.enums.Orientation;

import java.util.ArrayList;

public class SectionsModel {

    private int width;
    private int height;
    private int pointX;
    private int pointY;
    private Orientation orientation = Orientation.VERTICAL;

    private ArrayList<Object> elements;
    private ArrayList<Object> styles;

    public SectionsModel(ArrayList<PropertyItem> props) {

        for (PropertyItem prop : props) {

            addProperty(prop);

        }

    }

    public void addProperty(PropertyItem prop) {

        switch (prop.getKey()) {

            case "width":
                width = Integer.parseInt(prop.getValue().toString());
                break;

            case "height":
                height = Integer.parseInt(prop.getValue().toString());
                break;

            case "pointX":
                pointX = Integer.parseInt(prop.getValue().toString());
                break;

            case "pointY":
                pointY = Integer.parseInt(prop.getValue().toString());
                break;

            case "orientation":
                orientation = Orientation.valueOf(prop.value.toString());
                break;

            case "elements":
                elements = (ArrayList<Object>) prop.getValue();
                break;

            case "styles":
                styles = (ArrayList<Object>) prop.getValue();
                break;
        }

    }

    public void addElement(Object element) {
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

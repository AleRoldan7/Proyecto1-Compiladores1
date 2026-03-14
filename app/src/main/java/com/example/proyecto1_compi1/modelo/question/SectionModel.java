package com.example.proyecto1_compi1.modelo.question;

import com.example.proyecto1_compi1.enums.Orientation;

import java.util.ArrayList;

public class SectionModel {

    private int width;
    private int height;
    private int pointX;
    private int pointY;
    private Orientation orientation;

    private ArrayList<Object> elements;
    private ArrayList<Object> styles;

    public SectionModel() {
        elements = new ArrayList<>();
        styles = new ArrayList<>();
    }

    public void addProperty(PropertyItem prop){

        switch(prop.getKey()){

            case "width":
                width = (int) prop.getValue();
                break;

            case "height":
                height = (int) prop.getValue();
                break;

            case "pointX":
                pointX = (int) prop.getValue();
                break;

            case "pointY":
                pointY = (int) prop.getValue();
                break;

            case "orientation":
                if (prop.getValue().equals("VERTICAL")) {
                    orientation = Orientation.VERTICAL;
                } else {
                    orientation = Orientation.HORIZONATAL;
                }
                break;

            case "elements":
                break;

            case "styles":
                styles = (ArrayList<Object>) prop.getValue();
                break;
        }

    }

    public void addElement(Object element){
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

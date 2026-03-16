package com.example.proyecto1_compi1.modelo.table;

import com.example.proyecto1_compi1.modelo.question.PropertyItem;

import java.util.ArrayList;
import java.util.List;

public class TableModel {

    private int width;
    private int height;
    private int pointX;
    private int pointY;

    private ArrayList<ArrayList<Object>> elements = new ArrayList<>();
    private ArrayList<Object> styles = new ArrayList<>();


    public TableModel(ArrayList<PropertyItem> props) {

        for (PropertyItem prop : props) {

            addProperty(prop);

        }

    }


    public void addProperty(PropertyItem prop){

        switch(prop.getKey()){

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

            case "elements":
                elements = (ArrayList<ArrayList<Object>>) prop.getValue();
                break;

            case "styles":
                styles = (ArrayList<Object>) prop.getValue();
                break;
        }

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

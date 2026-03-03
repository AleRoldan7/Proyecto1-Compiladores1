package com.example.proyecto1_compi1.modelo.table;

import java.util.ArrayList;
import java.util.List;

public class TableModel {

    public int with;
    public int height;
    public int pointX;
    public int pointY;

    public List<Style> styles = new ArrayList<>();
    public List<PropertyTable> properties = new ArrayList<>();

    public void addProperty(PropertyTable propertyTable) {
        properties.add(propertyTable);
        propertyTable.apply(this);
    }

    public void addStyle(Style style) {
        styles.add(style);
    }

    public int getWith() {
        return with;
    }

    public void setWith(int with) {
        this.with = with;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

    public List<PropertyTable> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyTable> properties) {
        this.properties = properties;
    }
}

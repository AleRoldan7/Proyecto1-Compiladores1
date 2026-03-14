package com.example.proyecto1_compi1.modelo.question;

import java.util.ArrayList;

public class TextModel {

    private String content;
    private int width;
    private int height;

    private ArrayList<Object> styles;

    public TextModel(){
        styles = new ArrayList<>();
    }

    public void addProperty(PropertyItem prop){
        System.out.println("TextModel.addProperty - Key: " + prop.getKey() +
                ", Value: " + prop.getValue() +
                ", Value type: " + (prop.getValue() != null ? prop.getValue().getClass().getSimpleName() : "null"));
        switch(prop.getKey()){

            case "content":
                content = (String) prop.getValue();
                break;

            case "width":

                Object value = prop.getValue();

                if (value instanceof Integer) {
                    width = (Integer) value;
                } else if (value instanceof String){
                    width = Integer.parseInt((String) value);
                }

                break;

            case "height":
                if (prop.getValue() instanceof Integer) {
                    height = (Integer) prop.getValue();
                } else if (prop.getValue() instanceof String) {
                    try {
                        height = Integer.parseInt((String) prop.getValue());
                    } catch (NumberFormatException e) {
                        height = 0;
                    }
                }
                break;

            case "styles":
                styles = (ArrayList<Object>) prop.getValue();
                break;
        }

    }

    public String getContent() {
        return content;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Object> getStyles() {
        return styles;
    }

}

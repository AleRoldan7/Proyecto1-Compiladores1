package com.example.proyecto1_compi1.modelo.question;

import com.example.proyecto1_compi1.analizador.form.Parser;
import com.example.proyecto1_compi1.modelo.color_style.*;

import java.util.ArrayList;

public class TextModel {

    private String content;
    private int width;
    private int height;

    private ArrayList<Object> styles;
    private Parser parser = new Parser();

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
                if (prop.getValue() instanceof ArrayList) {
                    ArrayList<Object> rawStyles = (ArrayList<Object>) prop.getValue();
                    ArrayList<Object> evaluatedStyles = new ArrayList<>();

                    for (Object style : rawStyles) {
                        if (style instanceof RgbStyleWithWildcard) {
                            // Evaluar RGB con wildcards usando el analizador semántico
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

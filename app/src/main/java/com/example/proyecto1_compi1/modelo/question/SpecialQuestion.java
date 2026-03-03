package com.example.proyecto1_compi1.modelo.question;

import androidx.room.jarjarred.org.antlr.v4.codegen.model.Wildcard;

import java.util.ArrayList;

public class SpecialQuestion {

    private String name;
    private String questionType;
    private ArrayList<Object> properties;
    private int wildcardCount;

    public SpecialQuestion(String name, String questionType) {
        this.name = name;
        this.questionType = questionType;
        this.properties = new ArrayList<>();
        this.wildcardCount = 0;
    }

    public void addProperty(Object value){
        properties.add(value);
        if(value instanceof Wildcard){
            wildcardCount++;
        }
    }

    public int getWildcardCount(){
        return wildcardCount;
    }

    public void draw(ArrayList<Object> params){

        if(params.size() != wildcardCount){
            throw new RuntimeException(
                    "Error: número incorrecto de parámetros en draw()"
            );
        }

        int index = 0;

        for(Object prop : properties){

            if(prop instanceof Wildcard){
                Object value = params.get(index++);
                System.out.println("Reemplazando ? por " + value);
            }
            else{
                System.out.println("Propiedad fija: " + prop);
            }
        }
    }
}

package com.example.proyecto1_compi1.modelo;

public class QuestionModel {

    private String type;
    private String name;

    public QuestionModel() {
    }
    public QuestionModel(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}

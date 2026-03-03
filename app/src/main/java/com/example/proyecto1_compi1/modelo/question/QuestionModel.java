package com.example.proyecto1_compi1.modelo.question;

public abstract class QuestionModel {

    protected String name;

    public QuestionModel() {
    }

    public QuestionModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

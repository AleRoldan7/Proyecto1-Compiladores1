package com.example.proyecto1_compi1.modelo;

import java.util.ArrayList;
import java.util.List;

public class FormsModel {

    private String name;
    private List<QuestionModel> questions;

    public FormsModel() {
    }

    public FormsModel(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(QuestionModel q) {
        questions.add(q);
    }

    public String getName() {
        return name;
    }

    public List<QuestionModel> getQuestions() {
        return questions;
    }
}

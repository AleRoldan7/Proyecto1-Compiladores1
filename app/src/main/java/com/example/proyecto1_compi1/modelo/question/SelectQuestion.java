package com.example.proyecto1_compi1.modelo.question;

import java.util.List;

public class SelectQuestion extends QuestionModel {

    private List<String> options;

    public SelectQuestion(String name, List<String> options) {
        super(name);
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }
}

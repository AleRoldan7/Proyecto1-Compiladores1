package com.example.proyecto1_compi1.modelo.question;

import java.util.ArrayList;
import java.util.List;

public class DropQuestion extends QuestionModel {

    private ArrayList<String> options;
    private int correct;

    public DropQuestion() {
        options = new ArrayList<>();
    }

    @Override
    public void addProperty(PropertyItem prop) {

        super.addProperty(prop);

        switch (prop.getKey()) {

            case "options":
                options = (ArrayList<String>) (Object) prop.getValue();
                break;

            case "correct":
                correct = (int) prop.getValue();
                break;

        }
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getCorrect() {
        return correct;
    }

    /*
    private List<String> options;

    public DropQuestion(String name, List<String> options) {
        super(name);
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }
     */
}

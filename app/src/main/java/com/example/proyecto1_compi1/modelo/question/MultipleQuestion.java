package com.example.proyecto1_compi1.modelo.question;

import java.util.ArrayList;
import java.util.List;

public class MultipleQuestion extends QuestionModel {

    /*
    private List<String> options;

    public MultipleQuestion(String name, List<String> options) {
        super(name);
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }
     */

    private ArrayList<String> options;
    private ArrayList<Integer> correct;

    public MultipleQuestion() {
        options = new ArrayList<>();
        correct = new ArrayList<>();
    }

    @Override
    public void addProperty(PropertyItem prop) {

        super.addProperty(prop);

        switch (prop.getKey()) {

            case "options":
                options = (ArrayList<String>) (Object) prop.getValue();
                break;

            case "correct":
                correct = (ArrayList<Integer>) prop.getValue();
                break;
        }
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public boolean isCorrect(ArrayList<Integer> answers) {
        return answers.equals(correct);
    }
    public ArrayList<Integer> getCorrect() {
        return correct;
    }
}

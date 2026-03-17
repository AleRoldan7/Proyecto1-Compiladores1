package com.example.proyecto1_compi1.modelo.question;

import java.util.ArrayList;
import java.util.List;

public class DropQuestion extends QuestionModel {

    private ArrayList<String> options;
    private ArrayList<Integer> correct;

    public DropQuestion() {
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

            default:
                super.addProperty(prop);
        }
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public ArrayList<Integer> getCorrect() {
        return correct;
    }

    /*
    public boolean isCorrect(int answer) {
        return answer == correct;
    }

    public int getCorrect() {
        return correct;
    }


     */
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

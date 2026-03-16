package com.example.proyecto1_compi1.modelo.question;

import java.util.ArrayList;
import java.util.List;

public class SelectQuestion extends QuestionModel {

    private ArrayList<String> options;
    private int correct = -1;

    public SelectQuestion() {
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
                correct = (Integer) prop.getValue();
                break;

        }
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public boolean validateAnswer(int answer) {
        return answer == correct;
    }
    public int getCorrect() {
        return correct;
    }
    /*
    private List<String> options;

    public SelectQuestion(String name, List<String> options) {
        super(name);
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }
     */
}

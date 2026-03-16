package com.example.proyecto1_compi1.modelo.forms;

import com.example.proyecto1_compi1.modelo.question.DropQuestion;
import com.example.proyecto1_compi1.modelo.question.MultipleQuestion;
import com.example.proyecto1_compi1.modelo.question.QuestionModel;
import com.example.proyecto1_compi1.modelo.question.SelectQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormResponse {

    private HashMap<QuestionModel, Object> answers = new HashMap<>();

    public void answerQuestion(QuestionModel questionModel, Object value) {
        answers.put(questionModel, value);
    }

    public boolean isCorrect(QuestionModel questionModel) {

        Object answer = answers.get(questionModel);

        if (questionModel instanceof SelectQuestion) {
            return ((SelectQuestion) questionModel).validateAnswer((Integer) answer);
        }

        if (questionModel instanceof DropQuestion) {
            return ((DropQuestion) questionModel).isCorrect((Integer) answer);
        }

        if (questionModel instanceof MultipleQuestion) {
            return ((MultipleQuestion) questionModel).isCorrect((ArrayList<Integer>) answer);
        }

        return false;
    }
}

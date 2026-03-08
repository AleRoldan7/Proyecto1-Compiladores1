package com.example.proyecto1_compi1.modelo.forms;

import com.example.proyecto1_compi1.modelo.question.QuestionModel;
import com.example.proyecto1_compi1.modelo.question.SpecialQuestion;
import com.example.proyecto1_compi1.modelo.table.TableModel;
import com.example.proyecto1_compi1.modelo.variable.VariableModel;

import java.util.ArrayList;
import java.util.List;

public class FormsModel {

    private String name;
    private List<QuestionModel> questions;
    private List<SpecialQuestion> specialQuestions = new ArrayList<>();
    private ArrayList<VariableModel> variables;

    private ArrayList<TableModel> tables;
    public FormsModel() {
    }

    public FormsModel(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.tables = new ArrayList<>();

    }

    public void addQuestion(QuestionModel q) {
        questions.add(q);
    }

    public void addVariable(VariableModel variableModel) {
        variables.add(variableModel);
    }

    public void addTable(TableModel tableModel) {
        tables.add(tableModel);
    }
    public String getName() {
        return name;
    }

    public List<QuestionModel> getQuestions() {
        return questions;
    }

    public ArrayList<VariableModel> getVariables() {
        return variables;
    }

    public ArrayList<TableModel> getTables() {
        return tables;
    }

    public void addSpecialQuestion(SpecialQuestion sq) {
        specialQuestions.add(sq);
    }

    public SpecialQuestion findSpecialQuestion(String name) {
        for (SpecialQuestion q : specialQuestions) {
            if (q.getName().equals(name)) {
                return q;
            }
        }
        return null;
    }

    public List<SpecialQuestion> getSpecialQuestions() {
        return new ArrayList<>(specialQuestions);
    }

    public SpecialQuestion getSpecialQuestion(String name) {

        for (SpecialQuestion sq : specialQuestions) {

            if (sq.getName().equals(name)) {
                return sq;
            }
        }

        return null;
    }
}

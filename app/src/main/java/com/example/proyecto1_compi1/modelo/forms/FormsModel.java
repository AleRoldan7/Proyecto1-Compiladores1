package com.example.proyecto1_compi1.modelo.forms;

import com.example.proyecto1_compi1.modelo.question.QuestionModel;
import com.example.proyecto1_compi1.modelo.question.SectionsModel;
import com.example.proyecto1_compi1.modelo.question.SpecialQuestion;
import com.example.proyecto1_compi1.modelo.question.TextModel;
import com.example.proyecto1_compi1.modelo.table.TableModel;
import com.example.proyecto1_compi1.modelo.variable.VariableModel;

import java.util.ArrayList;
import java.util.List;

public class FormsModel {

    private String name;
    private ArrayList<Object> elements = new ArrayList<>();
    private List<SpecialQuestion> specialQuestions = new ArrayList<>();
    private ArrayList<VariableModel> variables = new ArrayList<>();
    private List<QuestionModel> questions = new ArrayList<>();
    public FormsModel(){}

    public FormsModel(String name) {
        this.name = name;
    }

    public void addElement(Object element){

        if(element != null){
            elements.add(element);
            System.out.println("ELEMENTO AGREGADO " + element.getClass().getSimpleName());
        }

    }

    public ArrayList<Object> getElements(){
        return elements;
    }

    public String getName(){
        return name;
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

    public void addQuestion(QuestionModel q) {
        questions.add(q);
    }

    public void addVariable(VariableModel variableModel) {
        variables.add(variableModel);
    }

}

    /*




    private ArrayList<TableModel> tables = new ArrayList<>();

    private ArrayList<SectionsModel> sections = new ArrayList<>();

    private ArrayList<TextModel> texts = new ArrayList<>();

    public FormsModel() {
    }

    public FormsModel(String name) {
        this.name = name;
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



    public void addSection(SectionsModel sectionModel) {
        sections.add(sectionModel);
    }

    public void addText(TextModel text) {
        if (texts != null && text != null) {
            texts.add(text);
            System.out.println("Texto agregado: " + text.getContent() + ", Total textos: " + texts.size());
        }
    }

    public ArrayList<TextModel> getTexts() {
        return texts != null ? texts : new ArrayList<>();
    }

    public ArrayList<SectionsModel> getSections() {
        return sections;
    }
}
*/
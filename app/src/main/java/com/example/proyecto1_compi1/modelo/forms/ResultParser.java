package com.example.proyecto1_compi1.modelo.forms;

import java.util.ArrayList;

public class ResultParser {



    public static ArrayList<FormsModel> forms = new ArrayList<>();
    public static FormsModel currentForm = null;
    public static FormsModel formsModel = null;

    public static void reset() {
        forms.clear();
        currentForm = new FormsModel("Formulario");
        forms.add(currentForm);
        System.out.println("=== RESET PARSER ===");
        System.out.println("forms size: " + forms.size());
        System.out.println("currentForm: " + currentForm);
    }

    public static void init() {
        if (forms == null) {
            forms = new ArrayList<>();
        }
        if (currentForm == null) {
            currentForm = new FormsModel("Formulario");
            forms.add(currentForm);
            System.out.println("=== INIT PARSER ===");
            System.out.println("Nuevo formulario creado");
        }
    }

    public static void printStatus() {
        System.out.println("=== STATUS PARSER ===");
        System.out.println("forms size: " + (forms != null ? forms.size() : 0));
        System.out.println("currentForm: " + currentForm);
        if (currentForm != null) {
           // System.out.println("preguntas: " + currentForm.getQuestions().size());
            //System.out.println("textos: " + currentForm.getTexts().size());
        }
    }

}



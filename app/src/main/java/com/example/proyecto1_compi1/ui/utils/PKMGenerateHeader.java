package com.example.proyecto1_compi1.ui.utils;

import com.example.proyecto1_compi1.modelo.forms.FormsModel;
import com.example.proyecto1_compi1.modelo.question.QuestionModel;
import com.example.proyecto1_compi1.modelo.question.SectionsModel;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PKMGenerateHeader {

    public static String generateHeader(
            String autor,
            String descripcion,
            List<FormsModel> forms
    ) {

        int totalSecciones = 0;
        int totalPreguntas = 0;

        for (FormsModel form : forms) {

            for (Object e : form.getElements()) {

                if (e instanceof SectionsModel) {
                    totalSecciones++;
                }

                if (e instanceof QuestionModel) {
                    totalPreguntas++;
                }
            }
        }

       // LocalDate fecha = LocalDate.now();
        //LocalTime hora = LocalTime.now().withNano(0);

        StringBuilder header = new StringBuilder();

        header.append("###\n");
        header.append("Author: ").append(autor).append("\n");
        //header.append("Date: ").append(fecha).append("\n");
        //header.append("Time: ").append(hora).append("\n");
        header.append("Description: ").append(descripcion).append("\n");
        header.append("Sections: ").append(totalSecciones).append("\n");
        header.append("Questions: ").append(totalPreguntas).append("\n");
        header.append("###\n\n");

        return header.toString();
    }
}

package com.example.proyecto1_compi1.generate_lenguaje;

import com.example.proyecto1_compi1.modelo.forms.FormsModel;
import com.example.proyecto1_compi1.modelo.question.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GeneratePKM {

    public static String generate(List<FormsModel> forms) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("###\n");
        stringBuilder.append("Author: Usuario\n");

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String hora = new SimpleDateFormat("HH:mm").format(new Date());

        stringBuilder.append("Fecha: ").append(fecha).append("\n");
        stringBuilder.append("Hora: ").append(hora).append("\n");

        stringBuilder.append("Description: Formulario generado\n");

        int totalSections = forms.size();
        int totalQuestions = 0;
        int abiertas = 0;
        int desplegables = 0;
        int seleccion = 0;
        int multiples = 0;

        for (FormsModel form : forms) {

            /*
            for (QuestionModel q : form.getQuestions()) {

                totalQuestions++;

                if (q instanceof OpenQuestion) abiertas++;
                if (q instanceof DropQuestion) desplegables++;
                if (q instanceof SelectQuestion) seleccion++;
                if (q instanceof MultipleQuestion) multiples++;
            }

             */
        }

        stringBuilder.append("Total de Secciones: ").append(totalSections).append("\n");
        stringBuilder.append("Total de Preguntas: ").append(totalQuestions).append("\n");
        stringBuilder.append("Abiertas: ").append(abiertas).append("\n");
        stringBuilder.append("Desplegables: ").append(desplegables).append("\n");
        stringBuilder.append("Selección: ").append(seleccion).append("\n");
        stringBuilder.append("Múltiples: ").append(multiples).append("\n");

        stringBuilder.append("###\n\n");


        for (FormsModel form : forms) {

            stringBuilder.append("<section=100,100,0,0,VERTICAL>\n");

            stringBuilder.append("<content>\n");

            /*
            for (QuestionModel q : form.getElements()) {

                stringBuilder.append(generateQuestion(q));

            }
    */
            stringBuilder.append("</content>\n");

            stringBuilder.append("</section>\n\n");
        }

        return stringBuilder.toString();
    }

    private static String generateQuestion(QuestionModel q){

        StringBuilder sb = new StringBuilder();

        if(q instanceof OpenQuestion){

            OpenQuestion o = (OpenQuestion) q;

            sb.append("<open=50,10,\"")

                    .append("\"/>\n");
        }

        if(q instanceof DropQuestion){

            DropQuestion d = (DropQuestion) q;

            sb.append("<drop=50,10,\"")

                    .append("\",{");

            for(int i=0;i<d.getOptions().size();i++){

                sb.append("\"").append(d.getOptions().get(i)).append("\"");

                if(i < d.getOptions().size()-1)
                    sb.append(",");
            }

            sb.append("},-1 />\n");
        }

        if(q instanceof SelectQuestion){

            SelectQuestion s = (SelectQuestion) q;

            sb.append("<select=50,10,\"")

                    .append("\",{");

            for(int i=0;i<s.getOptions().size();i++){

                sb.append("\"").append(s.getOptions().get(i)).append("\"");

                if(i < s.getOptions().size()-1)
                    sb.append(",");
            }

            sb.append("},0/>\n");
        }

        if(q instanceof MultipleQuestion){

            MultipleQuestion m = (MultipleQuestion) q;

            sb.append("<multiple=50,10,\"")

                    .append("\",{");

            for(int i=0;i<m.getOptions().size();i++){

                sb.append("\"").append(m.getOptions().get(i)).append("\"");

                if(i < m.getOptions().size()-1)
                    sb.append(",");
            }

            sb.append("},{}/>\n");
        }

        return sb.toString();
    }
}

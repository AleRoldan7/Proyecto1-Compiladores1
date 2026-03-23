package com.example.proyecto1_compi1.modelo.question;

import com.example.proyecto1_compi1.poke.PokeRepository;

import java.util.ArrayList;
import java.util.List;

public class DropQuestion extends QuestionModel {

    private ArrayList<String> options;
    private ArrayList<Integer> correct;
    private ArrayList<String> optionsPoke = null; // parámetros de PokeAPI
    private boolean isResolved = false;

    public DropQuestion() {
        options = new ArrayList<>();
        correct = new ArrayList<>();
    }

    @Override
    public void addProperty(PropertyItem prop) {
        super.addProperty(prop);

        switch (prop.getKey()) {
            case "options":
                if (prop.getValue() instanceof ArrayList) {
                    try {
                        options = (ArrayList<String>) prop.getValue();
                    } catch (ClassCastException e) {
                        ArrayList raw = (ArrayList) prop.getValue();
                        options = new ArrayList<>();
                        for (Object item : raw)
                            options.add(item != null ? item.toString() : "");
                    }
                }
                break;

            case "options_poke":
                if (prop.getValue() instanceof ArrayList) {
                    try {
                        optionsPoke = (ArrayList<String>) prop.getValue();
                    } catch (ClassCastException e) {
                        ArrayList raw = (ArrayList) prop.getValue();
                        optionsPoke = new ArrayList<>();
                        for (Object item : raw)
                            optionsPoke.add(item != null ? item.toString() : "");
                    }
                    android.util.Log.d("DropQuestion",
                            "optionsPoke recibido: " + optionsPoke);
                }
                break;

            case "correct":
                correct = new ArrayList<>();
                Object val = prop.getValue();

                if (val instanceof ArrayList) {
                    for (Object item : (ArrayList<?>) val) {
                        correct.add(toInt(item));
                    }
                } else if (val != null) {
                    correct.add(toInt(val));
                }
                break;
        }
    }

    /**
     * Resuelve las opciones desde la PokéAPI usando PokeRepository
     * Debe ser llamado antes de renderizar la pregunta
     */
    public void resolvePokeApi() {
        if (isResolved) return;
        if (optionsPoke == null || optionsPoke.isEmpty()) return;

        try {
            // Limpiar opciones existentes
            options.clear();

            // Obtener parámetros: [tipo, desde, hasta]
            String tipo = optionsPoke.get(0); // "NUMBER" por ahora
            int desde = Integer.parseInt(optionsPoke.get(1));
            int hasta = Integer.parseInt(optionsPoke.get(2));

            android.util.Log.d("DropQuestion",
                    "Resolviendo PokéAPI: desde=" + desde + " hasta=" + hasta);

            // Llamar al repositorio (esto es BLOQUEANTE, usar en hilo secundario)
            List<String> pokemonNames = PokeRepository.INSTANCE.obtenerPokemonesSync(desde, hasta);

            options.addAll(pokemonNames);
            isResolved = true;

            android.util.Log.d("DropQuestion",
                    "PokéAPI resuelta: " + options.size() + " opciones obtenidas: " + options);

        } catch (Exception e) {
            android.util.Log.e("DropQuestion",
                    "Error resolviendo PokéAPI: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si tiene opciones de PokéAPI pendientes
     */
    public boolean needsPokeApiResolution() {
        return optionsPoke != null && !optionsPoke.isEmpty() && !isResolved;
    }

    /**
     * Obtiene las opciones, resolviendo la API si es necesario
     * ATENCIÓN: Esto bloquea el hilo si necesita resolver API
     * Usar con cuidado, preferiblemente llamar a resolvePokeApi() antes
     */
    public ArrayList<String> getOptions() {
        if (needsPokeApiResolution()) {
            android.util.Log.w("DropQuestion",
                    "getOptions() está resolviendo PokéAPI de forma bloqueante");
            resolvePokeApi();
        }
        return options;
    }

    public ArrayList<Integer> getCorrect() {
        return correct;
    }

    public ArrayList<String> getOptionsPoke() {
        return optionsPoke;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public void setCorrect(ArrayList<Integer> correct) {
        this.correct = correct;
    }

    public void setOptionsPoke(ArrayList<String> optionsPoke) {
        this.optionsPoke = optionsPoke;
    }

    private int toInt(Object value) {
        if (value == null)            return -1;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double)  return ((Double) value).intValue();
        if (value instanceof Float)   return ((Float) value).intValue();
        if (value instanceof Long)    return ((Long) value).intValue();
        if (value instanceof Number)  return ((Number) value).intValue();
        if (value instanceof String) {
            try { return (int) Double.parseDouble((String) value); }
            catch (NumberFormatException e) { return -1; }
        }
        return -1;
    }
}
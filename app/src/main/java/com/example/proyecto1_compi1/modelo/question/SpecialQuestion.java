package com.example.proyecto1_compi1.modelo.question;

import androidx.room.jarjarred.org.antlr.v4.codegen.model.Wildcard;

import java.util.ArrayList;
import java.util.List;

public class SpecialQuestion {

    private String name;
    private String type;
    private List<PropertyItem> properties;
    private List<Integer> wildcardIndices;
    private List<Object> drawArguments = new ArrayList<>();

    public SpecialQuestion(String name, String type,
                           List<PropertyItem> properties,
                           List<Integer> wildcardIndices) {
        this.name = name;
        this.type = type;
        this.properties = new ArrayList<>(properties);
        this.wildcardIndices = new ArrayList<>(wildcardIndices);
    }

    public int getWildcardCount() {
        return wildcardIndices.size();
    }

    public void setDrawArguments(List<Object> args) {
        this.drawArguments = args != null ? new ArrayList<>(args) : new ArrayList<>();
    }

    public List<PropertyItem> getResolvedProperties() {
        if (drawArguments.size() != wildcardIndices.size()) {
            return new ArrayList<>(properties);
        }

        List<PropertyItem> resolved = new ArrayList<>();
        int argIndex = 0;
        for (PropertyItem item : properties) {
            if (item.value instanceof String && "?".equals(item.value)) {
                resolved.add(new PropertyItem(item.key, drawArguments.get(argIndex++)));
            } else {
                resolved.add(item);
            }
        }
        return resolved;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public List<PropertyItem> getProperties() { return properties; }

    public QuestionModel draw() {

        List<PropertyItem> resolved = getResolvedProperties();

        switch (type.toUpperCase()) {

            case "OPENQUESTION":
                return new OpenQuestion(name);

        }

        return null;
    }

    private String getLabel(List<PropertyItem> props) {

        for (PropertyItem prop : props) {

            if (prop.key.equalsIgnoreCase("label")) {
                return prop.value.toString();
            }
        }

        return name;
    }
}
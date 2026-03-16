package com.example.proyecto1_compi1.modelo.table;

import java.util.ArrayList;
import java.util.List;

public class PropertyElements implements PropertyTable {

    private final ArrayList<ArrayList<TableCell>> matrix;

    public PropertyElements(ArrayList<ArrayList<TableCell>> matrix) {
        this.matrix = matrix;
    }

    public ArrayList<ArrayList<TableCell>> getMatrix() {
        return matrix;
    }


    @Override
    public void apply(TableModel tableModel) {
        //tableModel.setElements(matrix);
    }
}
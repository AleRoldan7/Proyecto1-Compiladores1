package com.example.proyecto1_compi1.modelo.table;

public class PropertyPosition implements PropertyTable{

    private Integer x;
    private Integer y;

    public PropertyPosition(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void apply(TableModel tableModel) {
        tableModel.setPointX(x);
        tableModel.setPointY(y);
    }
}

package com.example.proyecto1_compi1.modelo.table;

import java.util.List;

public class TableCell {

    private List<Object> content;

    public TableCell(List<Object> content) {
        this.content = content;
    }

    public List<Object> getContent() {
        return content;
    }

    public void setContent(List<Object> content) {
        this.content = content;
    }
}

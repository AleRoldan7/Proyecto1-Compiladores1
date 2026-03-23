package com.example.proyecto1_compi1.modelo.color_style;

import com.example.proyecto1_compi1.analizador.semantico.AnalizadorSemantico;

public class RgbStyleWithWildcard {
    private Object r, g, b;

    public RgbStyleWithWildcard(Object r, Object g, Object b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RgbColor evaluar(AnalizadorSemantico semantico) {
        int red = (int) semantico.evaluar(r);
        int green = (int) semantico.evaluar(g);
        int blue = (int) semantico.evaluar(b);

        // Validar rangos
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return new RgbColor(red, green, blue);
    }

    public Object getR() { return r; }
    public Object getG() { return g; }
    public Object getB() { return b; }
}
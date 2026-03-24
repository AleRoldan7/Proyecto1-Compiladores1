package com.example.proyecto1_compi1.modelo.color_style;

import com.example.proyecto1_compi1.analizador.semantico.AnalizadorSemantico;

public class HslStyleWithWildcard {
    private Object h, s, l;

    public HslStyleWithWildcard(Object h, Object s, Object l) {
        this.h = h;
        this.s = s;
        this.l = l;
    }

    public HslColor evaluar(AnalizadorSemantico semantico) {
        float hue = (float) semantico.evaluar(h);
        float saturation = (float) semantico.evaluar(s);
        float lightness = (float) semantico.evaluar(l);
        return new HslColor(hue, saturation, lightness);
    }

    public Object getH() { return h; }
    public Object getS() { return s; }
    public Object getL() { return l; }
}
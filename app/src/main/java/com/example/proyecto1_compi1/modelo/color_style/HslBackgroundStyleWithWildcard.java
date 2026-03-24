package com.example.proyecto1_compi1.modelo.color_style;

import com.example.proyecto1_compi1.analizador.semantico.AnalizadorSemantico;

public class HslBackgroundStyleWithWildcard {
    private Object h, s, l;

    public HslBackgroundStyleWithWildcard(Object h, Object s, Object l) {
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
}
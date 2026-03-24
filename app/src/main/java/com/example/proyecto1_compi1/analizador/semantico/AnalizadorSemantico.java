package com.example.proyecto1_compi1.analizador.semantico;

import com.example.proyecto1_compi1.token.Token;
import com.example.proyecto1_compi1.modelo.forms.FormsModel;
import com.example.proyecto1_compi1.modelo.question.*;
import com.example.proyecto1_compi1.modelo.variable.VariableModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class AnalizadorSemantico {

    public static class AsignacionPendiente {
        public final String variable;
        public final Object nodoIzq;
        public final String operador;
        public final Object nodoDer;

        public AsignacionPendiente(String variable,
                                   Object nodoIzq, String operador, Object nodoDer) {
            this.variable  = variable;
            this.nodoIzq   = nodoIzq;
            this.operador  = operador;
            this.nodoDer   = nodoDer;
        }

        public AsignacionPendiente(String variable, Object valor) {
            this.variable  = variable;
            this.nodoIzq   = valor;
            this.operador  = "=";
            this.nodoDer   = null;
        }

        /* Ejecuta la asignación con el estado actual del semántico */
        public void ejecutar(AnalizadorSemantico sem) {
            Object resultado;
            if ("=".equals(operador)) {
                resultado = sem.resolverNodo(nodoIzq);
            } else {
                Object va = sem.resolverNodo(nodoIzq);
                Object vb = sem.resolverNodo(nodoDer);
                switch (operador) {
                    case "+": resultado = sem.suma(va, vb);             break;
                    case "-": resultado = sem.resta(va, vb);            break;
                    case "*": resultado = sem.multiplicacion(va, vb);   break;
                    case "/": resultado = sem.division(va, vb, 0, 0);   break;
                    case "^": resultado = sem.potencia(va, vb);         break;
                    case "%": resultado = sem.modulo(va, vb, 0, 0);     break;
                    default:  resultado = sem.resolverNodo(nodoIzq);    break;
                }
            }
            sem.asignarVariable(variable, resultado, 0, 0);
        }

        @Override
        public String toString() {
            return "AsignacionPendiente{" + variable + " = " + nodoIzq
                    + (operador.equals("=") ? "" : " " + operador + " " + nodoDer) + "}";
        }
    }

    private final ArrayList<Token> errores = new ArrayList<>();
    private final HashMap<String, String> tipoVariables = new HashMap<>();
    private final HashMap<String, Object> valorVariables = new HashMap<>();

    private static final int MAX_ITERACIONES = 50;

    private String ultimoOperadorLogico = null;
    private Object[] ultimaCondicion = null;

    private static final Pattern EXPRESION_NUMERICA = Pattern.compile("^[0-9+\\-*/%^.()]+$");

    public ArrayList<Token> getErrores() {
        return errores;
    }

    public HashMap<String, String> getTipoVariables() {
        return tipoVariables;
    }

    public HashMap<String, Object> getValorVariables() {
        return valorVariables;
    }

    public void reset() {
        errores.clear();
        tipoVariables.clear();
        valorVariables.clear();
        ultimoOperadorLogico = null;
        ultimaCondicion = null;
    }

    /* DECLARACIÓN Y ASIGNACIÓN DE VARIABLES */
    public void declararVariable(String nombre, String tipo, Object valor,
                                 int linea, int columna) {
        if (tipoVariables.containsKey(nombre)) {
            agregarError(nombre, linea, columna, "Semántico",
                    "Variable '" + nombre + "' ya fue declarada. No se puede redefinir.");
            return;
        }
        if (!validarTipoAsignacion(nombre, tipo, valor, linea, columna)) return;
        tipoVariables.put(nombre, tipo);

        Object valorEvaluado = valor;
        if (valor != null && "number".equals(tipo)) {
            valorEvaluado = evaluarNumero(valor);
        }
        valorVariables.put(nombre, valorEvaluado != null ? valorEvaluado : valorPorDefecto(tipo));
    }

    public void asignarVariable(String nombre, Object valor,
                                int linea, int columna) {
        if (!tipoVariables.containsKey(nombre)) {
            agregarError(nombre, linea, columna, "Semántico",
                    "Variable '" + nombre + "' no ha sido declarada.");
            return;
        }
        String tipo = tipoVariables.get(nombre);
        if (!validarTipoAsignacion(nombre, tipo, valor, linea, columna)) return;

        Object valorEvaluado = valor;
        if ("number".equals(tipo) && valor != null) {

            if (valor instanceof Number) {
                valorEvaluado = valor;
            } else {
                valorEvaluado = evaluarNumero(valor);
            }
        }
        valorVariables.put(nombre, valorEvaluado);
    }

    public void declararVariableFor(String nombre, Object valorInicial,
                                    int linea, int columna) {
        double valInicial = evaluar(valorInicial);
        if (!tipoVariables.containsKey(nombre)) {
            tipoVariables.put(nombre, "number");
            valorVariables.put(nombre, valInicial);
        } else {
            if (!"number".equals(tipoVariables.get(nombre))) {
                agregarError(nombre, linea, columna, "Semántico",
                        "Variable '" + nombre + "' en FOR ya existe con tipo '" +
                                tipoVariables.get(nombre) + "'. Debe ser number.");
            } else {
                valorVariables.put(nombre, valInicial);
            }
        }
    }

    public void actualizarVariableCiclo(String nombre, Object valor) {
        valorVariables.put(nombre, evaluar(valor));
    }

    public boolean existeVariable(String nombre) {
        return tipoVariables.containsKey(nombre);
    }

    public String getTipoVariable(String nombre) {
        return tipoVariables.getOrDefault(nombre, null);
    }

    public Object getValorVariable(String nombre) {
        return valorVariables.getOrDefault(nombre, null);
    }

    public void setValorVariable(String nombre, Object valor) {
        valorVariables.put(nombre, valor);
    }

    public Object resolverNodo(Object nodo) {
        if (nodo == null) return null;
        if (nodo instanceof Number) return nodo;
        if (nodo instanceof AsignacionPendiente) {
            AsignacionPendiente ap = (AsignacionPendiente) nodo;
            ap.ejecutar(this);
            return valorVariables.getOrDefault(ap.variable, 0.0);
        }
        if (nodo instanceof String) {
            String s = (String) nodo;
            if (s.contains("?")) return s;
            if (valorVariables.containsKey(s)) {
                return valorVariables.get(s);
            }
            try { return Double.parseDouble(s); } catch (NumberFormatException ignored) {}
            return s;
        }
        return nodo;
    }

    /* VALIDACIONES DE DIMENSIONES */
    public int validarDimension(Object valor, String prop, int linea, int columna) {
        int v = evaluarEntero(valor);
        if (v <= 0) {
            agregarError(prop, linea, columna, "Semántico",
                    "'" + prop + "' debe ser mayor que 0, se obtuvo " + v + ".");
            return 1;
        }
        return v;
    }

    public int validarCoordenada(Object valor, String prop, int linea, int columna) {
        int v = evaluarEntero(valor);
        if (v < 0) {
            agregarError(prop, linea, columna, "Semántico",
                    "'" + prop + "' no puede ser negativo, se obtuvo " + v + ".");
            return 0;
        }
        return v;
    }

    public int validarComponenteColor(Object valor, String comp, int linea, int columna) {
        int v = evaluarEntero(valor);
        if (v < 0 || v > 255) {
            agregarError(comp, linea, columna, "Semántico",
                    "Componente '" + comp + "=" + v + "' debe estar entre 0 y 255.");
            return Math.max(0, Math.min(255, v));
        }
        return v;
    }

    public int validarTextSize(Object valor, int linea, int columna) {
        int v = evaluarEntero(valor);
        if (v <= 0) {
            agregarError("text size", linea, columna, "Semántico",
                    "El tamaño de texto debe ser > 0, se obtuvo " + v + ".");
            return 12;
        }
        return v;
    }

    /**
     * Retorna true si la lista de correctos contiene -1,
     * lo que significa "cualquier respuesta es válida".
     */
    public boolean esCorrectoCualquiera(List<Integer> correctos) {
        if (correctos == null) return false;
        for (int idx : correctos) {
            if (idx == -1) return true;
        }
        return false;
    }

    public void validarCorrect(String label, List<String> opciones,
                               List<Integer> correctos, int linea, int columna) {
        if (opciones == null || correctos == null || correctos.isEmpty()) return;

        // -1 en cualquier posición = cualquier respuesta es correcta → válido siempre
        if (esCorrectoCualquiera(correctos)) {
            System.out.println("[validarCorrect] '" + label +
                    "' tiene correct=-1: cualquier respuesta es aceptada.");
            return;
        }

        int size = opciones.size();
        if (size == 0) {
            System.out.println("[validarCorrect] '" + label +
                    "' tiene opciones vacías — probable PokeAPI, omitiendo validación.");
            return;
        }

        for (int idx : correctos) {
            if (idx < 0 || idx >= size) {
                agregarError(
                        label != null ? label : "pregunta",
                        linea, columna, "Semántico",
                        "Índice 'correct=" + idx + "' fuera de rango en '" + label +
                                "'. Opciones válidas: 0 al " + (size - 1) + "."
                );
            }
        }
    }

    public void validarOpcionesSelect(String label, List<String> opciones,
                                      int linea, int columna) {
        if (opciones == null || opciones.isEmpty()) return;
        if (opciones.size() > 5) {
            agregarError(
                    label != null ? label : "SELECT_QUESTION", linea, columna, "Advertencia",
                    "SELECT_QUESTION '" + label + "' tiene " + opciones.size() +
                            " opciones (más de 5). Use MULTIPLE_QUESTION."
            );
        }
    }

    /* EVALUACIÓN MEJORADA */
    public double evaluar(Object expr) {
        if (expr == null) return 0;
        if (expr instanceof Number) return ((Number) expr).doubleValue();

        if (expr instanceof String) {
            String s = (String) expr;
            if (s.contains("?")) return 0;

            if (valorVariables.containsKey(s)) {
                Object v = valorVariables.get(s);
                if (v instanceof Number) return ((Number) v).doubleValue();
                if (v instanceof String) {
                    try {
                        return Double.parseDouble((String) v);
                    } catch (Exception ignored) {}
                }
                return 0;
            }
            try {
                return Double.parseDouble(s);
            } catch (Exception ignored) {}
        }
        return 0;
    }


    public Object evaluarExpresionConComodines(Object expr) {
        if (expr == null) return null;
        if ("?".equals(expr)) return "?";
        if (expr instanceof Number) return expr;

        if (expr instanceof String) {
            String s = (String) expr;
            if (valorVariables.containsKey(s)) {
                Object val = valorVariables.get(s);
                return evaluarExpresionConComodines(val);
            }
            if (s.contains("?")) return s;
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return s;
            }
        }
        return expr;
    }

    public Object evaluarExpresion(Object expr) {
        if (expr == null) return null;
        if ("?".equals(expr)) return "?";
        if (expr instanceof Number) return expr;

        if (expr instanceof String) {
            String s = (String) expr;
            if (s.contains("?")) return s;
            if (valorVariables.containsKey(s)) {
                return valorVariables.get(s);
            }
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return s;
            }
        }
        return expr;
    }

    private Object evaluarNumero(Object expr) {
        if (expr == null) return 0.0;
        if (expr instanceof Number) return expr;
        if (expr instanceof String) {
            try {
                return Double.parseDouble((String) expr);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    /* OPERACIONES MEJORADAS  */

    public Object suma(Object a, Object b) {
        System.out.println("[suma] a=" + a + ", b=" + b);

        Object va = resolverVariable(a);
        Object vb = resolverVariable(b);

        if (tieneComodin(va) || tieneComodin(vb)) {
            String resultado = objetoAString(va) + " + " + objetoAString(vb);
            System.out.println("[suma] Preservando comodín: " + resultado);
            return resultado;
        }

        if(a instanceof Boolean || b instanceof Boolean){
            agregarError("+",0,0,"Semántico",
                    "No se puede concatenar booleanos");
        }

        if (va instanceof Number && vb instanceof Number) {
            double resultado = ((Number) va).doubleValue() + ((Number) vb).doubleValue();
            return resultado;
        }


        return objetoAString(va) + objetoAString(vb);
    }

    public Object resta(Object a, Object b) {
        Object va = resolverVariable(a);
        Object vb = resolverVariable(b);

        if (tieneComodin(va) || tieneComodin(vb)) {
            String resultado = objetoAString(va) + " - " + objetoAString(vb);
            System.out.println("[resta] Preservando comodín: " + resultado);
            return resultado;
        }

        if (va instanceof Number && vb instanceof Number) {
            return ((Number) va).doubleValue() - ((Number) vb).doubleValue();
        }

        return objetoAString(va) + " - " + objetoAString(vb);
    }

    public Object multiplicacion(Object a, Object b) {
        Object va = resolverVariable(a);
        Object vb = resolverVariable(b);

        if (tieneComodin(va) || tieneComodin(vb)) {
            String resultado = objetoAString(va) + " * " + objetoAString(vb);
            System.out.println("[multiplicacion] Preservando comodín: " + resultado);
            return resultado;
        }

        if (va instanceof Number && vb instanceof Number) {
            return ((Number) va).doubleValue() * ((Number) vb).doubleValue();
        }

        return objetoAString(va) + " * " + objetoAString(vb);
    }

    public Object division(Object a, Object b, int linea, int columna) {
        Object va = resolverVariable(a);
        Object vb = resolverVariable(b);

        if (tieneComodin(va) || tieneComodin(vb)) {
            String resultado = objetoAString(va) + " / " + objetoAString(vb);
            System.out.println("[division] Preservando comodín: " + resultado);
            return resultado;
        }

        if (va instanceof Number && vb instanceof Number) {
            double divisor = ((Number) vb).doubleValue();
            if (divisor == 0) {
                agregarError("/", linea, columna, "Semántico",
                        "División entre cero. Revisa tus valores.");
                return 0;
            }
            return ((Number) va).doubleValue() / divisor;
        }

        return objetoAString(va) + " / " + objetoAString(vb);
    }

    public Object potencia(Object base, Object exp) {
        Object vb = resolverVariable(base);
        Object ve = resolverVariable(exp);

        if (tieneComodin(vb) || tieneComodin(ve)) {
            return objetoAString(vb) + " ^ " + objetoAString(ve);
        }

        if (vb instanceof Number && ve instanceof Number) {
            return Math.pow(((Number) vb).doubleValue(), ((Number) ve).doubleValue());
        }

        return objetoAString(vb) + " ^ " + objetoAString(ve);
    }

    public Object modulo(Object a, Object b, int linea, int columna) {
        Object va = resolverVariable(a);
        Object vb = resolverVariable(b);

        if (tieneComodin(va) || tieneComodin(vb)) {
            return objetoAString(va) + " % " + objetoAString(vb);
        }

        if (va instanceof Number && vb instanceof Number) {
            double divisor = ((Number) vb).doubleValue();
            if (divisor == 0) {
                agregarError("%", linea, columna, "Semántico",
                        "Módulo entre cero.");
                return 0;
            }
            return ((Number) va).doubleValue() % divisor;
        }

        return objetoAString(va) + " % " + objetoAString(vb);
    }

    public Object toStringConcat(Object left, Object right) {
        return objetoAString(resolverVariable(left)) + objetoAString(resolverVariable(right));
    }

    /* CONDICIONES */
    public boolean evaluarCondicion(Object cond) {
        if (cond instanceof Number) {
            return ((Number) cond).doubleValue() >= 1;
        }
        if (cond instanceof String) {
            String s = (String) cond;
            if (s.contains("?")) return false; // Comodín no evaluable
            try {
                return Double.parseDouble(s) >= 1;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public boolean recalcularUltimaCondicion() {
        if (ultimaCondicion == null) return false;
        Object resultado = ejecutarCondicion(ultimaCondicion);
        return evaluarCondicion(resultado);
    }

    private Object ejecutarCondicion(Object[] cond) {
        if (cond == null) return 0;
        String op = (String) cond[0];
        switch (op) {
            case "mayor":
                return mayor(cond[1], cond[2]);
            case "menor":
                return menor(cond[1], cond[2]);
            case "igual":
                return igual(cond[1], cond[2]);
            case "diferente":
                return diferente(cond[1], cond[2]);
            case "mayorIgual":
                return mayorIgual(cond[1], cond[2]);
            case "menorIgual":
                return menorIgual(cond[1], cond[2]);
            case "and": {
                Object[] la = (Object[]) cond[1];
                Object[] lb = (Object[]) cond[2];
                boolean ra = evaluarCondicion(ejecutarCondicion(la));
                boolean rb = evaluarCondicion(ejecutarCondicion(lb));
                return (ra && rb) ? 1 : 0;
            }
            case "or": {
                Object[] la = (Object[]) cond[1];
                Object[] lb = (Object[]) cond[2];
                boolean ra = evaluarCondicion(ejecutarCondicion(la));
                boolean rb = evaluarCondicion(ejecutarCondicion(lb));
                return (ra || rb) ? 1 : 0;
            }
            case "not": {
                Object[] lc = (Object[]) cond[1];
                return evaluarCondicion(ejecutarCondicion(lc)) ? 0 : 1;
            }
            default:
                return 0;
        }
    }

    /* EVALUAR COLOR */
    public int evaluarColorComponente(Object expr) {
        if (expr == null) return 0;
        if (expr instanceof Number) {
            int val = ((Number) expr).intValue();
            return Math.max(0, Math.min(255, val));
        }
        if (expr instanceof String) {
            String s = (String) expr;
            if (s.contains("?")) return 0;
            try {
                int val = (int) Double.parseDouble(s);
                return Math.max(0, Math.min(255, val));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public float evaluarHslComponente(Object expr) {
        if (expr == null) return 0f;
        if (expr instanceof Number) {
            return ((Number) expr).floatValue();
        }
        if (expr instanceof String) {
            String s = (String) expr;
            if (s.contains("?")) return 0f;
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0f;
            }
        }
        return 0f;
    }


    public int mayor(Object a, Object b) {
        ultimaCondicion = new Object[]{"mayor", a, b};
        return comparar(a, b) > 0 ? 1 : 0;
    }

    public int menor(Object a, Object b) {
        ultimaCondicion = new Object[]{"menor", a, b};
        return comparar(a, b) < 0 ? 1 : 0;
    }

    public int igual(Object a, Object b) {
        ultimaCondicion = new Object[]{"igual", a, b};
        return comparar(a, b) == 0 ? 1 : 0;
    }

    public int diferente(Object a, Object b) {
        ultimaCondicion = new Object[]{"diferente", a, b};
        return comparar(a, b) != 0 ? 1 : 0;
    }

    public int mayorIgual(Object a, Object b) {
        ultimaCondicion = new Object[]{"mayorIgual", a, b};
        return comparar(a, b) >= 0 ? 1 : 0;
    }

    public int menorIgual(Object a, Object b) {
        ultimaCondicion = new Object[]{"menorIgual", a, b};
        return comparar(a, b) <= 0 ? 1 : 0;
    }

    private double comparar(Object a, Object b) {
        Object va = resolverVariable(a);
        Object vb = resolverVariable(b);
        if (tieneComodin(va) || tieneComodin(vb)) {
            return 0;
        }

        double da = va instanceof Number ? ((Number) va).doubleValue() : (double) evaluarNumero(va);
        double db = vb instanceof Number ? ((Number) vb).doubleValue() : (double) evaluarNumero(vb);

        return Double.compare(da, db);
    }

    public int and(Object a, Object b) {
        validarOperadorLogico("&&");
        Object[] condA = ultimaCondicion != null ? ultimaCondicion.clone() : null;
        boolean ra = evaluarCondicion(a);
        Object[] condB = ultimaCondicion;
        ultimaCondicion = new Object[]{"and", condA, condB};
        return (ra && evaluarCondicion(b)) ? 1 : 0;
    }

    public int or(Object a, Object b) {
        validarOperadorLogico("||");
        Object[] condA = ultimaCondicion != null ? ultimaCondicion.clone() : null;
        boolean ra = evaluarCondicion(a);
        Object[] condB = ultimaCondicion;
        ultimaCondicion = new Object[]{"or", condA, condB};
        return (ra || evaluarCondicion(b)) ? 1 : 0;
    }

    public int not(Object c) {
        Object[] condC = ultimaCondicion;
        ultimaCondicion = new Object[]{"not", condC, null};
        return evaluarCondicion(c) ? 0 : 1;
    }

    public void resetOperadorLogico() {
        ultimoOperadorLogico = null;
    }

    private void validarOperadorLogico(String operador) {
        if (ultimoOperadorLogico == null) {
            ultimoOperadorLogico = operador;
        } else if (!ultimoOperadorLogico.equals(operador)) {
            agregarError(operador, 0, 0, "Semántico",
                    "No se pueden mezclar '" + ultimoOperadorLogico +
                            "' y '" + operador + "' en la misma expresión.");
        }
    }

    /* CICLOS */
    public void reportarLimiteCiclo(String tipo, int iter, int linea, int columna) {
        if (iter >= MAX_ITERACIONES) {
            agregarError(tipo, linea, columna, "Advertencia",
                    "Ciclo " + tipo + " alcanzó el límite de " + MAX_ITERACIONES +
                            " iteraciones. Verifique la condición de salida.");
        }
    }

    public int getMaxIteraciones() {
        return MAX_ITERACIONES;
    }

    /* ANÁLISIS DE FORMULARIO */
    public void analizarFormulario(FormsModel form) {
        if (form == null) return;
        if (form.getVariables() != null)
            for (VariableModel v : form.getVariables())
                analizarVariable(v, 0, 0);
        for (Object e : form.getElements())
            analizarElemento(e);
    }

    private void analizarVariable(VariableModel v, int linea, int columna) {
        if (v != null) declararVariable(v.getName(), v.getType(), v.getValue(), linea, columna);
    }

    private void analizarElemento(Object elem) {
        if (elem instanceof SectionsModel) {
            SectionsModel s = (SectionsModel) elem;
            validarDimension(s.getWidth(), "width(section)", 0, 0);
            validarDimension(s.getHeight(), "height(section)", 0, 0);
            validarCoordenada(s.getPointX(), "pointX(section)", 0, 0);
            validarCoordenada(s.getPointY(), "pointY(section)", 0, 0);
            if (s.getElements() != null)
                for (Object sub : s.getElements()) analizarElemento(sub);
        } else if (elem instanceof SelectQuestion) {
            SelectQuestion q = (SelectQuestion) elem;
            validarDimension(q.getWidth(), "width(SELECT_QUESTION)", 0, 0);
            validarDimension(q.getHeight(), "height(SELECT_QUESTION)", 0, 0);
            if (q.getOptionsPoke() == null || q.getOptionsPoke().isEmpty()) {
                validarOpcionesSelect(q.getLabel(), q.getOptions(), 0, 0);
                validarCorrect(q.getLabel(), q.getOptions(), q.getCorrect(), 0, 0);
            }
        } else if (elem instanceof DropQuestion) {
            DropQuestion q = (DropQuestion) elem;
            validarDimension(q.getWidth(), "width(DROP_QUESTION)", 0, 0);
            validarDimension(q.getHeight(), "height(DROP_QUESTION)", 0, 0);
            if (q.getOptionsPoke() == null || q.getOptionsPoke().isEmpty()) {
                validarCorrect(q.getLabel(), q.getOptions(), q.getCorrect(), 0, 0);
            }
        } else if (elem instanceof MultipleQuestion) {
            MultipleQuestion q = (MultipleQuestion) elem;
            validarDimension(q.getWidth(), "width(MULTIPLE_QUESTION)", 0, 0);
            validarDimension(q.getHeight(), "height(MULTIPLE_QUESTION)", 0, 0);
            if (q.getOptionsPoke() == null || q.getOptionsPoke().isEmpty()) {
                validarCorrect(q.getLabel(), q.getOptions(), q.getCorrect(), 0, 0);
            }
        } else if (elem instanceof OpenQuestion) {
            OpenQuestion q = (OpenQuestion) elem;
            validarDimension(q.getWidth(), "width(OPEN_QUESTION)", 0, 0);
            validarDimension(q.getHeight(), "height(OPEN_QUESTION)", 0, 0);
        }
    }

    /* HELPERS */
    public String expresionAString(Object expr) {
        if (expr == null) return "";
        if ("?".equals(expr)) return "?";
        if (expr instanceof String) {
            String s = (String) expr;
            if (s.contains("?")) return s;
            Object val = valorVariables.get(s);
            if (val != null) return expresionAString(val);
            return s.trim();
        }
        if (expr instanceof Double) {
            double d = (Double) expr;
            if (d == Math.floor(d) && !Double.isInfinite(d))
                return String.valueOf((long) d);
            return String.valueOf(d);
        }
        return expr.toString().trim();
    }

    public int evaluarEntero(Object expr) {
        double val = evaluar(expr);
        return (int) Math.round(val);
    }

    private Object resolverVariable(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return val;
        if ("?".equals(val)) return val;
        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.contains("?")) return val;
            Object stored = valorVariables.get(strVal);
            if (stored != null) return stored;
            try { return Double.parseDouble(strVal); } catch (NumberFormatException ignored) {}
        }
        return val;
    }

    private boolean tieneComodin(Object val) {
        if (val == null) return false;
        if ("?".equals(val)) return true;
        if (val instanceof String) return ((String) val).contains("?");
        return false;
    }

    private boolean esStringNoNumerico(Object val) {
        if (val == null) return false;
        if ("?".equals(val)) return true;
        if (val instanceof String) {
            String s = (String) val;
            if (s.contains("?")) return true;
            try {
                Double.parseDouble(s);
                return false;
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    private String objetoAString(Object val) {
        if (val == null) return "";
        if ("?".equals(val)) return "?";
        if (val instanceof String && ((String) val).contains("?")) {
            return (String) val;
        }
        if (val instanceof Double) {
            double d = (Double) val;
            if (d == Math.floor(d) && !Double.isInfinite(d))
                return String.valueOf((long) d);
            return String.valueOf(d);
        }
        return val.toString();
    }

    private boolean validarTipoAsignacion(String nombre, String tipo, Object valor,
                                          int linea, int columna) {
        if (valor == null) return true;

        Object vr = resolverVariable(valor);
        if ("number".equals(tipo)) {
            if (vr instanceof Number) return true;
            if (vr instanceof String) {
                String sv = (String) vr;
                if (sv.contains("?")) return true;
                try {
                    Double.parseDouble(sv);
                    return true;
                } catch (NumberFormatException e) {

                    if (valorVariables.containsKey(sv)) return true;
                    agregarError(nombre, linea, columna, "Semántico",
                            "Variable '" + nombre + "' es number pero se asignó \""
                                    + sv + "\". Solo acepta valores numéricos.");
                    return false;
                }
            }
        }
        if ("string".equals(tipo) && vr instanceof Number) {
            agregarError(nombre, linea, columna, "Semántico",
                    "Variable '" + nombre + "' es string pero se asignó el número "
                            + vr + ". Solo acepta cadenas.");
            return false;
        }
        return true;
    }

    private Object valorPorDefecto(String tipo) {
        if ("number".equals(tipo)) return 0.0;
        if ("string".equals(tipo)) return "";
        return null;
    }

    private void agregarError(String lexema, int linea, int columna,
                              String tipo, String desc) {
        errores.add(new Token(lexema, linea, columna, tipo, desc));
    }
}
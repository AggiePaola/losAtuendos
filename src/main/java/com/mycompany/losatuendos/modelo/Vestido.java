package com.mycompany.losatuendos.modelo;

/**
 * Vestido de dama.
 *
 * Atributos especificos: longitud (corta, larga, media) y si tiene pedreria.
 */
public class Vestido extends Prenda {
    private String longitud; // corta, media, larga
    private boolean tienePedreria;

    public Vestido() {}

    public Vestido(String codigo, String nombre, String descripcion,
                   String longitud, boolean tienePedreria) {
        super(codigo, nombre, descripcion);
        this.longitud = longitud;
        this.tienePedreria = tienePedreria;
    }

    @Override
    public TipoPrenda getTipo() { return TipoPrenda.VESTIDO; }

    public String getLongitud() { return longitud; }
    public void setLongitud(String longitud) { this.longitud = longitud; }

    public boolean isTienePedreria() { return tienePedreria; }
    public void setTienePedreria(boolean tienePedreria) { this.tienePedreria = tienePedreria; }

    @Override
    public String resumen() {
        return "Vestido [" + codigo + "] " + nombre
                + " | longitud=" + longitud
                + " | pedreria=" + (tienePedreria ? "si" : "no");
    }
}

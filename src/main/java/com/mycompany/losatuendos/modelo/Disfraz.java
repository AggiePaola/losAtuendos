package com.mycompany.losatuendos.modelo;

/**
 * Disfraz.
 *
 * Se identifica por su nombre (personaje o tematica).
 */
public class Disfraz extends Prenda {
    public Disfraz() {}

    public Disfraz(String codigo, String nombre, String descripcion) {
        super(codigo, nombre, descripcion);
    }

    @Override
    public TipoPrenda getTipo() { return TipoPrenda.DISFRAZ; }

    @Override
    public String resumen() {
        return "Disfraz [" + codigo + "] " + nombre;
    }
}

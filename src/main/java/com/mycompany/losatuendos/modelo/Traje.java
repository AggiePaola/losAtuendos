package com.mycompany.losatuendos.modelo;

/**
 * Traje de caballero.
 *
 * Atributo especifico: tipo de accesorios (corbatin, chaleco, faja, etc.).
 */
public class Traje extends Prenda {
    private String tipoAccesorios;

    public Traje() {}

    public Traje(String codigo, String nombre, String descripcion,
                 String tipoAccesorios) {
        super(codigo, nombre, descripcion);
        this.tipoAccesorios = tipoAccesorios;
    }

    @Override
    public TipoPrenda getTipo() { return TipoPrenda.TRAJE; }

    public String getTipoAccesorios() { return tipoAccesorios; }
    public void setTipoAccesorios(String tipoAccesorios) { this.tipoAccesorios = tipoAccesorios; }

    @Override
    public String resumen() {
        return "Traje [" + codigo + "] " + nombre
                + " | accesorios=" + tipoAccesorios;
    }
}

package com.mycompany.losatuendos.factory;

import com.mycompany.losatuendos.modelo.*;

public class PrendaFactory {
    public Prenda crearPrenda(TipoPrenda tipo, String codigo, String nombre,
                              String descripcion, String atributoExtra,
                              boolean atributoBool) {
        switch (tipo) {
            case VESTIDO:
                return new Vestido(codigo, nombre, descripcion,
                        atributoExtra, atributoBool);
            case TRAJE:
                return new Traje(codigo, nombre, descripcion, atributoExtra);
            case DISFRAZ:
                return new Disfraz(codigo, nombre, descripcion);
            default:
                throw new IllegalArgumentException("Tipo de prenda no soportado: " + tipo);
        }
    }
}

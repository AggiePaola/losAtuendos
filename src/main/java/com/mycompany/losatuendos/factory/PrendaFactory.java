package com.mycompany.losatuendos.factory;

import com.mycompany.losatuendos.modelo.*;

/**
 * Patron Factory Method.
 *
 * Centraliza la creacion de prendas (vestidos, trajes y disfraces). Delega
 * en metodos especializados segun el tipo solicitado, de modo que el resto
 * del sistema no depende de las clases concretas. Anadir un nuevo tipo de
 * prenda solo requiere extender este factory sin tocar el codigo cliente.
 */
public class PrendaFactory {

    /**
     * Crea una prenda del tipo indicado.
     *
     * @param tipo            VESTIDO, TRAJE o DISFRAZ
     * @param codigo          codigo unico de la prenda
     * @param nombre          nombre o descripcion corta
     * @param descripcion     descripcion detallada
     * @param atributoExtra   significado depende del tipo:
     *                        - VESTIDO: longitud (corta, media, larga)
     *                        - TRAJE: tipo de accesorios
     *                        - DISFRAZ: no se usa
     * @param atributoBool    para VESTIDO indica si tiene pedreria;
     *                        los demas tipos lo ignoran
     * @return instancia concreta de Prenda
     */
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

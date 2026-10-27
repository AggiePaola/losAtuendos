package com.mycompany.losatuendos.composite;

import com.mycompany.losatuendos.modelo.Prenda;

import java.util.List;

/**
 * Patron Composite - componente comun.
 *
 * Define la interfaz comun entre una prenda individual (hoja) y un conjunto
 * de prendas (compuesto) dentro de un servicio de alquiler. Permite tratar
 * de forma uniforme una prenda o un grupo de prendas.
 */
public interface ComponenteAlquiler {

    /**
     * Lista de prendas que contiene este componente.
     * Una hoja devuelve una lista con ella misma; un compuesto agrega
     * las de todos sus hijos.
     */
    List<Prenda> getPrendas();

    /**
     * Indica si todas las prendas del componente estan disponibles para la
     * fecha indicada.
     */
    boolean disponiblePara(String fecha);

    /**
     * Resumen en texto del contenido del componente.
     */
    String resumen();
}

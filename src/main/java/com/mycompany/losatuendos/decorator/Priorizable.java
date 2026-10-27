package com.mycompany.losatuendos.decorator;

/**
 * Interfaz comun del Decorator de lavanderia.
 *
 * Cualquier prenda o envoltorio que implemente esta interfaz puede ser
 * tratada de forma uniforme al organizar las tandas de lavanderia.
 */
public interface Priorizable {

    int getId();

    String getCodigo();

    String getNombre();

    /**
     * Nivel de prioridad: a mayor numero, mayor prioridad.
     */
    int getNivelPrioridad();

    /**
     * Indica si la prenda tiene prioridad especial.
     */
    PrioridadLavanderia getPrioridad();

    String resumen();
}

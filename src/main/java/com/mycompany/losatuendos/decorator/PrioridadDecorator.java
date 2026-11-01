package com.mycompany.losatuendos.decorator;

/**
 * Decorator
 * Envuelve a otra Priorizable y sobrescribe el nivel y tipo de prioridad,sin modificar la clase original de la prenda.
 */
public class PrioridadDecorator implements Priorizable {

    private final Priorizable envuelto;
    public PrioridadDecorator(Priorizable envuelto) {
        this.envuelto = envuelto;
    }
    @Override
    public int getId() {
        return envuelto.getId();
    }
    @Override
    public String getCodigo() {
        return envuelto.getCodigo();
    }
    @Override
    public String getNombre() {
        return envuelto.getNombre();
    }
    @Override
    public int getNivelPrioridad() {
        return envuelto.getNivelPrioridad() + 10;
    }
    @Override
    public PrioridadLavanderia getPrioridad() {
        return PrioridadLavanderia.PRIORITARIA;
    }
    @Override
    public String resumen() {
        return envuelto.resumen() + " [PRIORIDAD ESPECIAL]";
    }
}

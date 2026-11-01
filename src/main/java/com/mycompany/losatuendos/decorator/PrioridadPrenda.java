package com.mycompany.losatuendos.decorator;

import com.mycompany.losatuendos.modelo.Prenda;

public class PrioridadPrenda implements Priorizable {
    private final Prenda prenda;
    private PrioridadLavanderia prioridad = PrioridadLavanderia.NORMAL;
    public PrioridadPrenda(Prenda prenda) {
        this.prenda = prenda;
    }
    public Prenda getPrenda() {
        return prenda;
    }
    @Override
    public int getId() {
        return prenda.getId();
    }
    @Override
    public String getCodigo() {
        return prenda.getCodigo();
    }
    @Override
    public String getNombre() {
        return prenda.getNombre();
    }
    @Override
    public int getNivelPrioridad() {
        return prioridad == PrioridadLavanderia.PRIORITARIA ? 10 : 1;
    }
    @Override
    public PrioridadLavanderia getPrioridad() {
        return prioridad;
    }
    public void setPrioridad(PrioridadLavanderia prioridad) {
        this.prioridad = prioridad;
    }
    @Override
    public String resumen() {
        return prenda.resumen() + " | prioridad=" + prioridad;
    }
}

package com.mycompany.losatuendos.decorator;

public interface Priorizable {
    int getId();
    String getCodigo();
    String getNombre();
    int getNivelPrioridad();
    PrioridadLavanderia getPrioridad();
    String resumen();
}

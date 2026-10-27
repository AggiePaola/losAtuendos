package com.mycompany.losatuendos.persistencia;

import com.mycompany.losatuendos.decorator.Priorizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una tanda de lavanderia.
 *
 * Contiene las prendas asignadas y respeta la capacidad maxima.
 */
public class TandaLavanderia {

    private final int numero;
    private final int capacidad;
    private final List<Priorizable> prendas = new ArrayList<>();

    public TandaLavanderia(int numero, int capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
    }

    public int getNumero() { return numero; }
    public int getCapacidad() { return capacidad; }

    public List<Priorizable> getPrendas() { return prendas; }

    public boolean hayEspacio() {
        return prendas.size() < capacidad;
    }

    public void agregar(Priorizable p) {
        if (!hayEspacio()) {
            throw new IllegalStateException("La tanda " + numero + " esta llena.");
        }
        prendas.add(p);
    }

    public int cantidad() {
        return prendas.size();
    }
}

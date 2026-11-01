package com.mycompany.losatuendos.composite;

import com.mycompany.losatuendos.modelo.Prenda;

import java.util.Collections;
import java.util.List;

/**
 * Patron Composite
 *
 * Envuelve una prenda individual para que pueda usarse dentro de un
 * ComponenteAlquiler compuesto.
 */
public class PrendaIndividual implements ComponenteAlquiler {

    private final Prenda prenda;

    public PrendaIndividual(Prenda prenda) {
        this.prenda = prenda;
    }

    public Prenda getPrenda() {
        return prenda;
    }

    @Override
    public List<Prenda> getPrendas() {
        return Collections.singletonList(prenda);
    }

    @Override
    public boolean disponiblePara(String fecha) {
        return prenda.disponiblePara(fecha);
    }

    @Override
    public String resumen() {
        return prenda.resumen();
    }
}

package com.mycompany.losatuendos.composite;

import com.mycompany.losatuendos.modelo.Prenda;

import java.util.ArrayList;
import java.util.List;

/**
 * Patron Composite - compuesto.
 *
 * Representa un conjunto de prendas (varias hojas o sub-conjuntos) dentro de
 * un servicio de alquiler. Permite consultar la disponibilidad de todas las
 * prendas a la vez y enumerarlas.
 */
public class ConjuntoPrendas implements ComponenteAlquiler {

    private final List<ComponenteAlquiler> componentes = new ArrayList<>();

    public void agregar(ComponenteAlquiler componente) {
        componentes.add(componente);
    }

    public void remover(ComponenteAlquiler componente) {
        componentes.remove(componente);
    }

    public List<ComponenteAlquiler> getComponentes() {
        return componentes;
    }

    @Override
    public List<Prenda> getPrendas() {
        List<Prenda> todas = new ArrayList<>();
        for (ComponenteAlquiler c : componentes) {
            todas.addAll(c.getPrendas());
        }
        return todas;
    }

    @Override
    public boolean disponiblePara(String fecha) {
        for (ComponenteAlquiler c : componentes) {
            if (!c.disponiblePara(fecha)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String resumen() {
        StringBuilder sb = new StringBuilder();
        for (ComponenteAlquiler c : componentes) {
            sb.append(c.resumen()).append("\n");
        }
        return sb.toString().trim();
    }
}

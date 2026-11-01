package com.mycompany.losatuendos.composite;

import com.mycompany.losatuendos.modelo.Prenda;

import java.util.List;

/**
 * Patron Composite - componente comun.
 * Define la interfaz comun entre una prenda individual y un conjunto
 * de prendas dentro delservicio de alquiler, Permite tratar
 * de forma uniforme una prenda o un grupo de prendas.
 */
public interface ComponenteAlquiler {

    List<Prenda> getPrendas();
    boolean disponiblePara(String fecha);
    String resumen();
}

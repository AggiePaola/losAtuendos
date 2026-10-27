package com.mycompany.losatuendos.persistencia;

import com.mycompany.losatuendos.modelo.Prenda;
import com.mycompany.losatuendos.modelo.TipoPrenda;

import java.util.List;

/**
 * Interfaz del patron Adapter.
 *
 * Define el contrato que la logica de negocio usa para acceder a las prendas.
 * La implementacion concreta (RepositorioPrendasH2) traduce estas operaciones
 * a sentencias SQL, de modo que la logica del negocio queda desacoplada de la
 * tecnologia de persistencia.
 */
public interface RepositorioPrendas {

    /**
     * Guarda o actualiza una prenda.
     */
    void guardar(Prenda prenda);

    /**
     * Busca una prenda por su id.
     */
    Prenda buscarPorId(int id);

    /**
     * Lista todas las prendas.
     */
    List<Prenda> listarTodas();

    /**
     * Verifica si una prenda esta disponible para una fecha concreta,
     * consultando los alquileres vigentes que la incluyen.
     *
     * @param idPrenda id de la prenda
     * @param fecha    fecha en formato yyyy-MM-dd
     * @return true si no hay alquiler activo que la ocupe en esa fecha
     */
    boolean disponiblePara(int idPrenda, String fecha);
}

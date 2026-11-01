package com.mycompany.losatuendos.builder;

import com.mycompany.losatuendos.composite.ComponenteAlquiler;
import com.mycompany.losatuendos.composite.ConjuntoPrendas;
import com.mycompany.losatuendos.composite.PrendaIndividual;
import com.mycompany.losatuendos.modelo.Cliente;
import com.mycompany.losatuendos.modelo.Empleado;
import com.mycompany.losatuendos.modelo.EstadoAlquiler;
import com.mycompany.losatuendos.modelo.Prenda;
import com.mycompany.losatuendos.modelo.ServicioAlquiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Patron Builder.
 *
 * Construye un ServicioAlquiler paso a paso y valida que todos los datos
 * obligatorios esten presentes antes de crear la instancia. El director
 * (la fachada) usa este builder y le inyecta el numero consecutivo.
 */
public class ServicioAlquilerBuilder {

    private String fechaAlquiler;
    private String fechaDevolucion;
    private Cliente cliente;
    private Empleado empleado;
    private final List<Prenda> prendas = new ArrayList<>();
    private double precioTotal;

    public ServicioAlquilerBuilder conFechaAlquiler(String fechaAlquiler) {
        this.fechaAlquiler = fechaAlquiler;
        return this;
    }

    public ServicioAlquilerBuilder conFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
        return this;
    }

    public ServicioAlquilerBuilder conCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public ServicioAlquilerBuilder conEmpleado(Empleado empleado) {
        this.empleado = empleado;
        return this;
    }

    public ServicioAlquilerBuilder agregarPrenda(Prenda prenda) {
        this.prendas.add(prenda);
        return this;
    }

    public ServicioAlquilerBuilder conPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
        return this;
    }
    public ServicioAlquiler construir(int numeroConsecutivo) {
        List<String> errores = new ArrayList<>();

        if (fechaAlquiler == null || fechaAlquiler.isBlank()) {
            errores.add("La fecha de alquiler es obligatoria.");
        }
        if (fechaDevolucion == null || fechaDevolucion.isBlank()) {
            errores.add("La fecha de devolucion es obligatoria.");
        }
        if (cliente == null) {
            errores.add("El cliente es obligatorio.");
        }
        if (empleado == null) {
            errores.add("El empleado es obligatorio.");
        }
        if (prendas.isEmpty()) {
            errores.add("Debe incluir al menos una prenda.");
        }

        if (!errores.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede crear el servicio de alquiler: "
                            + String.join(" | ", errores));
        }

        ComponenteAlquiler componente;
        if (prendas.size() == 1) {
            componente = new PrendaIndividual(prendas.get(0));
        } else {
            ConjuntoPrendas conjunto = new ConjuntoPrendas();
            for (Prenda p : prendas) {
                conjunto.agregar(new PrendaIndividual(p));
            }
            componente = conjunto;
        }

        ServicioAlquiler servicio = new ServicioAlquiler();
        servicio.setNumeroConsecutivo(numeroConsecutivo);
        servicio.setFechaAlquiler(fechaAlquiler);
        servicio.setFechaDevolucion(fechaDevolucion);
        servicio.setCliente(cliente);
        servicio.setEmpleado(empleado);
        servicio.setPrendas(componente);
        servicio.setEstado(EstadoAlquiler.ACTIVO);
        servicio.setPrecioTotal(precioTotal);
        return servicio;
    }
}

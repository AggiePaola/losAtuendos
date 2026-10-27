package com.mycompany.losatuendos.modelo;

import com.mycompany.losatuendos.composite.ComponenteAlquiler;

import java.util.List;

/**
 * Servicio de alquiler.
 *
 * Contiene numero consecutivo, fechas, cliente, empleado y una o varias
 * prendas (a traves del composite). Se construye con el Builder.
 */
public class ServicioAlquiler {
    private int id;
    private int numeroConsecutivo;
    private String fechaAlquiler;   // yyyy-MM-dd
    private String fechaDevolucion; // yyyy-MM-dd
    private Cliente cliente;
    private Empleado empleado;
    private ComponenteAlquiler prendas;
    private EstadoAlquiler estado;
    private double precioTotal;

    public ServicioAlquiler() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumeroConsecutivo() { return numeroConsecutivo; }
    public void setNumeroConsecutivo(int numeroConsecutivo) { this.numeroConsecutivo = numeroConsecutivo; }

    public String getFechaAlquiler() { return fechaAlquiler; }
    public void setFechaAlquiler(String fechaAlquiler) { this.fechaAlquiler = fechaAlquiler; }

    public String getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(String fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public ComponenteAlquiler getPrendas() { return prendas; }
    public void setPrendas(ComponenteAlquiler prendas) { this.prendas = prendas; }

    public EstadoAlquiler getEstado() { return estado; }
    public void setEstado(EstadoAlquiler estado) { this.estado = estado; }

    public double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }

    /**
     * Devuelve la lista plana de prendas del servicio.
     */
    public List<Prenda> getListaPrendas() {
        return prendas != null ? prendas.getPrendas() : java.util.Collections.emptyList();
    }
}

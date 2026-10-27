package com.mycompany.losatuendos.modelo;

/**
 * Clase base abstracta para todas las prendas.
 *
 * Cada tipo de prenda (vestido, traje, disfraz) hereda de esta clase y
 * anade sus atributos especificos. Forma parte del patron Factory Method
 * (producto) y del Composite (componente hoja dentro de un servicio).
 */
public abstract class Prenda {
    protected int id;
    protected String codigo;
    protected String nombre;
    protected String descripcion;
    protected boolean disponible = true;

    public Prenda() {}

    public Prenda(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public abstract TipoPrenda getTipo();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    /**
     * Devuelve la disponibilidad de la prenda para una fecha concreta.
     * Por defecto, si la prenda esta marcada como disponible, se considera
     * disponible para cualquier fecha. Las prendas compuestas (servicio)
     * sobrescriben este metodo.
     *
     * @param fecha fecha en formato yyyy-MM-dd
     * @return true si la prenda esta disponible
     */
    public boolean disponiblePara(String fecha) {
        return disponible;
    }

    /**
     * Descripcion breve usada en la interfaz de consola.
     */
    public abstract String resumen();
}

package com.mycompany.losatuendos.modelo;

/**
 * Empleado del negocio.
 */
public class Empleado {
    private int id;
    private String documento;
    private String nombres;
    private String apellidos;
    private String cargo;
    private String telefono;

    public Empleado() {}

    public Empleado(String documento, String nombres, String apellidos,
                    String cargo, String telefono) {
        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }
}

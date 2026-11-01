package com.mycompany.losatuendos.modelo;
public class Cliente {
    private int id;
    private String documento;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    public Cliente() {}
    public Cliente(String documento, String nombres, String apellidos, String telefono, String direccion) {
        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }
}

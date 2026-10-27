package com.mycompany.losatuendos.persistencia;

import com.mycompany.losatuendos.modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de clientes con acceso directo a JDBC.
 */
public class RepositorioClientes {

    public void guardar(Cliente c) throws SQLException {
        String sql = "INSERT INTO clientes (documento, nombres, apellidos, telefono, direccion) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getDocumento());
            ps.setString(2, c.getNombres());
            ps.setString(3, c.getApellidos());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        }
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Cliente buscarPorDocumento(String documento) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE documento = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id";
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean existeDocumento(String documento) throws SQLException {
        return buscarPorDocumento(documento) != null;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setDocumento(rs.getString("documento"));
        c.setNombres(rs.getString("nombres"));
        c.setApellidos(rs.getString("apellidos"));
        c.setTelefono(rs.getString("telefono"));
        c.setDireccion(rs.getString("direccion"));
        return c;
    }
}

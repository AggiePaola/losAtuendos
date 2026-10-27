package com.mycompany.losatuendos.persistencia;

import com.mycompany.losatuendos.modelo.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de empleados con acceso directo a JDBC.
 */
public class RepositorioEmpleados {

    public void guardar(Empleado e) throws SQLException {
        String sql = "INSERT INTO empleados (documento, nombres, apellidos, cargo, telefono) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getDocumento());
            ps.setString(2, e.getNombres());
            ps.setString(3, e.getApellidos());
            ps.setString(4, e.getCargo());
            ps.setString(5, e.getTelefono());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }
        }
    }

    public Empleado buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empleados WHERE id = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Empleado buscarPorDocumento(String documento) throws SQLException {
        String sql = "SELECT * FROM empleados WHERE documento = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados ORDER BY id";
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

    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getInt("id"));
        e.setDocumento(rs.getString("documento"));
        e.setNombres(rs.getString("nombres"));
        e.setApellidos(rs.getString("apellidos"));
        e.setCargo(rs.getString("cargo"));
        e.setTelefono(rs.getString("telefono"));
        return e;
    }
}

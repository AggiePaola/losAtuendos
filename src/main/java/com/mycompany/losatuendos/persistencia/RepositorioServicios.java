package com.mycompany.losatuendos.persistencia;

import com.mycompany.losatuendos.composite.ComponenteAlquiler;
import com.mycompany.losatuendos.composite.ConjuntoPrendas;
import com.mycompany.losatuendos.composite.PrendaIndividual;
import com.mycompany.losatuendos.modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de servicios de alquiler.
 *
 * Persiste el encabezado del servicio y las relaciones con prendas. Usa los
 * repositorios de clientes, empleados y prendas para reconstruir los objetos.
 */
public class RepositorioServicios {

    private final RepositorioClientes repoClientes = new RepositorioClientes();
    private final RepositorioEmpleados repoEmpleados = new RepositorioEmpleados();
    private final RepositorioPrendasH2 repoPrendas = new RepositorioPrendasH2();

    /**
     * Obtiene el siguiente numero consecutivo de alquiler.
     */
    public int siguienteConsecutivo() throws SQLException {
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement()) {
            stmt.execute("UPDATE contador_alquiler SET valor = valor + 1");
            try (ResultSet rs = stmt.executeQuery("SELECT valor FROM contador_alquiler")) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 1;
    }

    /**
     * Guarda un servicio de alquiler completo (encabezado + prendas).
     */
    public void guardar(ServicioAlquiler s) throws SQLException {
        String sql = "INSERT INTO servicios_alquiler " +
                     "(numero_consecutivo, fecha_alquiler, fecha_devolucion, " +
                     " id_cliente, id_empleado, estado, precio_total) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = ConexionBD.obtener();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getNumeroConsecutivo());
                ps.setString(2, s.getFechaAlquiler());
                ps.setString(3, s.getFechaDevolucion());
                ps.setInt(4, s.getCliente().getId());
                ps.setInt(5, s.getEmpleado().getId());
                ps.setString(6, s.getEstado().name());
                ps.setDouble(7, s.getPrecioTotal());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) s.setId(rs.getInt(1));
                }
            }
            // Guardar relaciones prenda
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO servicio_prendas (id_servicio, id_prenda) VALUES (?, ?)")) {
                for (Prenda p : s.getListaPrendas()) {
                    ps.setInt(1, s.getId());
                    ps.setInt(2, p.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Cambia el estado de un servicio.
     */
    public void cambiarEstado(int idServicio, EstadoAlquiler estado) throws SQLException {
        String sql = "UPDATE servicios_alquiler SET estado = ? WHERE id = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt(2, idServicio);
            ps.executeUpdate();
        }
    }

    /**
     * Lista los alquileres vigentes (ACTIVO) de un cliente.
     */
    public List<ServicioAlquiler> alquileresVigentesPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT * FROM servicios_alquiler " +
                     "WHERE id_cliente = ? AND estado = 'ACTIVO' " +
                     "ORDER BY fecha_alquiler";
        List<ServicioAlquiler> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs, conn));
            }
        }
        return lista;
    }

    /**
     * Lista todos los servicios.
     */
    public List<ServicioAlquiler> listarTodos() throws SQLException {
        List<ServicioAlquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicios_alquiler ORDER BY numero_consecutivo";
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs, conn));
        }
        return lista;
    }

    /**
     * Lista los servicios cuyo estado es ACTIVO.
     */
    public List<ServicioAlquiler> listarVigentes() throws SQLException {
        List<ServicioAlquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicios_alquiler WHERE estado = 'ACTIVO' ORDER BY fecha_alquiler";
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs, conn));
        }
        return lista;
    }

    /**
     * Obtiene un servicio por su id.
     */
    public ServicioAlquiler buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM servicios_alquiler WHERE id = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs, conn);
            }
        }
        return null;
    }

    private ServicioAlquiler mapear(ResultSet rs, Connection conn) throws SQLException {
        ServicioAlquiler s = new ServicioAlquiler();
        s.setId(rs.getInt("id"));
        s.setNumeroConsecutivo(rs.getInt("numero_consecutivo"));
        s.setFechaAlquiler(rs.getString("fecha_alquiler"));
        s.setFechaDevolucion(rs.getString("fecha_devolucion"));
        s.setCliente(repoClientes.buscarPorId(rs.getInt("id_cliente")));
        s.setEmpleado(repoEmpleados.buscarPorId(rs.getInt("id_empleado")));
        s.setEstado(EstadoAlquiler.valueOf(rs.getString("estado")));
        s.setPrecioTotal(rs.getDouble("precio_total"));

        // Cargar prendas del servicio
        List<Prenda> prendas = new ArrayList<>();
        String sqlPrendas = "SELECT id_prenda FROM servicio_prendas WHERE id_servicio = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlPrendas)) {
            ps.setInt(1, s.getId());
            try (ResultSet rsP = ps.executeQuery()) {
                while (rsP.next()) {
                    Prenda p = repoPrendas.buscarPorId(rsP.getInt("id_prenda"));
                    if (p != null) prendas.add(p);
                }
            }
        }

        ComponenteAlquiler componente;
        if (prendas.size() == 1) {
            componente = new PrendaIndividual(prendas.get(0));
        } else if (prendas.size() > 1) {
            ConjuntoPrendas conjunto = new ConjuntoPrendas();
            for (Prenda p : prendas) {
                conjunto.agregar(new PrendaIndividual(p));
            }
            componente = conjunto;
        } else {
            componente = null;
        }
        s.setPrendas(componente);
        return s;
    }
}

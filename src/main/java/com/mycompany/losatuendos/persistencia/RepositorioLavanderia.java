package com.mycompany.losatuendos.persistencia;

import com.mycompany.losatuendos.decorator.PrioridadLavanderia;
import com.mycompany.losatuendos.decorator.PrioridadPrenda;
import com.mycompany.losatuendos.modelo.Prenda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de la cola de lavanderia.
 *
 * Conserva el orden de llegada de las prendas devueltas y permite asignarlas
 * a tandas respetando la capacidad y la prioridad.
 */
public class RepositorioLavanderia {

    /**
     * Registra una prenda devuelta en la cola de lavanderia.
     *
     * @param idPrenda   prenda a lavar
     * @param fecha      fecha de ingreso
     * @param prioridad  NORMAL o PRIORITARIA
     * @return orden asignado dentro de la cola
     */
    public int registrarIngreso(int idPrenda, String fecha,
                                PrioridadLavanderia prioridad) throws SQLException {
        // Calcular el siguiente orden
        int orden = 1;
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT COALESCE(MAX(orden), 0) + 1 FROM lavanderia")) {
            if (rs.next()) orden = rs.getInt(1);
        }
        String sql = "INSERT INTO lavanderia (id_prenda, fecha_ingreso, orden, prioridad) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPrenda);
            ps.setString(2, fecha);
            ps.setInt(3, orden);
            ps.setString(4, prioridad.name());
            ps.executeUpdate();
        }
        return orden;
    }

    /**
     * Lista las prendas pendientes de lavanderia ordenadas por prioridad
     * (prioritarias primero) y luego por orden de llegada.
     */
    public List<PrioridadPrenda> listarPendientes() throws SQLException {
        List<PrioridadPrenda> lista = new ArrayList<>();
        String sql = "SELECT l.*, p.codigo, p.nombre FROM lavanderia l " +
                     "JOIN prendas p ON p.id = l.id_prenda " +
                     "WHERE l.tanda IS NULL " +
                     "ORDER BY CASE l.prioridad WHEN 'PRIORITARIA' THEN 0 ELSE 1 END, l.orden";
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Prenda p = new RepositorioPrendasH2().buscarPorId(rs.getInt("id_prenda"));
                PrioridadPrenda pp = new PrioridadPrenda(p);
                pp.setPrioridad(PrioridadLavanderia.valueOf(rs.getString("prioridad")));
                lista.add(pp);
            }
        }
        return lista;
    }

    /**
     * Organiza las prendas pendientes en tandas de la capacidad indicada.
     *
     * Las prioritarias se colocan primero dentro de cada tanda, respetando
     * el orden de llegada dentro de cada grupo de prioridad.
     *
     * @param capacidad numero maximo de prendas por tanda
     * @return mapa con el numero de tanda y las prendas asignadas
     */
    public List<TandaLavanderia> organizarTandas(int capacidad) throws SQLException {
        List<PrioridadPrenda> pendientes = listarPendientes();
        List<TandaLavanderia> tandas = new ArrayList<>();
        int numeroTanda = 1;
        int pos = 0;
        while (pos < pendientes.size()) {
            TandaLavanderia tanda = new TandaLavanderia(numeroTanda, capacidad);
            while (tanda.hayEspacio() && pos < pendientes.size()) {
                PrioridadPrenda pp = pendientes.get(pos);
                tanda.agregar(pp);
                // Marcar la tanda en la BD
                actualizarTanda(pp.getId(), numeroTanda);
                pos++;
            }
            tandas.add(tanda);
            numeroTanda++;
        }
        return tandas;
    }

    private void actualizarTanda(int idPrenda, int numeroTanda) throws SQLException {
        String sql = "UPDATE lavanderia SET tanda = ? WHERE id_prenda = ? AND tanda IS NULL";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroTanda);
            ps.setInt(2, idPrenda);
            ps.executeUpdate();
        }
    }

    /**
     * Marca una prenda como disponible de nuevo (despues del lavado) y la
     * quita de la cola de lavanderia.
     */
    public void marcarLavada(int idPrenda) throws SQLException {
        String sql = "DELETE FROM lavanderia WHERE id_prenda = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPrenda);
            ps.executeUpdate();
        }
        // Marcar prenda como disponible
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE prendas SET disponible = TRUE WHERE id = ?")) {
            ps.setInt(1, idPrenda);
            ps.executeUpdate();
        }
    }

    public List<TandaLavanderia> historialTandas() throws SQLException {
        List<TandaLavanderia> tandas = new ArrayList<>();
        String sql = "SELECT tanda, COUNT(*) AS cantidad FROM lavanderia " +
                     "WHERE tanda IS NOT NULL GROUP BY tanda ORDER BY tanda";
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tandas.add(new TandaLavanderia(rs.getInt("tanda"), rs.getInt("cantidad")));
            }
        }
        return tandas;
    }
}

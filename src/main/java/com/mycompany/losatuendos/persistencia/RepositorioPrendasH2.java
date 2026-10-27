package com.mycompany.losatuendos.persistencia;

import com.mycompany.losatuendos.factory.PrendaFactory;
import com.mycompany.losatuendos.modelo.*;
import com.mycompany.losatuendos.modelo.TipoPrenda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del RepositorioPrendas sobre H2.
 *
 * Es el Adaptee del patron Adapter: traduce las operaciones de la interfaz
 * RepositorioPrendas a sentencias SQL, de modo que la logica del negocio no
 * conoce los detalles de persistencia.
 */
public class RepositorioPrendasH2 implements RepositorioPrendas {

    private final PrendaFactory factory = new PrendaFactory();

    @Override
    public void guardar(Prenda prenda) {
        String sql = "INSERT INTO prendas (codigo, tipo, nombre, descripcion, disponible, " +
                     "longitud, tiene_pedreria, tipo_acesorios) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, prenda.getCodigo());
            ps.setString(2, prenda.getTipo().name());
            ps.setString(3, prenda.getNombre());
            ps.setString(4, prenda.getDescripcion());
            ps.setBoolean(5, prenda.isDisponible());
            if (prenda instanceof Vestido) {
                Vestido v = (Vestido) prenda;
                ps.setString(6, v.getLongitud());
                ps.setBoolean(7, v.isTienePedreria());
                ps.setNull(8, Types.VARCHAR);
            } else if (prenda instanceof Traje) {
                Traje t = (Traje) prenda;
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.BOOLEAN);
                ps.setString(8, t.getTipoAccesorios());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.BOOLEAN);
                ps.setNull(8, Types.VARCHAR);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) prenda.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error guardando prenda", e);
        }
    }

    @Override
    public Prenda buscarPorId(int id) {
        String sql = "SELECT * FROM prendas WHERE id = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando prenda", e);
        }
        return null;
    }

    @Override
    public List<Prenda> listarTodas() {
        List<Prenda> lista = new ArrayList<>();
        String sql = "SELECT * FROM prendas ORDER BY id";
        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error listando prendas", e);
        }
        return lista;
    }

    @Override
    public boolean disponiblePara(int idPrenda, String fecha) {
        // Si la prenda no esta marcada como disponible, no se puede alquilar
        String sqlPrenda = "SELECT disponible FROM prendas WHERE id = ?";
        String sqlAlquiler = "SELECT 1 FROM servicios_alquiler sa " +
                             "JOIN servicio_prendas sp ON sp.id_servicio = sa.id " +
                             "WHERE sp.id_prenda = ? AND sa.estado = 'ACTIVO' " +
                             "AND ? BETWEEN sa.fecha_alquiler AND sa.fecha_devolucion";
        try (Connection conn = ConexionBD.obtener()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlPrenda)) {
                ps.setInt(1, idPrenda);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && !rs.getBoolean("disponible")) return false;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlAlquiler)) {
                ps.setInt(1, idPrenda);
                ps.setString(2, fecha);
                try (ResultSet rs = ps.executeQuery()) {
                    return !rs.next(); // si hay resultados, no esta disponible
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando disponibilidad", e);
        }
    }

    public Prenda buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM prendas WHERE codigo = ?";
        try (Connection conn = ConexionBD.obtener();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando prenda por codigo", e);
        }
        return null;
    }

    private Prenda mapear(ResultSet rs) throws SQLException {
        TipoPrenda tipo = TipoPrenda.valueOf(rs.getString("tipo"));
        String longitud = rs.getString("longitud");
        boolean pedreria = rs.getBoolean("tiene_pedreria");
        String accesorios = rs.getString("tipo_acesorios");

        Prenda p;
        switch (tipo) {
            case VESTIDO:
                p = factory.crearPrenda(tipo, rs.getString("codigo"),
                        rs.getString("nombre"), rs.getString("descripcion"),
                        longitud != null ? longitud : "media",
                        pedreria);
                break;
            case TRAJE:
                p = factory.crearPrenda(tipo, rs.getString("codigo"),
                        rs.getString("nombre"), rs.getString("descripcion"),
                        accesorios != null ? accesorios : "", false);
                break;
            default:
                p = factory.crearPrenda(tipo, rs.getString("codigo"),
                        rs.getString("nombre"), rs.getString("descripcion"),
                        null, false);
        }
        p.setId(rs.getInt("id"));
        p.setDisponible(rs.getBoolean("disponible"));
        return p;
    }
}

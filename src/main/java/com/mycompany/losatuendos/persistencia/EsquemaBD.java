package com.mycompany.losatuendos.persistencia;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea el esquema relacional de la base de datos.
 *
 * Tablas: clientes, empleados, prendas, servicios_alquiler,
 * servicio_prendas. Incluye claves primarias y foraneas.
 */
public class EsquemaBD {

    public static void crearSiNoExiste() {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS clientes (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  documento VARCHAR(50) NOT NULL UNIQUE," +
            "  nombres VARCHAR(100) NOT NULL," +
            "  apellidos VARCHAR(100) NOT NULL," +
            "  telefono VARCHAR(30)," +
            "  direccion VARCHAR(200)" +
            ")",

            "CREATE TABLE IF NOT EXISTS empleados (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  documento VARCHAR(50) NOT NULL UNIQUE," +
            "  nombres VARCHAR(100) NOT NULL," +
            "  apellidos VARCHAR(100) NOT NULL," +
            "  cargo VARCHAR(50)," +
            "  telefono VARCHAR(30)" +
            ")",

            "CREATE TABLE IF NOT EXISTS prendas (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  codigo VARCHAR(50) NOT NULL UNIQUE," +
            "  tipo VARCHAR(20) NOT NULL," +
            "  nombre VARCHAR(100) NOT NULL," +
            "  descripcion VARCHAR(300)," +
            "  disponible BOOLEAN NOT NULL DEFAULT TRUE," +
            "  longitud VARCHAR(20)," +
            "  tiene_pedreria BOOLEAN," +
            "  tipo_acesorios VARCHAR(100)" +
            ")",

            "CREATE TABLE IF NOT EXISTS servicios_alquiler (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  numero_consecutivo INT NOT NULL UNIQUE," +
            "  fecha_alquiler VARCHAR(10) NOT NULL," +
            "  fecha_devolucion VARCHAR(10) NOT NULL," +
            "  id_cliente INT NOT NULL," +
            "  id_empleado INT NOT NULL," +
            "  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'," +
            "  precio_total DECIMAL(10,2) DEFAULT 0," +
            "  CONSTRAINT fk_serv_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)," +
            "  CONSTRAINT fk_serv_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id)" +
            ")",

            "CREATE TABLE IF NOT EXISTS servicio_prendas (" +
            "  id_servicio INT NOT NULL," +
            "  id_prenda INT NOT NULL," +
            "  PRIMARY KEY (id_servicio, id_prenda)," +
            "  CONSTRAINT fk_sp_servicio FOREIGN KEY (id_servicio) REFERENCES servicios_alquiler(id)," +
            "  CONSTRAINT fk_sp_prenda FOREIGN KEY (id_prenda) REFERENCES prendas(id)" +
            ")",

            "CREATE TABLE IF NOT EXISTS lavanderia (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  id_prenda INT NOT NULL," +
            "  fecha_ingreso VARCHAR(10) NOT NULL," +
            "  orden INT NOT NULL," +
            "  prioridad VARCHAR(20) NOT NULL DEFAULT 'NORMAL'," +
            "  tanda INT," +
            "  CONSTRAINT fk_lav_prenda FOREIGN KEY (id_prenda) REFERENCES prendas(id)" +
            ")",

            "CREATE TABLE IF NOT EXISTS contador_alquiler (" +
            "  valor INT NOT NULL DEFAULT 0" +
            ")"
        };

        try (Connection conn = ConexionBD.obtener();
             Statement stmt = conn.createStatement()) {
            for (String sql : ddl) {
                stmt.execute(sql);
            }
            // Inicializar el contador si esta vacio
            stmt.execute("MERGE INTO contador_alquiler (valor) KEY(valor) " +
                         "SELECT 0 WHERE NOT EXISTS (SELECT 1 FROM contador_alquiler)");
        } catch (SQLException e) {
            throw new RuntimeException("Error creando el esquema de BD", e);
        }
    }
}

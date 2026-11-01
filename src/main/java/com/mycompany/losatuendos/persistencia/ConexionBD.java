package com.mycompany.losatuendos.persistencia;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL_POR_DEFECTO =
            "jdbc:h2:./losatuendos;DB_CLOSE_DELAY=-1";

    private static String url;

    static {
        url = System.getProperty("losatuendos.db.url", URL_POR_DEFECTO);
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontro el driver H2", e);
        }
    }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    public static String getUrl() {
        return url;
    }
}

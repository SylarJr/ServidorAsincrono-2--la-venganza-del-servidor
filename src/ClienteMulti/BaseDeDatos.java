package ClienteMulti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDeDatos {
      private static final String DB_URL = "jdbc:sqlite:data/Usuarios.db";

    public static void inicializar() {
        String sqlCrearTabla = 
            "CREATE TABLE IF NOT EXISTS Usuarios (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Usuario TEXT UNIQUE NOT NULL," +
            "Contraseña TEXT NOT NULL" +
            ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlCrearTabla);
            System.out.println("Base de datos lista.");

        } catch (SQLException e) {
            System.out.println("Error al inicializar BD: " + e.getMessage());
        }
    }
}

package ClienteMulti;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class SesionRegistro {
   
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\Juan\\Documents\\Proyectos\\Servidor Asincrono\\ServidorAsincrono\\src\\ClienteMulti\\Usuarios.db";
    
    public static boolean registrarUsuario(String username, String password) {
        String sql = "INSERT INTO Usuarios(username, password) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public static boolean iniciarSesion(String username, String password) {
        String sql = "SELECT * FROM Usuarios WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
            return false;
        }
    }
}

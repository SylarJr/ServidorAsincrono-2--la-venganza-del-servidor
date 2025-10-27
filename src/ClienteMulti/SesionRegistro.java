package ClienteMulti;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class SesionRegistro {
   
    private static final String DB_URL = "jdbc:sqlite:data/Usuarios.db";
    
    public static boolean registrarUsuario(String Usuario, String Contraseña) {
        String sql = "INSERT INTO Usuarios(Usuario, Contraseña) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, Usuario);
            pstmt.setString(2, Contraseña);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public static boolean iniciarSesion(String Usuario, String Contraseña) {
        String sql = "SELECT * FROM Usuarios WHERE Usuario = ? AND Contraseña = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, Usuario);
            pstmt.setString(2, Contraseña);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
            return false;
        }
    }
}

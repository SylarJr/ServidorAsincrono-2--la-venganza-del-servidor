import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import ClienteMulti.BaseDeDatos;

public class ServidorAsincrono {
    static HashMap <String, UnCliente> Cliente = new HashMap <String,UnCliente>();
    static HashMap <String, JuegoGato> juegosActivos = new HashMap <String, JuegoGato>();

    public static void main(String[] args) throws IOException {
        BaseDeDatos.inicializar();
        ServerSocket servidorSocket = new ServerSocket(8080);
        int contador = 0;
        while (true){
            Socket s = servidorSocket.accept();
            String idCliente = Integer.toString(contador);
            UnCliente unCliente = new UnCliente(s,idCliente);
            Thread hilo = new Thread(unCliente);
            Cliente.put(Integer.toString(contador), unCliente);
            hilo.start();
            contador++;
        }
    }
    public static UnCliente getClientePorUsername(String username) {
        for (UnCliente cliente : Cliente.values()) {
            if (username.equals(cliente.username)) {
                return cliente;
            }
        }
        return null; // No se encontró
    }
}

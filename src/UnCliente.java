import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class UnCliente implements Runnable {
    final DataOutputStream salida;

    final BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

    final DataInputStream entrada;

    String idCliente;

    public UnCliente(Socket s, String idCliente) throws IOException {
        salida = new DataOutputStream(s.getOutputStream());
        entrada = new DataInputStream(s.getInputStream());
        this.idCliente = this.idCliente;
    }

    @Override

    public void run() {
    Mensajes manejador = new Mensajes(entrada);
    Thread hiloMensaje = new Thread(manejador);
    hiloMensaje.start();

}
}

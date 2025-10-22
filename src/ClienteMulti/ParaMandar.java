package ClienteMulti;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ParaMandar implements Runnable {
    final BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    final DataOutputStream salida ;
    private boolean sesionIniciada = false;
    private String username ;
    private byte mensajesEnviados =0;
    private byte LimiteMensajes =3;

    public ParaMandar(Socket s) throws IOException {
        this.salida = new DataOutputStream(s.getOutputStream());
    }

    private void mostrarAyuda(){
        System.out.println("""
                comandos Disponibles:
                /register -Usuario- -Contraseña- -Para reguistrar se
                /login -Usuario- -Contraseña- -Para iniciar sesion
                /help -Muestra los comandos disponibles
                Nota: si no tienes sesion activa solo puedes mandar 3 mensajes, Cuidado con eso
                """);
    }

    @Override
    public void run() {
        while ( true ){
            String mensaje;
            try {
                mensaje = teclado.readLine();
                if (mensaje.startsWith("/")){
                    //ProcesarComando(Mensaje);
                    //continue;
                }
            } catch (IOException ex) {
            }

        }
    }
}

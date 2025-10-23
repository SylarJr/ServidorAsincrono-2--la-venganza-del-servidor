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
    Comandos comandos = new Comandos();

    public ParaMandar(Socket s) throws IOException {
        this.salida = new DataOutputStream(s.getOutputStream());
    }


    @Override
    public void run() {
        while ( true ){
            String mensaje;
            try {
                mensaje = teclado.readLine();
                if (mensaje.startsWith("/")){
                 String [] partes = mensaje.split("\\s+", 3);
                 String cmd = partes [0].toLowerCase();
                 switch (cmd) {
                    case "/help":
                        comandos.mostrarAyuda();
                        break;

                    case "/login":
                    //por ahora no hay sistema de sesiones
                    if(partes.length>= 2){
                        this.username = partes[2]
                    }
                    this.sesionIniciada = true;
                    this.
                    break;
                 
                    default:
                        break;
                 }
                }
            } catch (IOException ex) {
            }

        }
    }
}

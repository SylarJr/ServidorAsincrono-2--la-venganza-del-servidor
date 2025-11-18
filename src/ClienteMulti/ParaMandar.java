package ClienteMulti;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ParaMandar implements Runnable {
    final BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    final DataOutputStream salida ;
  
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
                
                if (mensaje.equalsIgnoreCase("/ayuda")) {
                   
                    comandos.mostrarAyuda();
                } else {
                   
                    salida.writeUTF(mensaje);
                    salida.flush();
                }

            } catch (IOException ex) {
                System.out.println("Error leyendo del teclado o enviando: " + ex.getMessage());
                break; 
            }
        }
    }
}

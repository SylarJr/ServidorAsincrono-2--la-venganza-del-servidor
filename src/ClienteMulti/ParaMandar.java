package ClienteMulti;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ParaMandar implements Runnable {
    final BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    
  
    Comandos comandos = new Comandos();

    public ParaMandar(Socket s) throws IOException {}
    


    @Override
    public void run() {
       while (true) {
            try {
                String mensaje = teclado.readLine(); 

             
                if (ClienteMulti.socketActual == null || ClienteMulti.socketActual.isClosed()) {
                    System.out.println("No hay conexión. Mensaje no enviado.");
                    continue; 
                }

                
                if (mensaje.equalsIgnoreCase("/ayuda")) {
                    comandos.mostrarAyuda();
                } else {
                    
                    DataOutputStream salidaTemp = new DataOutputStream(ClienteMulti.socketActual.getOutputStream());
                    salidaTemp.writeUTF(mensaje);
                    salidaTemp.flush();
                }

            } catch (IOException ex) {
                System.out.println("Error al intentar enviar (Servidor caído): " + ex.getMessage());
             
            }
        }
    }
}

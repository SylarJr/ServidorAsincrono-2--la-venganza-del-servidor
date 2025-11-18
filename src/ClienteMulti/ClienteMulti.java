package ClienteMulti;

import java.io.IOException;
import java.net.Socket;

public class ClienteMulti {
    

    public static volatile Socket socketActual = null;
    public static void main(String[] args) throws IOException, InterruptedException {
      
      
      Socket s = new Socket("localhost", 8080);
        ParaMandar Paramandar = new ParaMandar(s);
        Thread hiloMandar = new Thread(Paramandar);
        hiloMandar.start();
         
        System.out.println("iniciando cliente...Buscando servidor...");

       while (true) {
            try {
               
                socketActual = new Socket("localhost", 8080);
                System.out.println("¡Conectado al servidor!");

               
                ParaRecibir paraRecibir = new ParaRecibir(socketActual);
                Thread hiloRecibir = new Thread(paraRecibir);
                hiloRecibir.start();
                hiloRecibir.join(); 

            } catch (IOException e) {
                System.out.println("No se pudo conectar. Reintentando en 3 segundos...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (socketActual != null && !socketActual.isClosed()) {
                socketActual.close();
            }
            socketActual = null;
            Thread.sleep(3000); 
        }
    }
    
}

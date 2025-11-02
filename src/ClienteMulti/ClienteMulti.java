package ClienteMulti;

import java.io.IOException;
import java.net.Socket;

public class ClienteMulti {
    

    
    public static void main(String[] args) throws IOException {
      Socket s = new Socket("localhost", 8080);
        ParaMandar Paramandar = new ParaMandar(s);
        Thread hiloMandar = new Thread(Paramandar);
        hiloMandar.start();

        ParaRecibir paraRecibir = new ParaRecibir(s, Paramandar);
        Thread hiloRecibir = new Thread(paraRecibir);
        hiloRecibir.start();
    }
    
}

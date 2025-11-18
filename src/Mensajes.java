

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class Mensajes implements Runnable {
 private DataInputStream entrada;
 private ManejadorComandos manejador;
 private UnCliente miCliente;


    // Constructor que recibe el flujo de entrada del cliente
   public Mensajes(DataInputStream entrada, UnCliente miCliente) {
        this.entrada = entrada;
        this.miCliente = miCliente;
        this.manejador = new ManejadorComandos();
    }

    @Override
    public void run() {
        try {
            while (true) {
               String mensaje = entrada.readUTF();
                // Si el mensaje llega vacío o null, lo ignoramos
                if (mensaje == null || mensaje.trim().isEmpty()) continue;

                switch (miCliente.estado) {
                    case Conectado:
                        // Aquí SOLO funcionan /entrar y /registrar
                        manejador.procesarConectado(miCliente, mensaje);
                        break;
                        
                    case Libre:
                    case Invitado:
                        // Aquí funciona el chat normal y /invitar, /ayuda
                        manejador.procesarMensajeChat(miCliente, mensaje);
                        break;
                        
                    case Jugando:
                        // Aquí funciona /jugar y el chat privado
                        manejador.procesarMensajeJuego(miCliente, mensaje);
                        break;
                        
                    default:
                        System.out.println("Error: Estado desconocido " + miCliente.estado);
                }
            }
        } catch (IOException ex) {
            manejador.procesarDesconexion(miCliente);
        }
    }
    
}

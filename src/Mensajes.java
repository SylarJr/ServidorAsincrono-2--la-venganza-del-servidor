

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class Mensajes implements Runnable {
 private DataInputStream entrada;

    // Constructor que recibe el flujo de entrada del cliente
   public Mensajes(DataInputStream entrada) {
        this.entrada = entrada;
    }

    @Override
    public void run() {
        String mensaje;
        try {
            while (true) {
                mensaje = entrada.readUTF();

                if (mensaje.startsWith("@")) {
                    // Dividimos el mensaje en partes (por ejemplo: "@1 Hola")
                    String[] partes = mensaje.split(" ", 2);

                    if (partes.length < 2) {
                        // evita errores si no hay mensaje
                        continue;
                    }

                    // Obtenemos el número de cliente destinatario (quitando "@")
                    String Aquien = partes[0].substring(1);
                    int AquienNumerico = Integer.parseInt(Aquien);

                    // Buscamos el cliente correspondiente en el mapa del servidor
                    UnCliente cliente = ServidorAsincrono.Cliente.get(String.valueOf(AquienNumerico));

                    if (cliente != null) {
                        // Enviamos el mensaje solo al destinatario
                        cliente.salida.writeUTF(partes[1]); // enviamos solo el mensaje, sin "@1"
                    } else {
                        System.out.println("Cliente " + AquienNumerico + " no encontrado.");
                    }

                } else {
                    // Si no es mensaje privado, se envía a todos los clientes conectados
                    for (UnCliente cliente : ServidorAsincrono.Cliente.values()) {
                        cliente.salida.writeUTF(mensaje);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Error en la lectura del mensaje: " + ex.getMessage());
        }
    }
}

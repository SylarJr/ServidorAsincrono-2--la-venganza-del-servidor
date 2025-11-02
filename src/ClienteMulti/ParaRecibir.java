package ClienteMulti;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ParaRecibir implements Runnable {
   final DataInputStream entrada;
   private ParaMandar paraMandar;
    public ParaRecibir(Socket s, ParaMandar paraMandar) throws IOException {
        entrada = new DataInputStream(s.getInputStream());
        this.paraMandar = paraMandar;
    }

    @Override
    public void run() {
        String mensaje;
        mensaje = "";
        while(true){
            try {
                mensaje = entrada.readUTF();
                if (mensaje.startsWith("INVITACION_DE:")){
                    String invitador = mensaje.substring("INVITACION_DE:".length());
                    paraMandar.setInvitacionPendiente(true);
                    paraMandar.setInvitador(invitador);
                    System.out.println("Has recibido una invitacion de " + invitador + "Escribes /aceptar o /rechar");
                }else if(mensaje.startsWith("/privado")){
                    // Mensajes privados
                    String contenido = mensaje.substring(9);
                    System.out.println("mesaje de tu rival: "+ contenido);
                }else{
                    System.out.println(mensaje);
                }
                
                
            } catch (IOException ex) {
            }
        }
    } 
    
}

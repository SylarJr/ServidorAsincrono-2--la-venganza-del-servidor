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
    private boolean enPartida = false;
    private String rival = null;
    private boolean invitacionPendiente = false;

    //getter  de invitacionPendiente
    public void setInvitacionPendiente(boolean invitacionPendiente) {
        this.invitacionPendiente = invitacionPendiente;
    }
    // hasta aca

    private String invitador = null;
    public void setInvitador(String invitador) {
        this.invitador = invitador;
    }

    private Partida partida = null;
    

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
                        //login de usuario existente
                    case "/login":
                        try {
                            System.out.println("ingrese su usuario: ");
                            String usuario = teclado.readLine();
                            System.out.println("ingrese su contraseña: ");
                            String contraseña = teclado.readLine();
                            boolean exito = SesionRegistro.iniciarSesion(usuario, contraseña);
                            if (exito){
                                System.out.println("sesion iniciada con exito, bienvenido " + usuario + " ya puedes mandar mensajes ilimitados");
                                this.username = usuario;
                                this.sesionIniciada = true;
                                this.mensajesEnviados = 0;
                            }else {
                                System.out.println("Error al iniciar sesion, usuario o contraseña incorrectos");
                            }
                        }catch (IOException e){
                            System.out.println("Error al leer del teclado: " + e.getMessage());
                        }
                    break;

                    //registro de nuevo usuario
                    case "/register":
                    try{
                 System.out.println("Diga su nombre de usuario: ");
                    String nuevoUsuario = teclado.readLine();
                    System.out.println("Diga su contraseña: ");
                    String nuevaContraseña = teclado.readLine();
                    boolean exito = SesionRegistro.registrarUsuario(nuevoUsuario, nuevaContraseña);
                    if (exito) {
                        System.out.println("Usuario registrado con éxito.");
                    } else {
                        System.out.println("Error al registrar el usuario. Inténtelo de nuevo.");

                  } 
                 }catch(IOException e){
                    System.out.println("Error al leer del teclado: " + e.getMessage());
                  }
                    break;
                    case "/invitar":
                        if (!sesionIniciada) {
                            System.out.println("Debes de iniciar iniciar sesion");
                            break;
                        }
                        if(enPartida){
                            System.out.println("ya estas en una partida con " + rival);
                        }
                         if (partes.length < 2){
                            System.out.println("EL comando se usa asi: /invitar a jugar -nombre de usuario-");
                         }
                         String usuarioInvitado = partes[1];
                         salida.writeUTF("/invitar" + usuarioInvitado);
                         salida.flush();
                         System.out.println("Se ha invitado a "+ usuarioInvitado);
                        break;

                    case "/aceptar":
                    if(invitacionPendiente && invitador != null){
                        salida.writeUTF("/aceptar" + invitador);
                        salida.flush();
                        enPartida = true;
                        rival = invitador;
                        partida = new Partida(username, rival);
                        invitacionPendiente = false;
                        invitador = null;
                        System.out.println("Has aceptado, Es hora de jugar ");
                        System.out.println(partida.mostrarTablero());

                    } else {
                        System.out.println("no hay invitaciones pendientes");
                    }
                    case "/rechazar":
                    if(invitacionPendiente && invitador != null){
                        salida.writeUTF("/denegar " + invitador);
                        salida.flush();
                        invitacionPendiente = false;
                        invitador = null;
                        System.out.println("Has rechazado la invitacion ");
                    }else{
                        System.out.println("no tienes invitaciones pendientes");
                    }
                    break;
                 
                    default:
                    System.out.println("comando Incorrecto o desconocido, escribe /help para ver los comandos disponibles");
                        break;
                 }
                } else{
                    if (sesionIniciada){
                        try{
                            if(enPartida && partida != null){
                                salida.writeUTF("/privado" + rival + " " + mensaje);
                            }else{
                            salida.writeUTF(username + ": " + mensaje);
                                 }
                            salida.flush();
                        }catch(IOException e){
                            System.out.println("Error al enviar el mensaje: " + e.getMessage());
                        }
                    }else{
                        if(mensajesEnviados < LimiteMensajes){
                            try{
                                salida.writeUTF("Anonimo: " + mensaje);
                                salida.flush();
                                mensajesEnviados++;
                                System.out.println("Te quedan " + (LimiteMensajes - mensajesEnviados) + " mensajes antes de iniciar sesion.");
                                
                            }catch(IOException e){
                                System.out.println("Error al enviar el mensaje: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Has alcanzado el límite de mensajes sin iniciar sesión. Por favor, inicia sesión para continuar enviando mensajes, Nadie ve lo que haces ahora.");
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error leyendo del teclado: " + ex.getMessage());
            }

        }
    }
}

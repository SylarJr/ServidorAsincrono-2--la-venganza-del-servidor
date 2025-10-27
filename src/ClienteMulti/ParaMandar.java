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
                 
                    default:
                    System.out.println("comando Incorrecto o desconocido, escribe /help para ver los comandos disponibles");
                        break;
                 }
                } else{
                    if (sesionIniciada){
                        try{
                            salida.writeUTF(username + ": " + mensaje);
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

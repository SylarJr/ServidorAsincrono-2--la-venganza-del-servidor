import java.io.IOException;

import ClienteMulti.SesionRegistro;

public class ManejadorComandos {
    private static final int Limite =3;

     public ManejadorComandos() {}

    public void procesarMensajeChat(UnCliente miCliente, String mensaje) throws IOException {
        if (mensaje.startsWith("/")) {
            ProcesarComando(miCliente, mensaje);
        } else {
            String msgFormateado = miCliente.username + ": " + mensaje;
            difundirMensaje(miCliente, msgFormateado);
        }
    }



    private void difundirMensaje(UnCliente remitente, String texto) throws IOException {
        for (UnCliente cliente : ServidorAsincrono.Cliente.values()) {
            if (cliente != remitente && cliente.estado != ClienteEstado.Estado.Jugando) {
                cliente.salida.writeUTF(texto);
            }
        }
    }


    public void ProcesarComando(UnCliente miCliente, String cmdCompleto)throws IOException{
        String [] partes = cmdCompleto.split(" ",2);
        String cmd = partes[0];
        String arg = (partes.length > 1) ? partes[1] : "";
        switch (cmd) {
            //comandos para el registro
            case "/entrar":
            String usuario = partes[1];
            String contrasena = partes[2];

            boolean exitoEntrar = SesionRegistro.iniciarSesion(usuario, contrasena);

            if (exitoEntrar){
                miCliente.username = usuario;
                miCliente.estado = ClienteEstado.Estado.Libre;
                miCliente.salida.writeUTF("Se a iniciado la secion como: "+ usuario + " Ya puedes hablar sin limite y puedes jugar"); 
            }else{
                miCliente.salida.writeUTF("Error al iniciar sesion, usuario o contraseña incorrectos");
            }

                break;

            case "/registrar":
            String nuevoUsuario = partes[1];
            String nuevaContrasena = partes[2];
            boolean exitoRegistro = SesionRegistro.registrarUsuario(nuevoUsuario, nuevaContrasena);
            if(exitoRegistro){
                miCliente.salida.writeUTF("Te has registrado con exito, ya puedes iniciar secion con /entrar");
            }else{
                miCliente.salida.writeUTF("Error al registrar el usuario. Inténtelo de nuevo.");
            }
                break;
            //comandos del juego
            case "/invitar":
                if (miCliente.estado != ClienteEstado.Estado.Libre) {
                    miCliente.salida.writeUTF("No puedes invitar a nadie si no estás 'LIBRE'.");
                    return;
                }
                String usernameInvitado = partes[1];
                UnCliente invitado = ServidorAsincrono.getClientePorUsername(usernameInvitado);

                if (invitado == null) {
                    miCliente.salida.writeUTF("Error: Usuario '" + usernameInvitado + "' no encontrado o no conectado.");
                } else if (invitado.estado != ClienteEstado.Estado.Libre) {
                    miCliente.salida.writeUTF("Error: El usuario '" + usernameInvitado + "' no está disponible (quizás ya está jugando).");
                } else if (invitado == miCliente) {
                    miCliente.salida.writeUTF("No puedes invitarte a ti mismo.");
                } else {
                   
                    miCliente.estado = ClienteEstado.Estado.Invitado;
                    invitado.estado = ClienteEstado.Estado.Invitado;
                    
                    miCliente.oponente = invitado;
                    invitado.oponente = miCliente;
                    
                    miCliente.salida.writeUTF("Invitación enviada a " + usernameInvitado + ". Esperando respuesta...");
                    invitado.salida.writeUTF("¡" + miCliente.username + " te ha invitado a jugar 3 en Raya");
                    invitado.salida.writeUTF("Escribe /aceptar o /rechazar.");
                }
                break;
            case "/aceptar":
                if (miCliente.estado != ClienteEstado.Estado.Invitado) {
                    miCliente.salida.writeUTF("No tienes ninguna invitación pendiente.");
                    return;
                }
                UnCliente invitador = miCliente.oponente;
                
              
                miCliente.estado = ClienteEstado.Estado.Jugando;
                invitador.estado = ClienteEstado.Estado.Jugando;
                
                JuegoGato nuevoJuego = new JuegoGato(invitador, miCliente); 
                
               
                ServidorAsincrono.juegosActivos.put(miCliente.idCliente, nuevoJuego);
                ServidorAsincrono.juegosActivos.put(invitador.idCliente, nuevoJuego);

                invitador.salida.writeUTF("¡" + miCliente.username + " aceptó tu invitación! Empieza el juego.");
                miCliente.salida.writeUTF("Aceptaste la invitación. Empieza el juego.");

                invitador.salida.writeUTF("Eres 'X'. ¡Es tu turno! Usa /jugar <fila> <columna>");
                miCliente.salida.writeUTF("Eres 'O'. Espera el turno de 'X'.");
                
                nuevoJuego.enviarTablero();
                break;
            case "/rechazar":
                if (miCliente.estado != ClienteEstado.Estado.Invitado) {
                    miCliente.salida.writeUTF("No tienes ninguna invitación pendiente.");
                    return;
                }
                UnCliente invitadorRechazado = miCliente.oponente;
                
                invitadorRechazado.salida.writeUTF("Tu invitación a " + miCliente.username + " fue rechazada.");
                miCliente.salida.writeUTF("Rechazaste la invitación.");
                
                
                invitadorRechazado.estado = ClienteEstado.Estado.Libre;
                miCliente.estado = ClienteEstado.Estado.Libre;
                invitadorRechazado.oponente = null;
                miCliente.oponente = null;
                break;
            default:
                miCliente.salida.writeUTF("Comando desconocido.");
        }

    }


    public void procesarMensajeJuego(UnCliente miCliente, String mensaje) throws IOException {
        JuegoGato juego = ServidorAsincrono.juegosActivos.get(miCliente.idCliente);
        if (juego == null) {
         
            miCliente.estado = ClienteEstado.Estado.Libre;
            miCliente.salida.writeUTF("Error: Se perdió la partida. Has vuelto al chat general.");
            return;
        }

        if (mensaje.startsWith("/jugar ")) {
            try {
                String[] partes = mensaje.split(" "); 
                int fila = Integer.parseInt(partes[1]);
                int col = Integer.parseInt(partes[2]);
                juego.hacerMovimiento(miCliente, fila, col);
            } catch (Exception e) {
                miCliente.salida.writeUTF("Comando inválido. Usa: /jugar <fila> <columna>");
            }
        } else if (mensaje.startsWith("/")) {
            miCliente.salida.writeUTF("Comando no disponible. Usa /jugar <fila> <col>.");
        } else {
            
            juego.enviarMensaje(miCliente.username, mensaje);
        } 
    }
    public void procesarDesconexion(UnCliente miCliente) {
        System.out.println("Cliente " + miCliente.idCliente + " (" + miCliente.username + ") desconectado.");
        
        
        if (miCliente.oponente != null) {
            try {
                UnCliente oponente = miCliente.oponente;
                oponente.salida.writeUTF("Tu oponente (" + miCliente.username + ") se ha desconectado.");
                
                
                if (miCliente.estado == ClienteEstado.Estado.Jugando) {
                    JuegoGato juego = ServidorAsincrono.juegosActivos.get(miCliente.idCliente);
                    if (juego != null) {
                        juego.terminarJuegoPorDesconexion(oponente); 
                    }
                }
                
                oponente.estado = ClienteEstado.Estado.Libre;
                oponente.oponente = null;
                
            } catch (IOException e) {
                
            }
        }
        
        
        ServidorAsincrono.Cliente.remove(miCliente.idCliente);
        ServidorAsincrono.juegosActivos.remove(miCliente.idCliente);
    }


    public void procesarConectado(UnCliente miCliente, String mensaje) throws IOException {
        if (mensaje.startsWith("/")) {
            String[] partes = mensaje.split(" ", 3);
            String cmd = partes[0];

            switch (cmd) {
                case "/entrar":
                    if (partes.length < 3) {
                        miCliente.salida.writeUTF("Uso: /entrar <usuario> <contraseña>");
                        return;
                    }
                    String usuario = partes[1];
                    String contrasena = partes[2];
                    boolean exitoEntrar = SesionRegistro.iniciarSesion(usuario, contrasena);

                    if (exitoEntrar) {
                        miCliente.username = usuario;
                        miCliente.estado = ClienteEstado.Estado.Libre;
                        miCliente.salida.writeUTF("¡Bienvenido " + usuario + "! Ahora tienes chat ilimitado.");
                    } else {
                        miCliente.salida.writeUTF("Error: Usuario o contraseña incorrectos.");
                    }
                    break;

                case "/registrar":
                    if (partes.length < 3) {
                        miCliente.salida.writeUTF("Uso: /registrar <usuario> <contraseña>");
                        return;
                    }
                    String nuevoUsuario = partes[1];
                    String nuevaContrasena = partes[2];
                    boolean exitoRegistro = SesionRegistro.registrarUsuario(nuevoUsuario, nuevaContrasena);
                    if (exitoRegistro) {
                        miCliente.salida.writeUTF("Registro exitoso. Usa /entrar para ingresar.");
                    } else {
                        miCliente.salida.writeUTF("Error: El usuario ya existe.");
                    }
                    break;

                default:
                    miCliente.salida.writeUTF("Comando no permitido. Usa /entrar o /registrar.");
                    break;
            }
        } 
        
        else {
            if(miCliente.mensajesEnviados < Limite){
                miCliente.mensajesEnviados++;
                int restantes = Limite - miCliente.mensajesEnviados;

                String msgInvitado = "Invitado(" + miCliente.idCliente + "): " +mensaje;
                difundirMensaje(miCliente, msgInvitado);

                if(restantes > 0){
                    miCliente.salida.writeUTF("Te quedan: " + restantes + " mensajes");
                }else{
                    miCliente.salida.writeUTF("ya no tienes mensajes gratis, crea una cuenta con /registrar o  / entrar");
                }
            }else{
                miCliente.salida.writeUTF("Has alcanzado el límite de mensajes gratuitos. Usa /registrar o /entrar para más.");
            }
        }
    }
    
        
    }






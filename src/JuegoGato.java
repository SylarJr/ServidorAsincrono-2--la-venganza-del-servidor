import java.io.IOException;

public class JuegoGato {
    private UnCliente jugadorX;
    private UnCliente jugadorO;
    private UnCliente turnoActual;
    private char[][] tablero;

    public JuegoGato(UnCliente jugador1, UnCliente jugador2) {
        this.jugadorX = jugador1;
        this.jugadorO = jugador2;
        this.turnoActual = jugadorX;
        this.tablero = new char[3][3];
        inicializarTablero();
    }

    private void inicializarTablero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = ' '; 
            }
        }
    }

   
    public void enviarMensaje(String remitente, String msg) throws IOException {
        String mensajeCompleto = "[JUEGO] " + remitente + ": " + msg;
        
       
        enviarACliente(jugadorX, mensajeCompleto);
        enviarACliente(jugadorO, mensajeCompleto);
    }

    public void enviarTablero() {
        System.out.println("Intentando enviar tablero a los jugadores..."); // Log servidor
        
        String boardStr = "\n--- TABLERO ---\n" +
                          "  0 1 2\n" +
                          "0 " + tablero[0][0] + "|" + tablero[0][1] + "|" + tablero[0][2] + "\n" +
                          "  -----\n" +
                          "1 " + tablero[1][0] + "|" + tablero[1][1] + "|" + tablero[1][2] + "\n" +
                          "  -----\n" +
                          "2 " + tablero[2][0] + "|" + tablero[2][1] + "|" + tablero[2][2] + "\n";
        
        try {
            // Enviar a X
            jugadorX.salida.writeUTF(boardStr);
            jugadorX.salida.flush(); 
            
            // Enviar a O
            jugadorO.salida.writeUTF(boardStr);
            jugadorO.salida.flush(); 
            
            System.out.println("Tablero enviado correctamente.");
        } catch (IOException e) {
            System.out.println("Error enviando tablero: " + e.getMessage());
        }
    }
    
    
    private void enviarACliente(UnCliente c, String msg) {
        try {
            c.salida.writeUTF(msg);
            c.salida.flush(); // <--- IMPORTANTE
        } catch (IOException e) {
            System.out.println("Error enviando mensaje a " + c.username);
        }
    }

    public synchronized void hacerMovimiento(UnCliente jugador, int fila, int col) throws IOException {
        if (jugador != turnoActual) {
            enviarACliente(jugador, "[JUEGO] No es tu turno.");
            return;
        }
        if (fila < 0 || fila > 2 || col < 0 || col > 2 || tablero[fila][col] != ' ') {
            enviarACliente(jugador, "[JUEGO] Movimiento inválido. Intenta de nuevo (ej: /jugar 1 2)");
            return;
        }

        char simbolo = (jugador == jugadorX) ? 'X' : 'O';
        tablero[fila][col] = simbolo;

    
        enviarTablero(); 

        
        if (verificarGanador(simbolo)) {
            enviarMensaje("SISTEMA", "¡El jugador " + jugador.username + " (" + simbolo + ") ha ganado!");
            terminarJuego();
        } else if (verificarEmpate()) {
            enviarMensaje("SISTEMA", "¡Es un empate!");
            terminarJuego();
        } else {
            // Cambiar turno
            turnoActual = (turnoActual == jugadorX) ? jugadorO : jugadorX;
            enviarACliente(turnoActual, "[JUEGO] Es tu turno.");
        }
    }

    private boolean verificarGanador(char s) {
        for (int i = 0; i < 3; i++) {
            if ((tablero[i][0] == s && tablero[i][1] == s && tablero[i][2] == s) ||
                (tablero[0][i] == s && tablero[1][i] == s && tablero[2][i] == s)) {
                return true;
            }
        }
        if ((tablero[0][0] == s && tablero[1][1] == s && tablero[2][2] == s) ||
            (tablero[0][2] == s && tablero[1][1] == s && tablero[2][0] == s)) {
            return true;
        }
        return false;
    }

    private boolean verificarEmpate() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == ' ') {
                    return false; 
                }
            }
        }
        return true; 
    }

    private void terminarJuego() {
        ServidorAsincrono.juegosActivos.remove(jugadorX.idCliente);
        ServidorAsincrono.juegosActivos.remove(jugadorO.idCliente);
        
        jugadorX.estado = ClienteEstado.Estado.Libre;
        jugadorO.estado = ClienteEstado.Estado.Libre;
        jugadorX.oponente = null;
        jugadorO.oponente = null;
    }

    public void terminarJuegoPorDesconexion(UnCliente jugadorQueQueda) {
        enviarACliente(jugadorQueQueda, "[JUEGO] Tu oponente se desconectó. ¡Ganaste por abandono!");
        
        ServidorAsincrono.juegosActivos.remove(jugadorX.idCliente);
        ServidorAsincrono.juegosActivos.remove(jugadorO.idCliente);
        
        jugadorQueQueda.estado = ClienteEstado.Estado.Libre;
        jugadorQueQueda.oponente = null;
    }
}


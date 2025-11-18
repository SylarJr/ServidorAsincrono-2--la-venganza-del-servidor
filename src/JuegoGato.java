import java.io.IOException;

public class JuegoGato {
    private UnCliente jugadorX;
    private UnCliente jugadorO;
    private UnCliente turnoActual;
    private char[][] tablero;

    public JuegoGato(UnCliente jugador1, UnCliente jugador2) {
        this.jugadorX = jugador1;
        this.jugadorO = jugador2;
        this.turnoActual = jugadorX; // X siempre empieza
        this.tablero = new char[3][3];
        inicializarTablero();
    }

    private void inicializarTablero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = ' '; // Casilla vacía
            }
        }
    }

    // Método para que los jugadores envíen mensajes
    public void enviarMensaje(String remitente, String msg) throws IOException {
        String mensajeCompleto = "[JUEGO] " + remitente + ": " + msg;
        jugadorX.salida.writeUTF(mensajeCompleto);
        jugadorO.salida.writeUTF(mensajeCompleto);
    }

    // Método para enviar el estado del tablero a ambos
    public void enviarTablero() throws IOException {
        String boardStr = "\n--- TABLERO ---\n" +
                          "  0 1 2\n" +
                          "0 " + tablero[0][0] + "|" + tablero[0][1] + "|" + tablero[0][2] + "\n" +
                          "  -----\n" +
                          "1 " + tablero[1][0] + "|" + tablero[1][1] + "|" + tablero[1][2] + "\n" +
                          "  -----\n" +
                          "2 " + tablero[2][0] + "|" + tablero[2][1] + "|" + tablero[2][2] + "\n";
        
        jugadorX.salida.writeUTF(boardStr);
        jugadorO.salida.writeUTF(boardStr);
    }
    
    // Método principal para procesar un movimiento
    public synchronized void hacerMovimiento(UnCliente jugador, int fila, int col) throws IOException {
        if (jugador != turnoActual) {
            jugador.salida.writeUTF("[JUEGO] No es tu turno.");
            return;
        }
        if (fila < 0 || fila > 2 || col < 0 || col > 2 || tablero[fila][col] != ' ') {
            jugador.salida.writeUTF("[JUEGO] Movimiento inválido. Intenta de nuevo (ej: /jugar 1 2)");
            return;
        }

        char simbolo = (jugador == jugadorX) ? 'X' : 'O';
        tablero[fila][col] = simbolo;

        enviarTablero(); // Muestra el tablero actualizado

        if (verificarGanador(simbolo)) {
            enviarMensaje("SISTEMA", "¡El jugador " + jugador.username + " (" + simbolo + ") ha ganado!");
            terminarJuego();
        } else if (verificarEmpate()) {
            enviarMensaje("SISTEMA", "¡Es un empate!");
            terminarJuego();
        } else {
            // Cambiar turno
            turnoActual = (turnoActual == jugadorX) ? jugadorO : jugadorX;
            turnoActual.salida.writeUTF("[JUEGO] Es tu turno.");
        }
    }

    private boolean verificarGanador(char s) {
        // Lógica para verificar filas, columnas y diagonales
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
                    return false; // Todavía hay espacio
                }
            }
        }
        return true; // Tablero lleno
    }

    private void terminarJuego() {
        // Quitar el juego del mapa y liberar a los jugadores
        ServidorAsincrono.juegosActivos.remove(jugadorX.idCliente);
        ServidorAsincrono.juegosActivos.remove(jugadorO.idCliente);
        
        jugadorX.estado = ClienteEstado.Estado.Libre;
        jugadorO.estado = ClienteEstado.Estado.Libre;
        jugadorX.oponente = null;
        jugadorO.oponente = null;
    }
    public void terminarJuegoPorDesconexion(UnCliente jugadorQueQueda) {
        try {
            jugadorQueQueda.salida.writeUTF("[JUEGO] Tu oponente se desconectó. ¡Ganaste por abandono!");
        } catch (IOException e) {
            // Si falla esto, el otro jugador también se desconectó, no podemos hacer nada
        }
        
        // Limpiar mapas y liberar al jugador que queda
        ServidorAsincrono.juegosActivos.remove(jugadorX.idCliente);
        ServidorAsincrono.juegosActivos.remove(jugadorO.idCliente);
        
        jugadorQueQueda.estado = ClienteEstado.Estado.Libre;
        jugadorQueQueda.oponente = null;
    }
}


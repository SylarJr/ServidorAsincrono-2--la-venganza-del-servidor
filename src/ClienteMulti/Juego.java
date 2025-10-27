package ClienteMulti;

public class Juego {
    private char[][] tablero;
    private char turnoActual;
    private boolean terminado;

    public Juego() {
        tablero = new char[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                tablero[i][j] = ' ';
        turnoActual = 'X';
        terminado = false;
    }

    public char getTurnoActual() {
        return turnoActual;
    }

    public boolean estaTerminado() {
        return terminado;
    }

    public boolean hacerJugada(int fila, int columna) {
        if (fila < 0 || fila > 2 || columna < 0 || columna > 2) return false;
        if (tablero[fila][columna] != ' ' || terminado) return false;
        tablero[fila][columna] = turnoActual;
        if (hayGanador()) {
            terminado = true;
        } else if (tableroLleno()) {
            terminado = true;
        } else {
            turnoActual = (turnoActual == 'X') ? 'O' : 'X';
        }
        return true;
    }

    public char ganador() {
        
        if (hayGanador()) return turnoActual;
        if (tableroLleno()) return 'E';
        return ' ';
    }

    private boolean hayGanador() {
        // Filas y columnas
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] != ' ' && tablero[i][0] == tablero[i][1] && tablero[i][1] == tablero[i][2])
                return true;
            if (tablero[0][i] != ' ' && tablero[0][i] == tablero[1][i] && tablero[1][i] == tablero[2][i])
                return true;
        }
        // Diagonales
        if (tablero[0][0] != ' ' && tablero[0][0] == tablero[1][1] && tablero[1][1] == tablero[2][2])
            return true;
        if (tablero[0][2] != ' ' && tablero[0][2] == tablero[1][1] && tablero[1][1] == tablero[2][0])
            return true;
        return false;
    }

    private boolean tableroLleno() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tablero[i][j] == ' ')
                    return false;
        return true;
    }

    public String mostrarTablero() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(" ");
            for (int j = 0; j < 3; j++) {
                sb.append(tablero[i][j]);
                if (j < 2) sb.append(" | ");
            }
            sb.append("\n");
            if (i < 2) sb.append("---+---+---\n");
        }
        return sb.toString();
    }
}

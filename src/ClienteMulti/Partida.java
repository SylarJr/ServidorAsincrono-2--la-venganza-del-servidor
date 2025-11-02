package ClienteMulti;

public class Partida {
    private String jugador1;
    private String jugador2;
    private Juego juego;
    private String turno;

    public Partida(String jugador1, String jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.juego = new Juego();
        this.turno = jugador1; // El jugador1 siempre empieza
    }

    public boolean esTurnoDe(String jugador) {
        return turno.equals(jugador);
    }
      public boolean hacerJugada(String usuario, int fila, int columna) {
        if (!esTurnoDe(usuario)) return false;
        boolean exito = juego.hacerJugada(fila, columna);
        if (exito && !juego.estaTerminado()) {
            turno = turno.equals(jugador1) ? jugador2 : jugador1;
        }
        return exito;
    }

    public String mostrarTablero (){
        return juego.mostrarTablero();
    }
    
    public boolean estaTerminada() {
        return juego.estaTerminado();
    }

    public char getGanador() {
        return juego.ganador();
    }
}

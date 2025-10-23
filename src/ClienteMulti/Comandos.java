package ClienteMulti;

public class Comandos {
    public void mostrarAyuda(){
                    System.out.println("""
                comandos Disponibles:
                /register -Usuario- -Contraseña- -Para reguistrar se
                /login -Usuario- -Contraseña- -Para iniciar sesion
                /help -Muestra los comandos disponibles
                Nota: si no tienes sesion activa solo puedes mandar 3 mensajes, Cuidado con eso
                """);
    }
}

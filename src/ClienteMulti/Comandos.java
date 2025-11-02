package ClienteMulti;

public class Comandos {
    public void mostrarAyuda(){
                    System.out.println("""
                comandos Disponibles:
                /register -Usuario- -Contraseña- -Para reguistrar se
                /login -Usuario- -Contraseña- -Para iniciar sesion
                /help -Muestra los comandos disponibles
                -- solo para usuarios registrados -- Animate A registrarte!
                /invitar -Invitar a otro usuario a jugar al gatito
                /aceptar -aceptar invitacion para jugar al gatito
                /rechazar -Rechazas la invitacion para jugar al gatito
                Nota: si no tienes sesion activa solo puedes mandar 3 mensajes, Cuidado con eso
                """);
    }
}

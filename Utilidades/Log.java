package Utilidades;

    
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
    
public class Log {
    public static PrintWriter log;
    private static final SimpleDateFormat FORMATOHORA = new SimpleDateFormat("HH:mm:ss");
    
    public Log () {
        inicializarLog();
    }
    
    public static void inicializarLog(){
        try {
                log = new PrintWriter(new File("C:\\Users\\belen\\Downloads\\tpAeropuertoFAI1641\\pruebaAerop\\src\\utiles\\logAeropuerto.txt"));
        } catch (IOException e) {
                System.err.println("ERROR " + e);
        }
    }
    
    public static synchronized void escribir(String texto) {
        String evento = tiempoActual() + " " + texto;
        System.out.println(evento);//muestra por pantalla
        log.println(evento);//escribe.
        log.flush(); //fuerza escritura de buffer.
    }

    // Devuelve un String con la Hora:Minutos:Segundos actuales.
    public static String tiempoActual() {
        Date fechaActual = new Date();
        return (String) FORMATOHORA.format(fechaActual);
    }
    
    

}

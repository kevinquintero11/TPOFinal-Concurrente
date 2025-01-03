package Aeropuerto.PuestoAtencion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Pasajero.Pasajero;
import Utilidades.Log;

public class Hall {

    // Mapa que asocia aerolíneas con sus respectivas BlockingQueues
    private final Map<String, LinkedList<Pasajero>> colasDeEspera;

    public Hall() {
        this.colasDeEspera = new HashMap<>();
    }

    // Método para que el pasajero espere en el hall
    public void esperarEnHall(Pasajero pasajero, PuestoAtencion puestoAtencion) {
        String nombreAerolinea = puestoAtencion.getAerolinea().getNombre();

        // Crear la cola si no existe
        colasDeEspera.putIfAbsent(nombreAerolinea, new LinkedList<>());
        synchronized (pasajero) { // Sincronizar sobre el pasajero
            // Agregar el pasajero a la cola correspondiente
            colasDeEspera.get(nombreAerolinea).add(pasajero);
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " está esperando en la cola del hall para " + nombreAerolinea + 
                         ". Cantidad de personas: " + colasDeEspera.get(nombreAerolinea).size());
            
            try {
                pasajero.wait(); // Bloquear el hilo del pasajero hasta que sea notificado
                Log.escribir("Se desbloquea el pasajero: " + pasajero.getIdPasajero() + " para el puesto: " + puestoAtencion.getAerolinea().getNombre());
            } catch (InterruptedException e) {
            }
        }
        
    }

    public LinkedList<Pasajero> getColaEspera(String nombreAerolinea){
        return this.colasDeEspera.get(nombreAerolinea);
    } 

    
}

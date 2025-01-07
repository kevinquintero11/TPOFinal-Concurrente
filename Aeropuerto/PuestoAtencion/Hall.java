package Aeropuerto.PuestoAtencion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Aeropuerto.Aerolinea.Aerolinea;
import Pasajero.Pasajero;
import Utilidades.Log;

public class Hall {

    // Mapa que asocia aerolíneas con sus respectivas BlockingQueues
    private final Map<String, BlockingQueue<Pasajero>> colasDeEspera;

    public Hall(List<Aerolinea> listaAerolineas) {
        this.colasDeEspera = new HashMap<>();

         for (Aerolinea aerolinea : listaAerolineas) {
        this.colasDeEspera.put(aerolinea.getNombre(), new LinkedBlockingQueue<>());
    }
    }

    // Método para que el pasajero espere en el hall
    public void esperarEnHall(Pasajero pasajero, PuestoAtencion puestoAtencion) throws InterruptedException {
        String nombreAerolinea = puestoAtencion.getAerolinea().getNombre();
    
        synchronized (pasajero) {
            // Agregar el pasajero a la cola correspondiente
            colasDeEspera.get(nombreAerolinea).put(pasajero);
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " está esperando en la cola del hall para " + nombreAerolinea + 
                         ". Cantidad de personas: " + colasDeEspera.get(nombreAerolinea).size());
            
            try {
                pasajero.wait(); // Bloquear el hilo del pasajero hasta que sea notificado
              //  Log.escribir("Se desbloquea el pasajero: " + pasajero.getIdPasajero() + " para el puesto: " + puestoAtencion.getAerolinea().getNombre());
            } catch (InterruptedException e) {
                //Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
            }
        }
    }
    

    public BlockingQueue<Pasajero> getColaEspera(String nombreAerolinea){
        return this.colasDeEspera.get(nombreAerolinea);
    } 

    
}

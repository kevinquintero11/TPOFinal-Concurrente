package Aeropuerto.PuestoAtencion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Aeropuerto.Aerolinea.Aerolinea;
import Pasajero.Pasajero;
import Utilidades.Log;

// CLASE QUE SIMULA EL HALL DEL AEROPUERTO DONDE ESPERAN LOS PASAJEROS PARA LOS VUELOS

public class Hall {

    // Mapa que asocia aerolíneas con sus respectivas BlockingQueues
    private final Map<String, BlockingQueue<Pasajero>> colasDeEspera;

    public Hall(List<Aerolinea> listaAerolineas) {
        this.colasDeEspera = new HashMap<>();

        // Crea una blockingQueue para cada aerolinea, que va a ser donde esperen los pasajeros que esten en el hall
        for (Aerolinea aerolinea : listaAerolineas) {
            this.colasDeEspera.put(aerolinea.getNombre(), new LinkedBlockingQueue<>());
        }
    }

    public void esperarEnHall(Pasajero pasajero, PuestoAtencion puestoAtencion) throws InterruptedException {
        String nombreAerolinea = puestoAtencion.getAerolinea().getNombre();
    
        // Toma el monitor del objeto pasajero
        synchronized (pasajero) {
            colasDeEspera.get(nombreAerolinea).put(pasajero); // Agrega el pasajero a la cola correspondiente
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " está esperando en la cola del hall para " + nombreAerolinea + ". Cantidad de personas: " + colasDeEspera.get(nombreAerolinea).size());
            try {
                pasajero.wait(); // Bloquea al pasajero hasta que sea notificado para que ingrese al puesto de atención
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public BlockingQueue<Pasajero> getColaEspera(String nombreAerolinea){
        return this.colasDeEspera.get(nombreAerolinea);
    } 

}

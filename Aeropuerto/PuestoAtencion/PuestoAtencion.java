package Aeropuerto.PuestoAtencion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.Terminal.PuestoEmbarque;
import Aeropuerto.Terminal.Terminal;
import Pasajero.Pasajero;
import Utilidades.Log;

public class PuestoAtencion {
    
    private Aerolinea aerolinea;
    private Hall hall;
    private int capacidadMax;
    private BlockingQueue<Pasajero> colaPasajeros; // Cola de espera para los pasajeros
   // private Semaphore darPasoPasajero = new Semaphore(0);
    private Semaphore capacidadDisponible;
    private int cantidadPasajerosEnPuesto; 
    private Semaphore mutex = new Semaphore(1);

    public PuestoAtencion(Aerolinea aero, Hall salaEspera, int capacidad){
        this.aerolinea = aero;
        this.hall = salaEspera;
        this.capacidadMax = capacidad;
        this.colaPasajeros = new LinkedBlockingQueue<>(capacidadMax);
        this.capacidadDisponible = new Semaphore(capacidadMax);
        this.cantidadPasajerosEnPuesto = 0;

    }

    public Aerolinea getAerolinea(){
        return this.aerolinea;
    }

    public Hall getHall(){
        return this.hall;
    }

    public List<Object> atenderPasajero(Pasajero pas) throws InterruptedException {
        List<Object> terminalYPuestoEmbarque = new ArrayList<>();
        Pasajero pasajero = colaPasajeros.take(); // Toma al siguiente pasajero

        Log.escribir("Atendiendo al pasajero " + pasajero.getIdPasajero() + " en el puesto " + this.getAerolinea().getNombre());
        //System.out.println("Atendiendo a " + pasajero.getReserva().getIdReserva());

        Terminal terminal = pasajero.getReserva().getTerminal();
        terminalYPuestoEmbarque.add(terminal);
        terminalYPuestoEmbarque.add(pasajero.getReserva().getTerminal().getIdTerminal());
        Thread.sleep(2000); // Simula el tiempo de atención
        // capacidadDisponible.release(); // Libera espacio en el puesto
        return terminalYPuestoEmbarque;
    }

    public List<Object> ingresarPuestoAtencion(Pasajero pasajero) throws InterruptedException {
        List<Object> terminalYPuestoEmbarque = new ArrayList<>();
        if (capacidadDisponible.tryAcquire() && cantidadPasajerosEnPuesto < this.capacidadMax) {
            mutex.acquire();
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " ingresó al puesto de atención: " + this.aerolinea.getNombre());
            cantidadPasajerosEnPuesto++;
            Log.escribir("Cantidad de de personas " + this.cantidadPasajerosEnPuesto + " en el puesto " + this.getAerolinea().getNombre());
            mutex.release();
            colaPasajeros.put(pasajero); // Ingresa directamente al puesto
            
            //System.out.println(pasajero.getReserva().getIdReserva() + " ingresó al puesto de atención.");
        } else {
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " tiene que esperar en el hall porque el puesto de atención ya está lleno.");
            hall.getColaEspera().put(pasajero); // Si está lleno, espera en el hall
            
        }
        terminalYPuestoEmbarque = atenderPasajero(pasajero);
        Terminal term = (Terminal) terminalYPuestoEmbarque.get(0);
        
        mutex.acquire();
        cantidadPasajerosEnPuesto--;
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + " salió del puesto de atencion y se dirige rumbo a la terminal " + term.getIdTerminal());
        Log.escribir("Cantidad de lugadres disponibles " + this.cantidadPasajerosEnPuesto + " en el puesto " + this.getAerolinea().getNombre());
        mutex.release();
        capacidadDisponible.release();
        return terminalYPuestoEmbarque;
    }

    public void permitirIngresoDesdeHall() throws InterruptedException {
        // El guardia permite que un pasajero del hall central ingrese al puesto
        Pasajero pasajero = hall.getColaEspera().take(); // Toma al siguiente pasajero en espera
        colaPasajeros.put(pasajero); // Lo coloca en el puesto
        Log.escribir("Guardia permitió el ingreso de " + pasajero.getIdPasajero() + " al puesto de atencion: " + this.aerolinea.getNombre());
        //System.out.println("Guardia permitió el ingreso de " + pasajero.getReserva().getIdReserva());
        capacidadDisponible.acquire(); // Disminuye el espacio disponible
    }

    // public void permitirIngresoDesdeHall() throws InterruptedException {
    //     if (!hall.getColaEspera().isEmpty() && capacidadDisponible.tryAcquire()) {
    //         Pasajero pasajero = hall.getColaEspera().take(); // Toma al siguiente pasajero en espera
    //         colaPasajeros.put(pasajero); // Lo coloca en el puesto
    //         Log.escribir("Guardia permitió el ingreso de " + pasajero.getIdPasajero() + " al puesto de atención.");
    //     }
    // }


}

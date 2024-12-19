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

public class PuestoAtencion {
    
    private Aerolinea aerolinea;
    private Hall hall;
    private int capacidadMax;
    private BlockingQueue<Pasajero> colaPasajeros; // Cola de espera para los pasajeros
    private Semaphore darPasoPasajero = new Semaphore(0);
    private Semaphore capacidadDisponible;

    public PuestoAtencion(Aerolinea aero, Hall salaEspera, int capacidad){
        this.aerolinea = aero;
        this.hall = salaEspera;
        this.capacidadMax = capacidad;
        this.colaPasajeros = new LinkedBlockingQueue<>(capacidadMax);
        this.capacidadDisponible = new Semaphore(capacidadMax);

    }

    public Aerolinea getAerolinea(){
        return this.aerolinea;
    }

    public Hall getHall(){
        return this.hall;
    }

   // Método para que un pasajero ingrese al puesto de atención
    // public List<Object> ingresarPuestoAtencion(Pasajero pasajero) {
    //     List<Object> lista = new ArrayList<>();
    //     try {
    //         System.out.println(pasajero.getReserva().getIdReserva() + " está intentando ingresar al puesto de atención.");
    //         capacidadDisponible.acquire(); // Espera si no hay espacio en el puesto
    //         colaPasajeros.put(pasajero); // Bloquea si la cola está llena
    //         System.out.println(pasajero.getReserva().getIdReserva() + " ha ingresado al puesto de atención.");

    //         lista = atenderPasajero(pasajero);
    //         // Simular el tiempo de atención
    //         // Thread.sleep(2000); // Simula 2 segundos de atención

    //         // Después de atender al pasajero, se lo remueve de la cola
    //         // Pasajero atendido = colaPasajeros.take();
    //         // System.out.println("Pasajero atendido: " + atendido.getReserva().getIdReserva());

    //     } catch (InterruptedException e) {
    //         Thread.currentThread().interrupt();
    //         System.out.println("El hilo fue interrumpido.");
    //     }
    //     return lista;
    // }

    // public void trabajoGuardia() throws InterruptedException{
    //     darPasoPasajero.acquire();
    //     colaPasajeros.take();
    // }

    // public List<Object> atenderPasajero(Pasajero pasajero) throws InterruptedException{
    //     colaPasajeros.take();
    //     List<Object> terminalYPuestoEmbarque = new ArrayList<>();
    //     Thread.sleep(2000);
    //     Terminal terminal = pasajero.getReserva().getTerminal();
    //     System.out.println(pasajero.getReserva().getIdReserva() + " vaya a la terminal " + terminal.getIdTerminal());
    //     terminalYPuestoEmbarque.add(terminal);
    //     darPasoPasajero.release();
    //     return terminalYPuestoEmbarque;
    // }

    public List<Object> atenderPasajero(Pasajero pas) throws InterruptedException {
        List<Object> terminalYPuestoEmbarque = new ArrayList<>();
        Pasajero pasajero = colaPasajeros.take(); // Toma al siguiente pasajero
        System.out.println("Atendiendo a " + pasajero.getReserva().getIdReserva());
        terminalYPuestoEmbarque.add(pasajero.getReserva().getTerminal());
        terminalYPuestoEmbarque.add(pasajero.getReserva().getTerminal().getIdTerminal());
        Thread.sleep(2000); // Simula el tiempo de atención
        capacidadDisponible.release(); // Libera espacio en el puesto
        return terminalYPuestoEmbarque;
    }

    public List<Object> ingresarPuestoAtencion(Pasajero pasajero) throws InterruptedException {
        List<Object> terminalYPuestoEmbarque = new ArrayList<>();
        if (capacidadDisponible.tryAcquire()) {
            colaPasajeros.put(pasajero); // Ingresa directamente al puesto
            System.out.println(pasajero.getReserva().getIdReserva() + " ingresó al puesto de atención.");
           
        } else {
            hall.getColaEspera().put(pasajero); // Si está lleno, espera en el hall
            
        }
        terminalYPuestoEmbarque = atenderPasajero(pasajero);
        return terminalYPuestoEmbarque;
    }

    public void permitirIngresoDesdeHall() throws InterruptedException {
        // El guardia permite que un pasajero del hall central ingrese al puesto
        Pasajero pasajero = hall.getColaEspera().take(); // Toma al siguiente pasajero en espera
        colaPasajeros.put(pasajero); // Lo coloca en el puesto
        System.out.println("Guardia permitió el ingreso de " + pasajero.getReserva().getIdReserva());
        capacidadDisponible.acquire(); // Disminuye el espacio disponible
    }



}

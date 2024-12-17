package Aeropuerto.PuestoAtencion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.Terminal.PuestoEmbarque;
import Aeropuerto.Terminal.Terminal;
import Pasajero.Pasajero;

public class PuestoAtencion {
    
    private Aerolinea aerolinea;
    private Hall hall;
    private int capacidadMax;
    private BlockingQueue<Pasajero> colaPasajeros; // Cola de espera para los pasajeros

    public PuestoAtencion(Aerolinea aero, Hall salaEspera, int capacidad){
        this.aerolinea = aero;
        this.hall = salaEspera;
        this.capacidadMax = capacidad;
         this.colaPasajeros = new LinkedBlockingQueue<>(capacidadMax);

    }

    public Aerolinea getAerolinea(){
        return this.aerolinea;
    }

    public Hall getHall(){
        return this.hall;
    }

   // Método para que un pasajero ingrese al puesto de atención
    public List<Object> ingresarPuestoAtencion(Pasajero pasajero) {
        List<Object> terminalYPuestoEmbarque = new ArrayList<>();
        try {
            System.out.println(pasajero.getReserva().getIdReserva() + " está intentando ingresar al puesto de atención.");
            colaPasajeros.put(pasajero); // Bloquea si la cola está llena
            System.out.println(pasajero.getReserva().getIdReserva() + " ha ingresado al puesto de atención.");

            
            // Simular el tiempo de atención
            Thread.sleep(2000); // Simula 2 segundos de atención

            Terminal terminal = pasajero.getReserva().getTerminal();
            System.out.println(pasajero.getReserva().getIdReserva() + " vaya a la terminal " + terminal.getIdTerminal());
            terminalYPuestoEmbarque.add(terminal);
            // Después de atender al pasajero, se lo remueve de la cola
            Pasajero atendido = colaPasajeros.take();
            System.out.println("Pasajero atendido: " + atendido.getReserva().getIdReserva());

            

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("El hilo fue interrumpido.");
        }
        return terminalYPuestoEmbarque;
    }
}

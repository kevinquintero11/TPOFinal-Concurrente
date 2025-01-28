package Aeropuerto.PuestoAtencion;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.Terminal.PuestoEmbarque;
import Aeropuerto.Terminal.Terminal;
import Pasajero.Pasajero;
import Utilidades.Log;

// CLASE QUE SIMULA EL PUESTO DE ATENCIÓN DE CADA AEROLINEA

public class PuestoAtencion implements Runnable {
    
    private final Aerolinea aerolinea;
    private final Hall hall;
    private final int capacidadMax;
    private final BlockingQueue<Pasajero> colaPasajeros;
    private final Semaphore mutex;
    private final Semaphore semaforoGuardia = new Semaphore(0);
    private  int cantidadPasajeroEnPuesto = 0;

    public PuestoAtencion(Aerolinea aerolinea, Hall hall, int capacidadMax) {
        this.aerolinea = aerolinea;
        this.hall = hall;
        this.capacidadMax = capacidadMax;
        this.colaPasajeros = new ArrayBlockingQueue<>(capacidadMax);
        this.mutex = new Semaphore(1);
    }

    public Aerolinea getAerolinea() {
        return aerolinea;
    }

    public void ingresarPuestoAtencion(Pasajero pasajero) throws InterruptedException {
        mutex.acquire();
        if (this.cantidadPasajeroEnPuesto == this.capacidadMax) {
            this.mutex.release();
            this.hall.esperarEnHall(pasajero, this);
            this.mutex.acquire();
        }else{
            this.colaPasajeros.put(pasajero);
            this.cantidadPasajeroEnPuesto++;
            Log.escribir("> Pasajero " + pasajero.getIdPasajero() + " ingresó al puesto de atención de: " + aerolinea.getNombre() + " en la posicion: " + cantidadPasajeroEnPuesto);
        }

        this.mutex.release();
    }

    // Método ejecutado por los hilos puestoAtencion
    public void atenderPasajero() throws InterruptedException {
        Pasajero pasajero = colaPasajeros.take(); // Toma al primer pasajero ubicado en la cola de espera
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + " está siendo atendido");
        
        // Simula el tiempo de atención
        Thread.sleep(1000); 

        synchronized (pasajero) {
            pasajero.notify(); // Notifica al pasajero que ha sido atendido
        }
    
        this.cantidadPasajeroEnPuesto--;
        this.semaforoGuardia.release(); // Libera el semáforo del guardia para avisar que se liberó un espacio en la fila
    }

    // Método ejecutado por los hilos pasajero
    public List<Object> esperarAtencion(Pasajero pasajero) throws InterruptedException{
        List<Object> terminalYPuertoEmbarque = new LinkedList<>();
        synchronized (pasajero) {
            Terminal terminal = pasajero.getReserva().getTerminal();
            PuestoEmbarque puesto = pasajero.getReserva().getTerminal().getPuestoEmbarqueGeneral(); 
            terminalYPuertoEmbarque.add(terminal);
            terminalYPuertoEmbarque.add(puesto);
            pasajero.wait(); // Espera hasta ser notificado de que fue atendido
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " fue atendido exitosamente en: " + this.aerolinea.getNombre());
        }
        
        return terminalYPuertoEmbarque;
    }
    
    // Método ejecutado por los hilos guardia
    public void permitirIngresoDesdeHall() throws InterruptedException {
        this.semaforoGuardia.acquire(); // Espera a que se le avise al guardia para permitir el ingreso
        if(cantidadPasajeroEnPuesto == capacidadMax-1) {   
            BlockingQueue<Pasajero> cola = hall.getColaEspera(aerolinea.getNombre());
            if (!cola.isEmpty()) {
                Pasajero pasajero = cola.remove(); // Quita al pasjero de la cola de espera del hall
                colaPasajeros.put(pasajero); // Agrega al pasajero a la cola del puesto de atención
                cantidadPasajeroEnPuesto++;
                Log.escribir("Guardia permitió el ingreso de pasajero " + pasajero.getIdPasajero() + " al puesto de atención de " + this.aerolinea.getNombre() + ". Pasajeros restantes esperando: " + cola.size());
                Log.escribir("> Pasajero " + pasajero.getIdPasajero() + " ingresó al puesto de atención de: " + aerolinea.getNombre() + " en la posicion: " + cantidadPasajeroEnPuesto);
                
                synchronized (pasajero) {
                    pasajero.notify(); // Notifica al pasajero que está ingresando al puesto
                }
            }
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                this.atenderPasajero();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

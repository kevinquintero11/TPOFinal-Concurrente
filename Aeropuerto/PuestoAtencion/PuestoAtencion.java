package Aeropuerto.PuestoAtencion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.Terminal.PuestoEmbarque;
import Aeropuerto.Terminal.Terminal;
import Pasajero.Pasajero;
import Utilidades.Log;

public class PuestoAtencion implements Runnable {
    
    private final Aerolinea aerolinea;
    private final Hall hall;
    private final int capacidadMax;
    private final List<Pasajero> colaPasajeros;
    private final Semaphore mutex;
    private final Semaphore espacioDisponible;
    private final Semaphore atender;
    private final Semaphore semaforoGuardia = new Semaphore(0);
    private  int cantidadPasajeroEnPuesto = 0;

    public PuestoAtencion(Aerolinea aerolinea, Hall hall, int capacidadMax) {
        this.aerolinea = aerolinea;
        this.hall = hall;
        this.capacidadMax = capacidadMax;
        this.colaPasajeros = new LinkedList<>();
        this.mutex = new Semaphore(1);
        this.espacioDisponible = new Semaphore(capacidadMax);
        this.atender = new Semaphore(0);
        
    }

    public Aerolinea getAerolinea() {
        return aerolinea;
    }

    public void ingresarPuestoAtencion(Pasajero pasajero) throws InterruptedException {
        mutex.acquire();
        if (cantidadPasajeroEnPuesto >= capacidadMax) {
            Log.escribir("\u23F3 Pasajero " + pasajero.getIdPasajero() + " debe esperar en el hall porque el puesto de atención está lleno.");
            this.mutex.release();
            this.hall.esperarEnHall(pasajero, this);
            this.mutex.acquire();
        }else{
            this.colaPasajeros.add(pasajero);
            this.cantidadPasajeroEnPuesto++;
            Log.escribir("> Pasajero " + pasajero.getIdPasajero() + " ingresó al puesto de atención de: " + aerolinea.getNombre() + " en la posicion: " + cantidadPasajeroEnPuesto);
        }

            this.mutex.release();
            this.atender.release();
       
    }

    public void atenderPasajero() throws InterruptedException {
        this.atender.acquire(); // Esperar hasta que haya un pasajero para atender
        //this.mutex.acquire(); // Adquirir el mutex para sincronizar el acceso a la lista de pasajeros
        Pasajero pasajero = colaPasajeros.remove(0); // Extraer al primer pasajero
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + " está siendo atendido");
        // Simulamos el tiempo de atención
        Thread.sleep(2000); 
        //this.mutex.release();
    
        // Ahora notificamos al pasajero que se ha completado la atención
        synchronized (pasajero) {
            pasajero.notify(); // Notificar al pasajero que ha sido atendido
        }
    
        // Actualizamos el número de pasajeros en el puesto
        //this.mutex.acquire();
        this.cantidadPasajeroEnPuesto--;
        //this.mutex.release();
    
        // Permitir que el guardia libere el puesto para otros pasajeros
        this.semaforoGuardia.release(); // Liberar semáforo guardia solo cuando el proceso de atención se complete
    }

    public List<Object> esperarAtencion(Pasajero pasajero) throws InterruptedException{
        List<Object> terminalYPuertoEmbarque = new LinkedList<>();
        synchronized (pasajero) {
            pasajero.wait(); // Esperar hasta ser notificado
            Log.escribir("\u2705 Pasajero " + pasajero.getIdPasajero() + " fue atendido exitosamente en: " + this.aerolinea.getNombre());
        }
        Terminal terminal = pasajero.getReserva().getTerminal();
        PuestoEmbarque puesto = pasajero.getReserva().getTerminal().getPuestoEmbarqueGeneral(); 
        terminalYPuertoEmbarque.add(terminal);
        terminalYPuertoEmbarque.add(puesto);
        return terminalYPuertoEmbarque;
    }
    
    public void permitirIngresoDesdeHall() throws InterruptedException {
        this.semaforoGuardia.acquire(); // Espera hasta que el guardia pueda permitir el ingreso
        try {
            LinkedList<Pasajero> cola = hall.getColaEspera(aerolinea.getNombre());
            if (cola != null && !cola.isEmpty()) {
                Pasajero pasajero = cola.remove(0); // Extraer el primer pasajero de la cola
                colaPasajeros.add(pasajero); // Agregar al puesto de atención
                cantidadPasajeroEnPuesto++;
                Log.escribir("\uD83D\uDC6E Guardia permitió el ingreso de pasajero " + pasajero.getIdPasajero() + " al puesto de atención de " + this.aerolinea.getNombre() + ". Pasajeros restantes esperando: " + cola.size());
                Log.escribir("> Pasajero " + pasajero.getIdPasajero() + " ingresó al puesto de atención de: " + aerolinea.getNombre() + " en la posicion: " + cantidadPasajeroEnPuesto);
               
                synchronized (pasajero) {
                    //Log.escribir("Se notifica que se libero espacio en: " + this.aerolinea.getNombre());
                    pasajero.notify(); // Notificar al pasajero que está ingresando al puesto
                }
            } else {
                //Log.escribir("No hay pasajeros en la cola del hall para " + aerolinea.getNombre());
            }
        } finally {
            // No se debe liberar semáforo hasta que el proceso de atención termine
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(true){
            try {
                this.atenderPasajero();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

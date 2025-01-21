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
        if (cantidadPasajeroEnPuesto == capacidadMax) {
            this.mutex.release();
            this.hall.esperarEnHall(pasajero, this);
            this.mutex.acquire();
        }else{
            this.colaPasajeros.put(pasajero);
            this.cantidadPasajeroEnPuesto++;
            Log.escribir("> Pasajero " + pasajero.getIdPasajero() + " ingresó al puesto de atención de: " + aerolinea.getNombre() + " en la posicion: " + cantidadPasajeroEnPuesto);
        }

            this.mutex.release();
            //this.atender.release();
       
    }

    public void atenderPasajero() throws InterruptedException {
       
        Pasajero pasajero = colaPasajeros.take(); // Extraer al primer pasajero
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + " está siendo atendido");
        // Simulamos el tiempo de atención
        Thread.sleep(2000); 
        //this.mutex.release();
    
        // Ahora notificamos al pasajero que se ha completado la atención
        synchronized (pasajero) {
            pasajero.notify(); // Notificar al pasajero que ha sido atendido
        }
    
        // Actualizamos el número de pasajeros en el puesto
       
        this.cantidadPasajeroEnPuesto--;
      
    
        // Permitir que el guardia libere el puesto para otros pasajeros
        this.semaforoGuardia.release(); // Liberar semáforo guardia solo cuando el proceso de atención se complete
    }

    public List<Object> esperarAtencion(Pasajero pasajero) throws InterruptedException{
        List<Object> terminalYPuertoEmbarque = new LinkedList<>();
        synchronized (pasajero) {
            Terminal terminal = pasajero.getReserva().getTerminal();
            PuestoEmbarque puesto = pasajero.getReserva().getTerminal().getPuestoEmbarqueGeneral(); 
            terminalYPuertoEmbarque.add(terminal);
            terminalYPuertoEmbarque.add(puesto);
            pasajero.wait(); // Esperar hasta ser notificado
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " fue atendido exitosamente en: " + this.aerolinea.getNombre());
        }
        
        return terminalYPuertoEmbarque;
    }
    
    public void permitirIngresoDesdeHall() throws InterruptedException {
        this.semaforoGuardia.acquire(); // Espera hasta que el guardia pueda permitir el ingreso
        //Log.escribir("Pasajeros en puesto: " + cantidadPasajeroEnPuesto + " colaPasajeros: " + colaPasajeros.size());
        if(cantidadPasajeroEnPuesto == capacidadMax-1) {   
            BlockingQueue<Pasajero> cola = hall.getColaEspera(aerolinea.getNombre());
            if (cola != null && !cola.isEmpty()) {
                Pasajero pasajero = cola.remove(); // Remueve solo si hay elementos
                colaPasajeros.put(pasajero); // Agregar al puesto de atención
                cantidadPasajeroEnPuesto++;
                Log.escribir("Guardia permitió el ingreso de pasajero " + pasajero.getIdPasajero() + " al puesto de atención de " + this.aerolinea.getNombre() + ". Pasajeros restantes esperando: " + cola.size());
                Log.escribir("> Pasajero " + pasajero.getIdPasajero() + " ingresó al puesto de atención de: " + aerolinea.getNombre() + " en la posicion: " + cantidadPasajeroEnPuesto);
                synchronized (pasajero) {
                    pasajero.notify(); // Notificar al pasajero que está ingresando al puesto
                }
            }
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

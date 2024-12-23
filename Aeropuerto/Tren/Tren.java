package Aeropuerto.Tren;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Aeropuerto.Terminal.Terminal;
import Pasajero.Pasajero;
import Utilidades.Log;

public class Tren implements Runnable{
    private int capacidadTren;
    private int pasajerosABordo;
    private List<Terminal> listaTerminales;
    private Semaphore espaciosDisponibles;
    private Semaphore iniciarViaje;
    private Semaphore mutex;
    private CyclicBarrier barreraSubida;
    private boolean detenidoEnTerminal = false;
    private boolean ultimaTerminal = false;
    private Terminal terminalActual;
    private ReentrantLock cerrojo = new ReentrantLock();
    private Condition esperandoDetencion = cerrojo.newCondition();
    private Condition esperandoBajadaPasajeros = cerrojo.newCondition();

    public Tren(int capacidad, List<Terminal> terminales){
        this.capacidadTren = capacidad;
        this.listaTerminales = terminales;
        this.espaciosDisponibles = new Semaphore(capacidadTren);
        this.pasajerosABordo = 0;
        this.iniciarViaje = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.barreraSubida = new CyclicBarrier(capacidad);
        this.terminalActual = null;
    }
    
    public void subir(Pasajero pasajero) throws InterruptedException {
        // espaciosDisponibles.acquire(); // Esperar espacio disponible
        // //mutex.acquire(); // Exclusión mutua para abordar
        // cerrojo.lock();
        // pasajerosABordo++;
        // System.out.println(Thread.currentThread().getName() + " subió al tren. Pasajeros a bordo: " + pasajerosABordo);
        
        // try {
        //     // Espera un tiempo antes de iniciar si no todos los pasajeros han subido
        //     barreraSubida.await(5, TimeUnit.SECONDS);
        //     iniciarViaje.release();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        // cerrojo.unlock();

        cerrojo.lock();
        try {
            while (pasajerosABordo >= capacidadTren) {
                esperandoDetencion.await();
            }
            pasajerosABordo++;
            Log.escribir("Pasajero " + pasajero.getReserva().getIdReserva() + ": subio al tren. Pasajeros a bordo: " + pasajerosABordo);
            System.out.println(Thread.currentThread().getName() + " subió al tren. Pasajeros a bordo: " + pasajerosABordo);

            // Si el tren está lleno, notifica que puede iniciar el viaje
            if (pasajerosABordo == capacidadTren) {
                iniciarViaje.release();
            }
        } finally {
            cerrojo.unlock();
        }
    }

    public void bajar(Terminal terminal, Pasajero pasajero) throws InterruptedException{
        cerrojo.lock();
        try {
            // Espera hasta que el tren se detenga en una terminal
            while (!detenidoEnTerminal || terminalActual != terminal) {
                esperandoDetencion.await();
            }
    
            // Una vez detenido en la terminal correcta, el pasajero baja
            //espaciosDisponibles.release();
            pasajerosABordo--;
            if(pasajerosABordo == 0){
                esperandoBajadaPasajeros.signal();
            }
            Log.escribir("Pasajero " + pasajero.getReserva().getIdReserva() + ": bajo en la terminal " + terminal.getIdTerminal());
            System.out.println(Thread.currentThread().getName() + " bajó en la terminal: " + terminal.getIdTerminal());
        } finally {
            // Asegura la liberación del lock
            cerrojo.unlock();
        }
    }

    // El tren verifica y detiene en cada terminal según las solicitudes
    private void detenerEnTerminal(Terminal terminal) throws InterruptedException {
        System.out.println("El tren se detiene en la terminal: " + terminal.getIdTerminal());
        Log.escribir("El tren se detiene en la terminal: " + terminal.getIdTerminal());
        this.terminalActual = terminal;
        this.detenidoEnTerminal = true;
        this.esperandoDetencion.signalAll();
        
        Thread.sleep(2000); // Simulamos el tiempo de detención en la terminal
        if(ultimaTerminal){
            while (pasajerosABordo > 0) {
                esperandoBajadaPasajeros.await(); // Espera a que todos los pasajeros bajen
            }
            //espaciosDisponibles.drainPermits();
        }
        this.detenidoEnTerminal = false;
    }
        
    // Inicia el viaje
    private void iniciarViajeTren() throws InterruptedException {
        this.iniciarViaje.acquire();
        System.out.println("El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
        Log.escribir("El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
        int cantidadTerminales = listaTerminales.size();

        for (int i = 0; i < cantidadTerminales; i++) {
            Terminal terminal = listaTerminales.get(i);
            try {
                if (i == cantidadTerminales - 1) { // Última terminal
                    this.ultimaTerminal = true;
                }
                detenerEnTerminal(terminal); 
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
       
        
    }

    private void regresarInicio() throws InterruptedException{
        System.out.println("El tren regresa vacío al inicio del recorrido.");
        Log.escribir("El tren regresa vacío al inicio del recorrido.");
        this.ultimaTerminal = false;
        this.terminalActual = null; // Indica que el tren está fuera de las terminales
        Thread.sleep(2000); // Simula el tiempo de regreso
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.iniciarViajeTren();
                this.regresarInicio();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // public void subir() throws InterruptedException{
        //     espaciosDisponibles.acquire();
        //     mutex.acquire();
        //     this.pasajerosABordo++;
            
        //      //si el pasajero es el ultimo en ingresar, avisa al tren.
        //      if (this.pasajerosABordo == this.capacidadTren) {
        //         this.iniciarViaje.release();
        //     }
    
        //     mutex.release();
        // }
        
    
        // public void bajar() throws InterruptedException{
        //     mutex.acquire();
        //     this.pasajerosABordo--;
        //     mutex.release();
    
        // }
    
        // public void iniciarViajeTren() throws InterruptedException{
        //     this.iniciarViaje.acquire();
        //     mutex.acquire();
        //     int permisos = this.capacidadTren - this.pasajerosABordo;
        //     mutex.release();
        //     espaciosDisponibles.acquire(permisos);
        //     int cantidadTerminales = listaTerminales.size();
        //     for(int i = 0; i < cantidadTerminales; i++){
    
        //     }
    
    
        // }
    
        // @Override
        // public void run() {
        //     while (true) {
        //         try {
        //             this.iniciarViajeTren();
        //             //esto es para que espere un tiempo despues de estacionar en una terminal
        //             while (!this.pararTerminal()) {
        //                 Thread.sleep(4000); //espero un corto periodo de tiempo antes de volver a llamar al metodo.
        //             }
                    
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }

        // Pasajero baja del tren
        // public void bajar(Terminal terminal) throws InterruptedException {
        //     mutex.acquire();

        //     if(detenidoEnTerminal && terminalActual == terminal){
        //         espaciosDisponibles.release(); // Liberar espacio en el tren
        //         pasajerosABordo--;
        //     }
        //     mutex.release();
        // }
}

package Aeropuerto.Tren;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import Aeropuerto.Terminal.Terminal;

public class Tren implements Runnable{
    private int capacidadTren;
    private int pasajerosABordo;
    private List<Terminal> listaTerminales;
    private Semaphore espaciosDisponibles;
    private Semaphore iniciarViaje;
    private Semaphore mutex;
    private CyclicBarrier barreraSubida;
    private boolean detenidoEnTerminal = false;
    private Terminal terminalActual;
    
    
        public Tren(int capacidad, List<Terminal> terminales){
            this.capacidadTren = capacidad;
            this.listaTerminales = terminales;
            this.espaciosDisponibles = new Semaphore(capacidadTren);
            this.pasajerosABordo = 0;
            this.iniciarViaje = new Semaphore(0);
            this.mutex = new Semaphore(1);
            this.barreraSubida = new CyclicBarrier(capacidad);
    
    
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
    
        public void subir() throws InterruptedException {
            espaciosDisponibles.acquire(); // Esperar espacio disponible
            mutex.acquire(); // Exclusión mutua para abordar
            pasajerosABordo++;
            System.out.println(Thread.currentThread().getName() + " subió al tren. Pasajeros a bordo: " + pasajerosABordo);
            
            try {
                // Espera un tiempo antes de iniciar si no todos los pasajeros han subido
                barreraSubida.await(5, TimeUnit.SECONDS);
                iniciarViaje.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            mutex.release();
        }
    
        // Pasajero baja del tren
        public void bajar(Terminal terminal) throws InterruptedException {
            mutex.acquire();
            if(detenidoEnTerminal && terminalActual == terminal){
                espaciosDisponibles.release(); // Liberar espacio en el tren
                pasajerosABordo--;
            }
            mutex.release();
        }
    
        // El tren verifica y detiene en cada terminal según las solicitudes
        private void detenerEnTerminal(Terminal terminal) throws InterruptedException {
            System.out.println("El tren se detiene en la terminal: " + terminal.getIdTerminal());
            this.terminalActual = terminal;
            this.detenidoEnTerminal = true;
            Thread.sleep(1000); // Simulamos el tiempo de detención en la terminal
            this.detenidoEnTerminal = false;
        }

    // Inicia el viaje
    private void iniciarViajeTren() throws InterruptedException {
        this.iniciarViaje.acquire();
        System.out.println("El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
        
        for (int i = 0; i < listaTerminales.size(); i++) {
            Terminal terminal = listaTerminales.get(i);
            
            try {
                detenerEnTerminal(terminal); // Parar si hay solicitudes
                if (i == listaTerminales.size() - 1) { // Última terminal
                    reiniciarTren();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void reiniciarTren(){
        
    }

    @Override
    public void run() {
        while (true) {
            try {
                iniciarViajeTren();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

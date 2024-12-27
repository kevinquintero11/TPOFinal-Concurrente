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

import java.util.HashMap;
import java.util.Map;

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
    private Condition esperandoNuevoRecorrido = cerrojo.newCondition();
    private boolean inicioRecorrido = true;

    public Tren(int capacidad, List<Terminal> terminales){
        this.capacidadTren = capacidad;
        this.listaTerminales = terminales;
        this.espaciosDisponibles = new Semaphore(capacidadTren);
        this.pasajerosABordo = 0;
        this.iniciarViaje = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.barreraSubida = new CyclicBarrier(capacidad);
        this.terminalActual = terminales.get(0);
    }

    public void subir(Pasajero pasajero) throws InterruptedException {
        cerrojo.lock();
        try {
            // Solo permite subir si el tren está al inicio del recorrido
            while (!inicioRecorrido || pasajerosABordo >= capacidadTren) {
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + " intenta subir al tren, pero no puede.");
                esperandoNuevoRecorrido.await();
            }
            pasajerosABordo++;
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": subió al tren. Pasajeros a bordo: " + pasajerosABordo);
    
            // Notifica que el tren puede iniciar si está lleno
            if (pasajerosABordo == capacidadTren) {
                iniciarViaje.release();
            }
        } finally {
            cerrojo.unlock();
        }
    }
    
    public void bajar(Terminal terminal, Pasajero pasajero) throws InterruptedException {
        cerrojo.lock();
        try {
            // Espera hasta que el tren se detenga en una terminal.
            while (!detenidoEnTerminal || terminalActual.getIdTerminal() != terminal.getIdTerminal() ) {
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + ". Terminal actual: " + terminalActual.getIdTerminal() + ". Terminal de bajada: " + terminal.getIdTerminal());
                esperandoDetencion.await();
            }
            pasajerosABordo--;
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " bajó en la terminal " + terminalActual.getIdTerminal());
            esperandoBajadaPasajeros.signal(); // Notificar que un pasajero ha bajado.
            
        } finally {
            cerrojo.unlock();
        }
    }
    
    private void detenerEnTerminal(Terminal terminal) throws InterruptedException {
        cerrojo.lock();
        try {
            Log.escribir("El tren se detiene en la terminal: " + terminal.getIdTerminal());
            this.terminalActual = terminal;
            this.detenidoEnTerminal = true;
            esperandoDetencion.signalAll(); // Notificar a los pasajeros que pueden bajar.
            
            if (ultimaTerminal) {
                // Forzar el descenso de todos los pasajeros.
                while (pasajerosABordo > 0) {
                    Log.escribir("Todos los pasajeros deben bajar en la última terminal: " + terminal.getIdTerminal());
                    esperandoBajadaPasajeros.await(); // Esperar a que todos bajen.
                }
            }
            Thread.sleep(2000); // Simula la detención.
            this.detenidoEnTerminal = false;
        } finally {
            cerrojo.unlock();
        }
    }

    private void iniciarViajeTren() throws InterruptedException {
        iniciarViaje.acquire();
       // cerrojo.lock();
        try {
            inicioRecorrido = false; // Cambia el estado
            Log.escribir("El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
            int cantidadTerminales = listaTerminales.size();
            for (int i = 0; i < cantidadTerminales; i++) {
                Terminal terminal = listaTerminales.get(i);
                Log.escribir("Terminal actual: " + terminal.getIdTerminal());
                try {
                    if (i == cantidadTerminales - 1) { // Última terminal
                        ultimaTerminal = true;
                    }
                    detenerEnTerminal(terminal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.escribir("Terminó el recorrido del tren");
        } finally {
           // cerrojo.unlock();
        }
        
    }
    
    private void regresarInicio() throws InterruptedException {
        cerrojo.lock();
        try {
            while (pasajerosABordo > 0) {
                esperandoBajadaPasajeros.await();
            }
            Log.escribir("El tren regresa vacío al inicio del recorrido.");
            Thread.sleep(2000); // Simula el tiempo de regreso
            pasajerosABordo = 0;
            inicioRecorrido = true;
            ultimaTerminal = false; // Resetea el flag
            esperandoNuevoRecorrido.signalAll();
        } finally {
            cerrojo.unlock();
        }
    }
    
    

    @Override
    public void run() {
        while (true) {
            try {
                this.iniciarViajeTren();
                Log.escribir("Volviendo al inicio del aeropuerto.");
                this.regresarInicio();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // public class Tren implements Runnable {
    //     private int capacidadTren;
    //     private int pasajerosABordo;
    //     private List<Terminal> listaTerminales;
    //     private Semaphore espaciosDisponibles;
    //     private Semaphore iniciarViaje;
    //     private Semaphore mutex;
    //     private CyclicBarrier barreraSubida;
    //     private boolean detenidoEnTerminal = false;
    //     private boolean ultimaTerminal = false;
    //     private Terminal terminalActual;
    //     private ReentrantLock cerrojo = new ReentrantLock();
    //     private Condition esperandoDetencion = cerrojo.newCondition();
    //     private Condition esperandoBajadaPasajeros = cerrojo.newCondition();
    //     private Condition esperandoNuevoRecorrido = cerrojo.newCondition();
    //     private boolean inicioRecorrido = true;
        
    //     // Mapa para llevar cuenta de los pasajeros que bajan en cada terminal
    //     private Map<Terminal, Integer> pasajerosPorTerminal = new HashMap<>();
        
    //     public Tren(int capacidad, List<Terminal> terminales){
    //         this.capacidadTren = capacidad;
    //         this.listaTerminales = terminales;
    //         this.espaciosDisponibles = new Semaphore(capacidadTren);
    //         this.pasajerosABordo = 0;
    //         this.iniciarViaje = new Semaphore(0);
    //         this.mutex = new Semaphore(1);
    //         this.barreraSubida = new CyclicBarrier(capacidad);
    //         this.terminalActual = terminales.get(0);
            
    //         // Inicializar el mapa de pasajeros por terminal
    //         for (Terminal terminal : terminales) {
    //             pasajerosPorTerminal.put(terminal, 0);
    //         }
    //     }
        
    //     public void subir(Pasajero pasajero) throws InterruptedException {
    //         cerrojo.lock();
    //         try {
    //             while (!inicioRecorrido || pasajerosABordo >= capacidadTren) {
    //                 Log.escribir("Pasajero " + pasajero.getIdPasajero() + " intenta subir al tren, pero no puede.");
    //                 esperandoNuevoRecorrido.await();
    //             }
    //             pasajerosABordo++;
    //             Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": subió al tren. Pasajeros a bordo: " + pasajerosABordo);
        
    //             // Si el tren está lleno, permite iniciar el viaje
    //             if (pasajerosABordo == capacidadTren) {
    //                 iniciarViaje.release();
    //             }
    //         } finally {
    //             cerrojo.unlock();
    //         }
    //     }
        
    //     public void bajar(Terminal terminal, Pasajero pasajero) throws InterruptedException {
    //         cerrojo.lock();
    //         try {
    //             // Incrementamos el contador de pasajeros que bajarán en esta terminal
    //             pasajerosPorTerminal.put(terminal, pasajerosPorTerminal.get(terminal) + 1);
                
    //             // Espera hasta que el tren se detenga en la terminal correspondiente
    //             while (!detenidoEnTerminal || terminalActual.getIdTerminal() != terminal.getIdTerminal()) {
    //                 Log.escribir("Pasajero " + pasajero.getIdPasajero() + ". Terminal actual: " + terminalActual.getIdTerminal() + ". Terminal de bajada: " + terminal.getIdTerminal());
    //                 esperandoDetencion.await();
    //             }
                
    //             pasajerosABordo--;
    //             Log.escribir("Pasajero " + pasajero.getIdPasajero() + " bajó en la terminal " + terminalActual.getIdTerminal());
                
    //             // Notifica que un pasajero ha bajado en esta terminal
    //             pasajerosPorTerminal.put(terminal, pasajerosPorTerminal.get(terminal) - 1);
    //             if (pasajerosPorTerminal.get(terminal) == 0) {
    //                 esperandoBajadaPasajeros.signal();  // Señaliza que no hay más pasajeros en esta terminal.
    //             }
    //         } finally {
    //             cerrojo.unlock();
    //         }
    //     }
        
    //     private void detenerEnTerminal(Terminal terminal) throws InterruptedException {
    //         cerrojo.lock();
    //         try {
    //             Log.escribir("El tren se detiene en la terminal: " + terminal.getIdTerminal());
    //             this.terminalActual = terminal;
    //             this.detenidoEnTerminal = true;
    //             esperandoDetencion.signalAll();  // Notificar a los pasajeros que pueden bajar.
                
    //             // Si es la última terminal, esperamos que todos los pasajeros bajen antes de continuar
    //             if (ultimaTerminal) {
    //                 while (pasajerosABordo > 0) {
    //                     Log.escribir("Todos los pasajeros deben bajar en la última terminal: " + terminal.getIdTerminal());
    //                     esperandoBajadaPasajeros.await();  // Espera hasta que todos los pasajeros bajen.
    //                 }
    //             } else {
    //                 // Para terminales intermedias, esperamos hasta que todos los pasajeros que deben bajar aquí bajen.
    //                 while (pasajerosPorTerminal.get(terminal) > 0) {
    //                     Log.escribir("Esperando a que todos los pasajeros bajen en la terminal " + terminal.getIdTerminal());
    //                     esperandoBajadaPasajeros.await();  // Espera hasta que todos los pasajeros bajen.
    //                 }
    //             }
                
    //             // Simula el tiempo de detención en la terminal
    //             Thread.sleep(2000);  // Tiempo de espera simulado para la detención del tren.
    //             this.detenidoEnTerminal = false;  // El tren está listo para continuar.
                
    //         } finally {
    //             cerrojo.unlock();
    //         }
    //     }
        
    //     private void iniciarViajeTren() throws InterruptedException {
    //         iniciarViaje.acquire();
    //         cerrojo.lock();
    //         try {
    //             inicioRecorrido = false;
    //             Log.escribir("El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
    //             int cantidadTerminales = listaTerminales.size();
    //             for (int i = 0; i < cantidadTerminales; i++) {
    //                 Terminal terminal = listaTerminales.get(i);
    //                 Log.escribir("Terminal actual: " + terminal.getIdTerminal());
    //                 try {
    //                     if (i == cantidadTerminales - 1) {  // Última terminal
    //                         ultimaTerminal = true;
    //                     }
    //                     detenerEnTerminal(terminal);
    //                 } catch (InterruptedException e) {
    //                     e.printStackTrace();
    //                 }
    //             }
    //             Log.escribir("Terminó el recorrido del tren");
    //         } finally {
    //             cerrojo.unlock();
    //         }
    //     }
        
    //     private void regresarInicio() throws InterruptedException {
    //         cerrojo.lock();
    //         try {
    //             while (pasajerosABordo > 0) {
    //                 esperandoBajadaPasajeros.await();
    //             }
    //             Log.escribir("El tren regresa vacío al inicio del recorrido.");
    //             Thread.sleep(2000);  // Simula el tiempo de regreso
    //             pasajerosABordo = 0;
    //             inicioRecorrido = true;
    //             ultimaTerminal = false;
    //             esperandoNuevoRecorrido.signalAll();
    //         } finally {
    //             cerrojo.unlock();
    //         }
    //     }
        
    //     @Override
    //     public void run() {
    //         while (true) {
    //             try {
    //                 this.iniciarViajeTren();
    //                 Log.escribir("Volviendo al inicio del aeropuerto.");
    //                 this.regresarInicio();
    //             } catch (InterruptedException e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     }
    // }

}

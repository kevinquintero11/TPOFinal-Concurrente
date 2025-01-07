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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// public class Tren implements Runnable{
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

//     public Tren(int capacidad, List<Terminal> terminales){
//         this.capacidadTren = capacidad;
//         this.listaTerminales = terminales;
//         this.espaciosDisponibles = new Semaphore(capacidadTren);
//         this.pasajerosABordo = 0;
//         this.iniciarViaje = new Semaphore(0);
//         this.mutex = new Semaphore(1);
//         this.barreraSubida = new CyclicBarrier(capacidad);
//         this.terminalActual = terminales.get(0);
//     }

//     public void subir(Pasajero pasajero) throws InterruptedException {
//         cerrojo.lock();
//         try {
//             // Solo permite subir si el tren está al inicio del recorrido
//             while (!inicioRecorrido || pasajerosABordo >= capacidadTren) {
//                 Log.escribir("Pasajero " + pasajero.getIdPasajero() + " intenta subir al tren, pero no puede.");
//                 esperandoNuevoRecorrido.await();
//             }
//             pasajerosABordo++;
//             Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": subió al tren. Pasajeros a bordo: " + pasajerosABordo);
    
//             // Notifica que el tren puede iniciar si está lleno
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
//             // Espera hasta que el tren se detenga en una terminal.
//             while (!detenidoEnTerminal || terminalActual.getIdTerminal() != terminal.getIdTerminal() ) {
//                 Log.escribir("Pasajero " + pasajero.getIdPasajero() + ". Terminal actual: " + terminalActual.getIdTerminal() + ". Terminal de bajada: " + terminal.getIdTerminal());
//                 esperandoDetencion.await();
//             }
//             pasajerosABordo--;
//             Log.escribir("Pasajero " + pasajero.getIdPasajero() + " bajó en la terminal " + terminalActual.getIdTerminal());
//             esperandoBajadaPasajeros.signal(); // Notificar que un pasajero ha bajado.
            
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
//             esperandoDetencion.signalAll(); // Notificar a los pasajeros que pueden bajar.
            
//             if (ultimaTerminal) {
//                 // Forzar el descenso de todos los pasajeros.
//                 while (pasajerosABordo > 0) {
//                     Log.escribir("Todos los pasajeros deben bajar en la última terminal: " + terminal.getIdTerminal());
//                     esperandoBajadaPasajeros.await(); // Esperar a que todos bajen.
//                 }
//             }
//             Thread.sleep(2000); // Simula la detención.
//             this.detenidoEnTerminal = false;
//         } finally {
//             cerrojo.unlock();
//         }
//     }

//     private void iniciarViajeTren() throws InterruptedException {
//         iniciarViaje.acquire();
//        // cerrojo.lock();
//         try {
//             inicioRecorrido = false; // Cambia el estado
//             Log.escribir("El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
//             int cantidadTerminales = listaTerminales.size();
//             for (int i = 0; i < cantidadTerminales; i++) {
//                 Terminal terminal = listaTerminales.get(i);
//                 Log.escribir("Terminal actual: " + terminal.getIdTerminal());
//                 try {
//                     if (i == cantidadTerminales - 1) { // Última terminal
//                         ultimaTerminal = true;
//                     }
//                     detenerEnTerminal(terminal);
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//             }
//             Log.escribir("Terminó el recorrido del tren");
//         } finally {
//            // cerrojo.unlock();
//         }
        
//     }
    
//     private void regresarInicio() throws InterruptedException {
//         cerrojo.lock();
//         try {
//             while (pasajerosABordo > 0) {
//                 esperandoBajadaPasajeros.await();
//             }
//             Log.escribir("El tren regresa vacío al inicio del recorrido.");
//             Thread.sleep(2000); // Simula el tiempo de regreso
//             pasajerosABordo = 0;
//             inicioRecorrido = true;
//             ultimaTerminal = false; // Resetea el flag
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


    public class Tren implements Runnable {
        private int capacidadTren;
        private int pasajerosABordo;
        private List<Terminal> listaTerminales;
        //private Semaphore espaciosDisponibles;
        private Semaphore iniciarViaje;
        //private Semaphore mutex;
        //private CyclicBarrier barreraSubida;
        private boolean detenidoEnTerminal = false;
        //private boolean ultimaTerminal = false;
        private Terminal terminalActual;
        private ReentrantLock cerrojo = new ReentrantLock();
        //private Condition esperandoDetencion = cerrojo.newCondition();
        private Condition esperandoBajadaPasajeros = cerrojo.newCondition();
        private Condition esperandoNuevoRecorrido = cerrojo.newCondition();
        private boolean inicioRecorrido = true;
        private boolean trenLleno = false;

        private List<Condition> conjuntoEesperaPorTerminal = new ArrayList<>();
        // Arreglo para llevar cuenta de pasajeros por terminal
        private int[] pasajerosPorTerminal;
        
        public Tren(int capacidad, List<Terminal> terminales){
            this.capacidadTren = capacidad;
            this.listaTerminales = terminales;
            //this.espaciosDisponibles = new Semaphore(capacidadTren);
            this.pasajerosABordo = 0;
            this.iniciarViaje = new Semaphore(0);
            //this.mutex = new Semaphore(1);
            //this.barreraSubida = new CyclicBarrier(capacidad);
            this.terminalActual = terminales.get(0);

            // Inicializar el mapa de pasajeros por terminal
            pasajerosPorTerminal = new int[terminales.size()];
            for (int i = 0; i < terminales.size(); i++) {
                conjuntoEesperaPorTerminal.add(cerrojo.newCondition());
                pasajerosPorTerminal[i] = 0;
            }
        }
        
        public void subir(Pasajero pasajero) throws InterruptedException {
            cerrojo.lock();
            try {
                while (!inicioRecorrido || pasajerosABordo >= capacidadTren) {
                    Log.escribir("Pasajero " + pasajero.getIdPasajero() + " intenta subir al tren, pero no puede.");
                    esperandoNuevoRecorrido.await();
                }
                pasajerosABordo++;
                Log.escribir("> Pasajero " + pasajero.getIdPasajero() + ": subió al tren. Pasajeros a bordo: " + pasajerosABordo);
                if(pasajerosABordo == 1){
                    iniciarViaje.release();
                }
                
                // Si el tren está lleno, permite iniciar el viaje
            } finally {
                cerrojo.unlock();
            }
        }
        
        public void bajar(Terminal terminal, Pasajero pasajero) throws InterruptedException {
            cerrojo.lock();
            try {
                int indiceTerminal = listaTerminales.indexOf(terminal);
                // Incrementamos el contador de pasajeros para esta terminal
                pasajerosPorTerminal[indiceTerminal]++;
    
                // Espera hasta que el tren se detenga en la terminal correspondiente
                while (!detenidoEnTerminal || !terminalActual.equals(terminal)) {
                    Log.escribir("\u23F3 Pasajero " + pasajero.getIdPasajero() + " espera para bajar en la terminal " + terminal.getIdTerminal());
                    conjuntoEesperaPorTerminal.get(indiceTerminal).await();
                }
    
                pasajerosABordo--;
                Log.escribir("< Pasajero " + pasajero.getIdPasajero() + " bajó en la terminal " + terminal.getIdTerminal());
    
                // Decrementa el contador de pasajeros que deben bajar en esta terminal
                pasajerosPorTerminal[indiceTerminal]--;
    
                // Si no quedan más pasajeros por bajar en esta terminal, señaliza al tren
                if (pasajerosPorTerminal[indiceTerminal] == 0) {
                    conjuntoEesperaPorTerminal.get(indiceTerminal).signalAll();
                }
            } finally {
                cerrojo.unlock();
            }
        }
        
        private void detenerEnTerminal(Terminal terminal) throws InterruptedException {
            cerrojo.lock();
            try {
                Log.escribir("\uD83D\uDEA8 El tren se detiene en la terminal: " + terminal.getIdTerminal());
                this.terminalActual = terminal;
                this.detenidoEnTerminal = true;
    
                int indiceTerminal = listaTerminales.indexOf(terminal);
                // Notificar a los pasajeros de esta terminal
                conjuntoEesperaPorTerminal.get(indiceTerminal).signalAll();
    
                // Esperar a que todos los pasajeros bajen
                while (pasajerosPorTerminal[indiceTerminal] > 0) {
                    Log.escribir("\u23F3 Esperando a que todos los pasajeros bajen en la terminal " + terminal.getIdTerminal());
                    conjuntoEesperaPorTerminal.get(indiceTerminal).await();
                }
    
                Thread.sleep(2000); // Simula el tiempo de detención
                this.detenidoEnTerminal = false;
            } finally {
                cerrojo.unlock();
            }
        }
        
        private void iniciarViajeTren() throws InterruptedException {
            //long tiempoInicio = System.currentTimeMillis();
            long tiempoEsperaMaximo = 500; // Espera máxima de 5 segundos.
            
            // while (!iniciarViaje.tryAcquire(tiempoEsperaMaximo, TimeUnit.MILLISECONDS)) {
            //     if (pasajerosABordo > 0) {
            //         Log.escribir("El tren no se llenó, pero iniciará el viaje con " + pasajerosABordo + " pasajeros.");
            //         break;
            //     } else {
            //         tiempoEsperaMaximo -= (System.currentTimeMillis() - tiempoInicio);
            //         // if (tiempoEsperaMaximo <= 0) {
            //         //     //Log.escribir("El tren no tiene pasajeros y no iniciará el viaje.");
            //         // }
            //     } 
            // }

        //iniciarViaje.tryAcquire(tiempoEsperaMaximo, TimeUnit.MILLISECONDS);
           iniciarViaje.acquire();
           if(!trenLleno){
                Thread.sleep(2000);
            }
           
            //cerrojo.lock();
            //try {
                inicioRecorrido = false;
                Log.escribir("\uD83D\uDE84 El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
                int cantidadTerminales = listaTerminales.size();
                for (int i = 0; i < cantidadTerminales; i++) {
                    Terminal terminal = listaTerminales.get(i);
                    Log.escribir(" Terminal actual: " + terminal.getIdTerminal());
                    detenerEnTerminal(terminal);
                }
                Log.escribir("\uD83D\uDE84 Terminó el recorrido del tren");
            // } finally {
            //     cerrojo.unlock();
            // }
        }
        
        private void regresarInicio() throws InterruptedException {
            cerrojo.lock();
            try {
                while (pasajerosABordo > 0) {
                    esperandoBajadaPasajeros.await();
                }
                Log.escribir("\uD83D\uDE84 El tren regresa vacío al inicio del recorrido.");
                Thread.sleep(2000);  // Simula el tiempo de regreso
                pasajerosABordo = 0;
                inicioRecorrido = true;
                trenLleno = false;
                //ultimaTerminal = false;
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
                    //Log.escribir("Volviendo al inicio del aeropuerto.");
                    this.regresarInicio();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    //}

}

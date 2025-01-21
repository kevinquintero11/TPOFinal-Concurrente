package Aeropuerto.Tren;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import Aeropuerto.Terminal.Terminal;
import Pasajero.Pasajero;
import Utilidades.Log;
import java.util.ArrayList;


public class Tren implements Runnable {
    private int capacidadTren;
    private int pasajerosABordo;
    private List<Terminal> listaTerminales;

    private boolean detenidoEnTerminal = false;

    private Terminal terminalActual;
    private ReentrantLock cerrojo = new ReentrantLock();

    //private Condition esperandoBajadaPasajeros = cerrojo.newCondition();
    private Condition esperandoNuevoRecorrido = cerrojo.newCondition();
    private Condition avanzar = cerrojo.newCondition();
    private boolean inicioRecorrido = true;

    private List<Condition> conjuntoEsperaPorTerminal = new ArrayList<>();
    // Arreglo para llevar cuenta de pasajeros por terminal
    private int[] pasajerosPorTerminal;
    private CyclicBarrier barrera;

    public Tren(int capacidad, List<Terminal> terminales) {
        this.capacidadTren = capacidad;
        this.listaTerminales = terminales;
        this.pasajerosABordo = 0;
        this.terminalActual = terminales.get(0);

        this.barrera = new CyclicBarrier(capacidad+1);

        // Inicializar el mapa de pasajeros por terminal
        pasajerosPorTerminal = new int[terminales.size()];
        for (int i = 0; i < terminales.size(); i++) {
            conjuntoEsperaPorTerminal.add(cerrojo.newCondition());
            pasajerosPorTerminal[i] = 0;
        }
    }

    public void subir(Pasajero pasajero) throws InterruptedException {
        cerrojo.lock();
        try {
            while (!inicioRecorrido || pasajerosABordo == capacidadTren) {
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + " intenta subir al tren, pero no puede.");
                esperandoNuevoRecorrido.await();
            }
            pasajerosABordo++;
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": subió al tren. Pasajeros a bordo: " + pasajerosABordo);
            cerrojo.unlock();
            barrera.await();

            // Si el tren está lleno, permite iniciar el viaje
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
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
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + " espera para bajar en la terminal " + terminal.getIdTerminal());
                conjuntoEsperaPorTerminal.get(indiceTerminal).await();
            }

            pasajerosABordo--;
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " bajó en la terminal " + terminal.getIdTerminal());

            // Decrementa el contador de pasajeros que deben bajar en esta terminal
            pasajerosPorTerminal[indiceTerminal]--;

            // Si no quedan más pasajeros por bajar en esta terminal, señaliza al tren
            if (pasajerosPorTerminal[indiceTerminal] == 0) {
                //conjuntoEsperaPorTerminal.get(indiceTerminal).signalAll();
                avanzar.signal();
            }
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

            int indiceTerminal = listaTerminales.indexOf(terminal);
            
            // Notificar a los pasajeros de esta terminal
            conjuntoEsperaPorTerminal.get(indiceTerminal).signalAll();

            // Esperar a que todos los pasajeros bajen
            while (pasajerosPorTerminal[indiceTerminal] > 0) {
                Log.escribir("Esperando a que todos los pasajeros bajen en la terminal " + terminal.getIdTerminal() + "...");
                //conjuntoEsperaPorTerminal.get(indiceTerminal).await();
                avanzar.await();
            }

            Thread.sleep(2000); // Simula el tiempo de detención
            this.detenidoEnTerminal = false;
        } finally {
            cerrojo.unlock();
        }
    }

    private void iniciarViajeTren() throws InterruptedException {
        try {
            barrera.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        inicioRecorrido = false;

        Log.escribir("El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
        
        int cantidadTerminales = listaTerminales.size();
        for (int i = 0; i < cantidadTerminales; i++) {
            Terminal terminal = listaTerminales.get(i);
            Log.escribir(" Terminal actual: " + terminal.getIdTerminal());
            detenerEnTerminal(terminal);
        }
        Log.escribir("Terminó el recorrido del tren.");
    }

    private void regresarInicio() throws InterruptedException {
        cerrojo.lock();
        try {
            Log.escribir("Tren volviendo al inicio del aeropuerto...");
            Thread.sleep(2000); // Simula el tiempo de regreso
            pasajerosABordo = 0;
            inicioRecorrido = true;
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
                this.regresarInicio();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // }

}

package Aeropuerto.Tren;

import java.util.List;
import java.util.concurrent.Semaphore;

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

    private Semaphore iniciarViaje;

    private boolean detenidoEnTerminal = false;

    private Terminal terminalActual;
    private ReentrantLock cerrojo = new ReentrantLock();

    private Condition esperandoBajadaPasajeros = cerrojo.newCondition();
    private Condition esperandoNuevoRecorrido = cerrojo.newCondition();
    private boolean inicioRecorrido = true;
    private boolean trenLleno = false;

    private List<Condition> conjuntoEesperaPorTerminal = new ArrayList<>();
    // Arreglo para llevar cuenta de pasajeros por terminal
    private int[] pasajerosPorTerminal;

    public Tren(int capacidad, List<Terminal> terminales) {
        this.capacidadTren = capacidad;
        this.listaTerminales = terminales;

        this.pasajerosABordo = 0;
        this.iniciarViaje = new Semaphore(0);

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
            Log.escribir(
                    "Pasajero " + pasajero.getIdPasajero() + ": subió al tren. Pasajeros a bordo: " + pasajerosABordo);
            if (pasajerosABordo == 1) {
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
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + " espera para bajar en la terminal "
                        + terminal.getIdTerminal());
                conjuntoEesperaPorTerminal.get(indiceTerminal).await();
            }

            pasajerosABordo--;
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + " bajó en la terminal " + terminal.getIdTerminal());

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
                Log.escribir(
                        "Esperando a que todos los pasajeros bajen en la terminal " + terminal.getIdTerminal() + "...");
                conjuntoEesperaPorTerminal.get(indiceTerminal).await();
            }

            Thread.sleep(2000); // Simula el tiempo de detención
            this.detenidoEnTerminal = false;
        } finally {
            cerrojo.unlock();
        }
    }

    private void iniciarViajeTren() throws InterruptedException {

        iniciarViaje.acquire();
        if (!trenLleno) {
            Thread.sleep(2000);
        }

        inicioRecorrido = false;
        Log.escribir("\uD83D\uDE84 El tren comienza su recorrido con " + pasajerosABordo + " pasajeros a bordo.");
        int cantidadTerminales = listaTerminales.size();
        for (int i = 0; i < cantidadTerminales; i++) {
            Terminal terminal = listaTerminales.get(i);
            Log.escribir(" Terminal actual: " + terminal.getIdTerminal());
            detenerEnTerminal(terminal);
        }
        Log.escribir("\uD83D\uDE84 Terminó el recorrido del tren");
    }

    private void regresarInicio() throws InterruptedException {
        cerrojo.lock();
        try {
            while (pasajerosABordo > 0) {
                esperandoBajadaPasajeros.await();
            }
            Log.escribir("\uD83D\uDE84 El tren regresa vacío al inicio del recorrido.");
            Thread.sleep(2000); // Simula el tiempo de regreso
            pasajerosABordo = 0;
            inicioRecorrido = true;
            trenLleno = false;
            // ultimaTerminal = false;
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
                // Log.escribir("Volviendo al inicio del aeropuerto.");
                this.regresarInicio();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // }

}

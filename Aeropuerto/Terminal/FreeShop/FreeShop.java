package Aeropuerto.Terminal.FreeShop;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import Pasajero.Pasajero;
import Utilidades.Log;

// CLASE QUE SIMULA LA TIENDA DE UNA TERMINAL

public class FreeShop {

    private String nombre;
    private Semaphore capacidadTienda; 
    private Semaphore cajasDisponibles; 
    private Semaphore mutex;
    private double balanceTienda;

    public FreeShop(int capacidad, int numCajas, String nombreTienda) {
        this.capacidadTienda = new Semaphore(capacidad);
        this.cajasDisponibles = new Semaphore(numCajas);
        this.mutex = new Semaphore(1);
        this.nombre = nombreTienda;
        this.balanceTienda = ThreadLocalRandom.current().nextDouble(5000, 20000);
    }

    // Método ejecutado por los hilos pasajero
    public boolean ingresarFreeShop(Pasajero pasajero) {
        boolean ingreso = false;
        try {
            if (!capacidadTienda.tryAcquire()) {
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": no pudo ingresar. " + this.nombre + " está llena.");
                ingreso = false;
            }else{
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": ingresó a la " + this.nombre);
                ingreso = true;
            }   
            
        } catch (Exception e) {
            e.printStackTrace();          
        }

        return ingreso;
    }

    // Método ejecutado por los hilos pasajero
    public void salirFreeShop(Pasajero pasajero) {
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": salió de la " + this.nombre);
        capacidadTienda.release(); // Libera un lugar en la tienda
    }

    // Método ejecutado por los hilos pasajero
    public void comprar(double monto, Pasajero pasajero) {
        try {
            cajasDisponibles.acquire(); // Espera por una caja disponible
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": está usando una caja. Mostrando un gasto de $" + Math.round(monto));
            mutex.acquire();
            balanceTienda += monto;
            mutex.release();
            Thread.sleep(1000); // Simula el tiempo de uso de la caja
            cajasDisponibles.release(); // Libera una caja
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

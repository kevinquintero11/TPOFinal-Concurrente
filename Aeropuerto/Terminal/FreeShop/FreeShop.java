package Aeropuerto.Terminal.FreeShop;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

import Pasajero.Pasajero;
import Utilidades.Log;

public class FreeShop {

    private String nombre;
    private Semaphore capacidadTienda; // Controla el acceso a la tienda
    private Semaphore cajasDisponibles; // Controla la cantidad de cajas disponibles
    private Semaphore mutex; // Exclusión mutua para acceder a las listas de cajas
    
    // private List<Caja> cajasRDisponibles = new LinkedList<>();
    // private List<Caja> cajasOcupadas = new LinkedList<>();
    private double balanceTienda;

    public FreeShop(int capacidad, int numCajas, String nombreTienda) {
        this.capacidadTienda = new Semaphore(capacidad);
        this.cajasDisponibles = new Semaphore(numCajas);
        this.mutex = new Semaphore(1);
        
        this.nombre = nombreTienda;
        this.balanceTienda = ThreadLocalRandom.current().nextDouble(5000, 20000);

    }

    // Método para ingresar al free-shop
    public boolean ingresarFreeShop(Pasajero pasajero) {
        boolean ingreso = false;
        try {
            if (!capacidadTienda.tryAcquire()) {
                Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": no pudo ingresar." + this.nombre + " lleno.");
                ingreso = false;
            }
            Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": ingresó a la " + this.nombre);
            ingreso = true;
        } catch (Exception e) {
            e.printStackTrace();
            ingreso = false;
        }

        return ingreso;
    }

    public void salirFreeShop(Pasajero pasajero) {
       
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + ": salió de la " + this.nombre);
        capacidadTienda.release(); // Libera un lugar en la tienda
    }

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

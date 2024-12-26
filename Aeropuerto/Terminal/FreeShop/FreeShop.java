package Aeropuerto.Terminal.FreeShop;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

import Pasajero.Pasajero;
import Utilidades.Log;

public class FreeShop {

    private String nombre;
    private int capacidadMax;
    private Semaphore capacidadTienda; // Controla el acceso a la tienda
    private Semaphore cajasDisponibles; // Controla la cantidad de cajas disponibles
    private Semaphore mutex; // Exclusión mutua para acceder a las listas de cajas
    
    private List<Caja> cajasRDisponibles = new LinkedList<>();
    private List<Caja> cajasOcupadas = new LinkedList<>();
    private double balanceTienda;

    public FreeShop(int capacidad, int numCajas, String nombreTienda) {
        this.capacidadMax = capacidad;
        this.capacidadTienda = new Semaphore(capacidad);
        this.cajasDisponibles = new Semaphore(numCajas);
        this.mutex = new Semaphore(1);
        
        this.nombre = nombreTienda;
        this.balanceTienda = ThreadLocalRandom.current().nextDouble(5000, 20000);

        // Crear las cajas registradoras
        for (int i = 0; i < numCajas; i++) {
            cajasRDisponibles.add(new Caja(i));
        }
    }

    // Método para ingresar al free-shop
    public boolean ingresarFreeShop(Pasajero pasajero) {
        boolean ingreso = false;
        try {
            if (!capacidadTienda.tryAcquire()) {
                //System.out.println(Thread.currentThread().getName() + " no pudo ingresar. Free-shop lleno.");
                Log.escribir("Pasajero " + pasajero.getReserva().getIdReserva() + ": no pudo ingresar. Free-shop lleno.");
                ingreso = false;
            }
            Log.escribir("Pasajero " + pasajero.getReserva().getIdReserva() + ": ingresó al free-shop.");
            //System.out.println(Thread.currentThread().getName() + " ingresó al free-shop.");
            ingreso = true;
        } catch (Exception e) {
            e.printStackTrace();
            ingreso = false;
        }

        return ingreso;
    }

    public void salirFreeShop(Pasajero pasajero) {
        //System.out.println(Thread.currentThread().getName() + " salió del free-shop.");
        Log.escribir("Pasajero " + pasajero.getReserva().getIdReserva() + ": salió del free-shop.");
        capacidadTienda.release(); // Libera un lugar en la tienda
    }

    public void comprar(double monto, Pasajero pasajero) {
        try {
            cajasDisponibles.acquire(); // Espera por una caja disponible
           // System.out.println(Thread.currentThread().getName() + " está usando una caja.");
            Log.escribir("Pasajero " + pasajero.getReserva().getIdReserva() + ":  está usando una caja.");
            mutex.acquire();
            balanceTienda += monto;
            mutex.release();
            Thread.sleep(1000); // Simula el tiempo de uso de la caja
            Log.escribir(nombre);
            //System.out.println("Pasajero " + pasajero.getReserva().getIdReserva() + ": terminó de usar la caja.");
            cajasDisponibles.release(); // Libera una caja
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    

    // Método para usar una caja registradora
    // public void usarCaja() {
    //     try {
    //         cajasDisponibles.acquire(); // Espera por una caja disponible

    //         // Obtener una caja de la lista
    //         Caja caja;
    //         mutex.acquire(); // Exclusión mutua para acceder a las listas
    //         try {
    //             caja = cajasRDisponibles.remove(0); // Tomar una caja disponible
    //             cajasOcupadas.add(caja); // Mover a la lista de ocupadas
    //         } finally {
    //             mutex.release();
    //         }

    //         System.out.println(Thread.currentThread().getName() + " está usando la caja " + caja.getIdCaja());
    //         Thread.sleep(2000); // Simula el tiempo de uso de la caja

    //         // Liberar la caja
    //         mutex.acquire();
    //         try {
    //             cajasOcupadas.remove(caja); // Quitar de la lista de ocupadas
    //             cajasRDisponibles.add(caja); // Regresar a la lista de disponibles
    //         } finally {
    //             mutex.release();
    //         }
    //         System.out.println(Thread.currentThread().getName() + " terminó de usar la caja " + caja.getIdCaja());
    //         cajasDisponibles.release(); // Libera una caja

    //     } catch (InterruptedException e) {
    //         e.printStackTrace();
    //     }
    // }

}

package Aeropuerto;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.PuestoAtencion.PuestoAtencion;
import Pasajero.Pasajero;
import Utilidades.Log;

public class PuestoInforme {
    
    private Semaphore mutex;

    public PuestoInforme () {
        this.mutex = new Semaphore(1);
    }

    public PuestoAtencion atenderPasajero(Pasajero pasajero, List<PuestoAtencion> puestos) throws InterruptedException{
        mutex.acquire();
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + " esta siendo atendido en el puesto de informes.");
        Thread.sleep(1000);
        PuestoAtencion puesto = indicarPuesto(pasajero, puestos);
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + " se tiene que dirigir al puesto " + puesto.getAerolinea().getNombre());
        mutex.release();
        return puesto;

    }

    private PuestoAtencion indicarPuesto(Pasajero pasajero, List<PuestoAtencion> puestos){
        Aerolinea  aerolineaReserva = pasajero.getReserva().getAerolinea();
        PuestoAtencion puestoEncontrado = null;
        int i = 0;
        int longitud = puestos.size();
        
        while (i < longitud && puestoEncontrado == null) {
            PuestoAtencion puesto = puestos.get(i);
            if (puesto.getAerolinea() != null 
                && puesto.getAerolinea().getNombre().equals(aerolineaReserva.getNombre())) {
                puestoEncontrado = puesto;
            }
            i++;
        }
        return puestoEncontrado;

    }
}

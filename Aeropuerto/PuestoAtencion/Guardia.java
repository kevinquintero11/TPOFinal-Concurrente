package Aeropuerto.PuestoAtencion;

import Pasajero.Pasajero;

public class Guardia implements Runnable {

    private PuestoAtencion puestoTrabajo;

    public Guardia(PuestoAtencion puesto){
        this.puestoTrabajo = puesto;
    }

    @Override
    public void run() {
        while (true) {
            try {
                puestoTrabajo.permitirIngresoDesdeHall(); // Permitir ingreso desde el hall central
            } catch (InterruptedException e) {
                //Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
}

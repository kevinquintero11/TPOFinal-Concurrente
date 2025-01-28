package Aeropuerto.PuestoAtencion;

// CLASE QUE SIMULA EL FUNCIONAMIENTO DE UN GUARDIA

public class Guardia implements Runnable {

    private PuestoAtencion puestoTrabajo;

    public Guardia(PuestoAtencion puesto){
        this.puestoTrabajo = puesto;
    }

    public PuestoAtencion getPuestoAtencion(){
        return this.puestoTrabajo;
    }

    @Override
    public void run() {
        while (true) {
            try {
                puestoTrabajo.permitirIngresoDesdeHall(); 
            } catch (InterruptedException e) {
                break;
            }
        }
    }

   
    
}

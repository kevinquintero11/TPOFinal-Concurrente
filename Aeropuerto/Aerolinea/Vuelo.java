package Aeropuerto.Aerolinea;

import java.util.concurrent.CountDownLatch;
import Aeropuerto.Terminal.PuestoEmbarque;
import Utilidades.Log;
import Utilidades.Reloj;

// CLASE QUE SIMULA UN VUELO DE UNA AEROLINEA

public class Vuelo implements Runnable{

    private int horaDespegue;
    private String destino;
    private PuestoEmbarque puestoSalida;
    private Reloj relojAeropuerto;
    private CountDownLatch latchDespegue;
    private boolean partioRumbo;
    private Aerolinea aerolinea;

    public Vuelo(int hs, String nombreDestino, PuestoEmbarque puesto, Reloj reloj, CountDownLatch despegue, Aerolinea aero){
        this.horaDespegue = hs;
        this.destino = nombreDestino;
        this.puestoSalida = puesto;
        this.relojAeropuerto = reloj;
        this.latchDespegue = despegue;
        this.partioRumbo = false;
        this.aerolinea = aero;
    }

    public String getDestino(){
        return this.destino;
    }

    public PuestoEmbarque getPuestoEmbarque(){
        return this.puestoSalida;
    }

    public int getHora(){
        return this.horaDespegue;
    }

    public void esperarDespegue() throws InterruptedException{
       this.latchDespegue.await(); // Los pasajeros esperan acá, hasta que el latch llegue a 0
    }

    public boolean inicioViaje(){
        return this.partioRumbo;
    }

    public Aerolinea getAerolinea(){
        return this.aerolinea;
    }

    @Override
    public void run() {
        try {
            relojAeropuerto.verificarHoraVuelo(horaDespegue); // Verifica si ya es hora de despegar
            Log.escribir("Comienza el abordaje del vuelo con destino '" + this.destino + " de la aerolinea '" + this.aerolinea.getNombre() + "'");
            latchDespegue.countDown();
            Thread.sleep(10000); // Se da un tiempo de una hora para el abordaje de los pasajeros
            this.partioRumbo = true;
            Log.escribir("Despega el vuelo con destino '" + this.destino + " de la aerolinea '" + this.aerolinea.getNombre() + "'");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

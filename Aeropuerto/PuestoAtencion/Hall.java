package Aeropuerto.PuestoAtencion;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Pasajero.Pasajero;

public class Hall {
    
    private BlockingQueue<Pasajero> colaEspera;
   
    public Hall() { 
        this.colaEspera = new LinkedBlockingQueue<>(200);
    }

    public BlockingQueue<Pasajero> getColaEspera(){
        return this.colaEspera;
    }
}

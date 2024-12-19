package Pasajero;

import java.util.List;

import Aeropuerto.Aeropuerto;
import Aeropuerto.Aerolinea.Reserva;
import Aeropuerto.PuestoAtencion.PuestoAtencion;
import Aeropuerto.Terminal.Terminal;

public class Pasajero implements Runnable {

    private Reserva miReserva;
    private int idPasajero;

    public Pasajero(int id, Reserva res){
        this.idPasajero = id;
        this.miReserva = res;
    }

    public int getIdPasajero(){
        return this.idPasajero;
    }

    public void setIdPasajero(int id){
        this.idPasajero = id;
    }

    public Reserva getReserva(){
        return this.miReserva;
    }

    public void setReserva (Reserva res) {
        this.miReserva = res;
    }

    @Override
    public void run() {

        Aeropuerto aeropuerto = this.miReserva.getAeropuerto();
        PuestoAtencion puesto;
        try {
            aeropuerto.ingresarAeropuerto();
            puesto = aeropuerto.ingresarPuestoInforme(this);
            List<Object> terminalYPuertoEmbarque = puesto.ingresarPuestoAtencion(this); // Intenta ingresar al puesto
            aeropuerto.irTerminal(this, terminalYPuertoEmbarque);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        
    }


    
}

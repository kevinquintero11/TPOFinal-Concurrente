package Pasajero;

import Aeropuerto.Aerolinea.Reserva;

public class Pasajero {

     private Reserva miReserva;
    private int idPasajero;

    public Pasajero(int id, Reserva res){
        this.idPasajero = id;
        this.miReserva = res;
    }
    
}

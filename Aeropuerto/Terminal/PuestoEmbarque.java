package Aeropuerto.Terminal;

import Aeropuerto.Aerolinea.Vuelo;
import Pasajero.Pasajero;
import Utilidades.Log;

public class PuestoEmbarque {
    
    private int idPuesto;

    public PuestoEmbarque(int id){
        this.idPuesto = id;
    }

    public int getIdPuesto(){
        return this.idPuesto;
    }

    public void esperarAbordaje(Vuelo vuelo, Pasajero pasajero) throws InterruptedException{
        Log.escribir("Pasajero " + pasajero.getIdPasajero() + " está esperando al abordaje del vuelo: " + vuelo.getDestino());
        vuelo.esperarDespegue();
    }
    
}

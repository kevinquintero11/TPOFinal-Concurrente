package Aeropuerto.Terminal;

import Aeropuerto.Aerolinea.Vuelo;

public class PuestoEmbarque {
    
    private int idPuesto;

    public PuestoEmbarque(int id){
        this.idPuesto = id;
    }

    public int getIdPuesto(){
        return this.idPuesto;
    }

    public void esperarAbordaje(Vuelo vuelo) throws InterruptedException{
        vuelo.esperarDespegue();
    }
    
}

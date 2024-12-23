package Aeropuerto.Aerolinea;

//import java.util.List;

import Aeropuerto.Terminal.PuestoEmbarque;
import Utilidades.Reloj;

public class Vuelo {

    private int horaDespegue;
    private String destino;
    private PuestoEmbarque puestoSalida;
    private Reloj relojAeropuerto;

    public Vuelo(int hs, String nombreDestino, PuestoEmbarque puesto){
        this.horaDespegue = hs;
        this.destino = nombreDestino;
        this.puestoSalida = puesto;
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

    public synchronized void esperarDespegue() throws InterruptedException{
       relojAeropuerto.verificarHoraVuelo(this.horaDespegue); //en este instante, los hilos haran wait dentro de verificarHolaVuelo
    }


}

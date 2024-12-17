package Aeropuerto.Aerolinea;

import java.util.List;

import Aeropuerto.Terminal.PuestoEmbarque;

public class Vuelo {

    private int hora;
    private String destino;
    private PuestoEmbarque puestoSalida;

    public Vuelo(int hs, String nombreDestino, PuestoEmbarque puesto){
        this.hora = hs;
        this.destino = nombreDestino;
        this.puestoSalida = puesto;

    }

    public PuestoEmbarque getPuestoEmbarque(){
        return this.puestoSalida;
    }

}

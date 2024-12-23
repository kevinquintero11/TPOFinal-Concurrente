package Aeropuerto.Terminal;

import Aeropuerto.Terminal.FreeShop.FreeShop;

public class Terminal {

    private char idTerminal;
    private PuestoEmbarque puestoEmbarqueGeneral;
    private FreeShop tienda;

    public Terminal(PuestoEmbarque puesto, char id, FreeShop tiendaTerminal){
        this.puestoEmbarqueGeneral = puesto;
        this.idTerminal = id;
        this.tienda = tiendaTerminal;

    }

    public char getIdTerminal(){
        return this.idTerminal;
    }

    public FreeShop getTienda(){
        return this.tienda;
    }

    public PuestoEmbarque getPuestoEmbarqueGeneral(){
        return this.puestoEmbarqueGeneral;
    }
       
}

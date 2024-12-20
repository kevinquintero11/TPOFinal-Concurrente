package Aeropuerto.Terminal;

import java.util.List;

import Aeropuerto.Terminal.FreeShop.FreeShop;

public class Terminal {

    private int idTerminal;
    private List<PuestoEmbarque> listaPuestos;
    private FreeShop tienda;

    public Terminal(List<PuestoEmbarque> puestos, int id, FreeShop tiendaTerminal){
        this.listaPuestos = puestos;
        this.idTerminal = id;
        this.tienda = tiendaTerminal;

    }

    public int getIdTerminal(){
        return this.idTerminal;
    }

    public FreeShop getTienda(){
        return this.tienda;
    }

   
    
}

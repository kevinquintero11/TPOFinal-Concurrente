package Aeropuerto.Terminal;

import java.util.List;

public class Terminal {

    private int idTerminal;
    private List<PuestoEmbarque> listaPuestos;

    public Terminal(List<PuestoEmbarque> puestos, int id){
        this.listaPuestos = puestos;
        this.idTerminal = id;

    }

    public int getIdTerminal(){
        return this.idTerminal;
    }

   
    
}

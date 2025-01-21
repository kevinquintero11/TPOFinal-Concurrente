package Aeropuerto.Aerolinea;

import java.util.List;

public class Aerolinea {

    private String nombreAerolinea;
    private List<Vuelo> listaVuelos;

    public Aerolinea(String nombre, List<Vuelo> vuelos){
        this.nombreAerolinea = nombre;
        this.listaVuelos = vuelos;
    }
    
    public String getNombre(){
        return this.nombreAerolinea;
    }

    public List<Vuelo> getVuelos(){
        return this.listaVuelos;
    }
    
}

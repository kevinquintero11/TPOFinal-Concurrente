package Aeropuerto.Aerolinea;

import java.util.List;

public class Aerolinea {

    private String nombreAerolinea;
    private int idAerolinea;
    private List<Vuelo> listaVuelos;

    public Aerolinea(String nombre, int id, List<Vuelo> vuelos){
        this.nombreAerolinea = nombre;
        this.idAerolinea = id;
        this.listaVuelos = vuelos;
    }
    
    public String getNombre(){
        return this.nombreAerolinea;
    }

    public List<Vuelo> getVuelos(){
        return this.listaVuelos;
    }
    
}

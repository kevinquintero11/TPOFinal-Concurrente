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
    

    
}

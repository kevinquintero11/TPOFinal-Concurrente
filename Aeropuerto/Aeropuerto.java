package Aeropuerto;

import java.util.List;

import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.Terminal.Terminal;
import Tren.Tren;

public class Aeropuerto{
    private String nombreAeropuerto;
    private Tren tren;
    private PuestoInforme puestoInforme;
    private List<Aerolinea> listaAerolineas;
    private List<Terminal> listaTerminales;

    public Aeropuerto(String nombre, List<Aerolinea> aerolineas, List<Terminal> terminales, Tren trencito){
        this.nombreAeropuerto = nombre;
        this.listaAerolineas = aerolineas;
        this.listaTerminales = terminales;
        this.tren = trencito;
        this.puestoInforme = new PuestoInforme();

    }
}
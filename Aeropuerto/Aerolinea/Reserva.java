package Aeropuerto.Aerolinea;
import Aeropuerto.Aeropuerto;
import Aeropuerto.Terminal.Terminal;

public class Reserva {
    private Aerolinea aerolinea;
    private Terminal terminal;
    private Aeropuerto aeropuerto;
    private Vuelo miVuelo;
    private int idReserva;

    public Reserva(Aeropuerto aeropuerto, Aerolinea aero, Vuelo vuelo, int id){
        this.aerolinea = aero;
        this.miVuelo = vuelo;
        this.aeropuerto = aeropuerto;
        this.idReserva = id;
    }

    public Aeropuerto getAeropuerto(){
        return this.aeropuerto;
    }

    public Aerolinea getAerolinea(){
        return this.aerolinea;
    }


    
}

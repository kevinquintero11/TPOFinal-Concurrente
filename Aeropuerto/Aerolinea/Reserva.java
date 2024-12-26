package Aeropuerto.Aerolinea;
import Aeropuerto.Aeropuerto;
import Aeropuerto.Terminal.Terminal;

public class Reserva {
    private Aerolinea aerolinea;
    private Terminal terminal;
    private Aeropuerto aeropuerto;
    private Vuelo miVuelo;
    private int idReserva;

    public Reserva(Aeropuerto aeropuerto, Aerolinea aero, Vuelo vuelo, int id, Terminal term){
        this.aerolinea = aero;
        this.miVuelo = vuelo;
        this.aeropuerto = aeropuerto;
        this.idReserva = id;
        this.terminal = term;
    }

    public Aeropuerto getAeropuerto(){
        return this.aeropuerto;
    }

    public Aerolinea getAerolinea(){
        return this.aerolinea;
    }

    public int getIdReserva(){
        return this.idReserva;
    }

    public Vuelo getVuelo(){
        return this.miVuelo;
    }

    public Terminal getTerminal(){
        return this.terminal;
    }

    
}

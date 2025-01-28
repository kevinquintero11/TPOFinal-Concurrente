package Pasajero;

import java.util.List;
import java.util.Random;

import Aeropuerto.Aeropuerto;
import Aeropuerto.Aerolinea.Reserva;
import Aeropuerto.PuestoAtencion.PuestoAtencion;
import Aeropuerto.Terminal.Terminal;
import Aeropuerto.Terminal.FreeShop.FreeShop;
import Utilidades.Log;

public class Pasajero implements Runnable {

    private Reserva miReserva;
    private int idPasajero;

    public Pasajero(int id, Reserva res){
        this.idPasajero = id;
        this.miReserva = res;
    }

    public int getIdPasajero(){
        return this.idPasajero;
    }

    public void setIdPasajero(int id){
        this.idPasajero = id;
    }

    public Reserva getReserva(){
        return this.miReserva;
    }

    public void setReserva (Reserva res) {
        this.miReserva = res;
    }

    @Override
    public void run() {

        Aeropuerto aeropuerto = this.miReserva.getAeropuerto();
        PuestoAtencion puesto;
            try {
                aeropuerto.ingresarAeropuerto(this);
                puesto = aeropuerto.ingresarPuestoInforme(this);
                puesto.ingresarPuestoAtencion(this);
                List<Object> terminalYPuertoEmbarque = puesto.esperarAtencion(this);
                Terminal terminalVuelo = (Terminal) terminalYPuertoEmbarque.get(0);
                Log.escribir("< Pasajero " + this.idPasajero + " salió del puesto de atencion y se dirige rumbo a la terminal " + terminalVuelo.getIdTerminal());
                aeropuerto.irTerminal(this, terminalVuelo);
                Thread.sleep(1000); // simular viaje a la terminal
                if (Math.abs(aeropuerto.getReloj().getHora() - this.miReserva.getVuelo().getHora()) > 1) {
                    FreeShop tienda = this.miReserva.getTerminal().getTienda();
                    if(tienda.ingresarFreeShop(this)) {
                        Random random = new Random();
                        boolean comprar = random.nextBoolean();
                        if(comprar){
                            double monto = 1000 + (4000 - 1000) * random.nextDouble();
                            tienda.comprar(monto, this);
                        }else{
                            Log.escribir("Pasajero " + this.idPasajero + ": solo observó los productos");
                        }
                        tienda.salirFreeShop(this);
                    }
                }
                if(this.miReserva.getVuelo().inicioViaje()){
                    Log.escribir("Pasajero " + this.idPasajero + ": Perdió el avión con destino: " + this.miReserva.getVuelo().getDestino());
                }else{
                    terminalVuelo.getPuestoEmbarqueGeneral().esperarAbordaje(this.getReserva().getVuelo(), this);
                    Log.escribir("Pasajero " + this.idPasajero + ": Subió al avión con destino: " +  this.miReserva.getVuelo().getDestino() + ". Hora de vuelo: " + this.miReserva.getVuelo().getHora()+ "hs.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }       
    }
}

package Pasajero;

import java.util.List;
import java.util.Random;

import Aeropuerto.Aeropuerto;
import Aeropuerto.Aerolinea.Reserva;
import Aeropuerto.PuestoAtencion.PuestoAtencion;
import Aeropuerto.Terminal.Terminal;
import Aeropuerto.Terminal.FreeShop.FreeShop;
//import Aeropuerto.Terminal.FreeShop.Producto;
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
            aeropuerto.ingresarAeropuerto();
            puesto = aeropuerto.ingresarPuestoInforme(this);
            List<Object> terminalYPuertoEmbarque = puesto.ingresarPuestoAtencion(this); // Intenta ingresar al puesto
            Terminal terminalVuelo = (Terminal) terminalYPuertoEmbarque.get(0);
            aeropuerto.irTerminal(this, terminalVuelo);
            if (Math.abs(aeropuerto.getReloj().getHora() - this.miReserva.getVuelo().getHora()) > 1) {
                FreeShop tienda = this.miReserva.getTerminal().getTienda();
                if(tienda.ingresarFreeShop(this)) {
                    Random random = new Random();
                    boolean comprar = random.nextBoolean();
                    if(comprar){
                        double monto = 1000 + (4000 - 1000) * random.nextDouble();
                        tienda.comprar(monto, this);
                    }else{
                        Log.escribir("Pasajero " + this.getReserva().getIdReserva() + ": solo observó los productos");
                        System.out.println(this.getReserva().getIdReserva() + " solo observo los productos");
                    }
                    tienda.salirFreeShop(this);
                }
            }
           // this.getReserva().getVuelo().esperarAbordaje();
            terminalVuelo.getPuestoEmbarqueGeneral().esperarAbordaje(this.getReserva().getVuelo());
            System.out.println(this.getReserva().getIdReserva() + " Subio al avion. Hora de vuelo: " + this.miReserva.getVuelo().getHora()+ ". Hora aeropuerto: "+aeropuerto.getReloj().getHora());
            Log.escribir("Pasajero " + this.getReserva().getIdReserva() + ": Subio al avion. Hora de vuelo: " + this.miReserva.getVuelo().getHora()+ ". Hora aeropuerto: "+aeropuerto.getReloj().getHora());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }      
    }
}

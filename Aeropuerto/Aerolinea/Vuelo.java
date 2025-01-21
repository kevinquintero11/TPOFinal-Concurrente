package Aeropuerto.Aerolinea;

import java.util.concurrent.CountDownLatch;
import Aeropuerto.Terminal.PuestoEmbarque;
import Utilidades.Log;
import Utilidades.Reloj;

public class Vuelo implements Runnable{

    private int horaDespegue;
    private String destino;
    private PuestoEmbarque puestoSalida;
    private Reloj relojAeropuerto;
    private CountDownLatch latchDespegue;

    public Vuelo(int hs, String nombreDestino, PuestoEmbarque puesto, Reloj reloj, CountDownLatch despegue){
        this.horaDespegue = hs;
        this.destino = nombreDestino;
        this.puestoSalida = puesto;
        this.relojAeropuerto = reloj;
        this.latchDespegue = despegue;
    }

    public String getDestino(){
        return this.destino;
    }

    public PuestoEmbarque getPuestoEmbarque(){
        return this.puestoSalida;
    }

    public int getHora(){
        return this.horaDespegue;
    }

    public void esperarDespegue() throws InterruptedException{
       this.latchDespegue.await();
    }

    @Override
    public void run() {
        try {
            relojAeropuerto.verificarHoraVuelo(horaDespegue);
            Log.escribir("Comienza el abordaje del vuelo con destino: " + this.destino);
            latchDespegue.countDown();
            Thread.sleep(3000);
            Log.escribir("Despega el vuelo con destino: " + this.destino);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

package Utilidades;

import Aeropuerto.Aeropuerto;

// CLASE QUE SIMULA EL RELOJ DEL AEROPUERTO

public class Reloj implements Runnable {
    private int hora;
    private Aeropuerto aeropuerto; 
    
        public Reloj(int hora) {
            this.hora = hora;
        }
    
        public void setAeropuerto(Aeropuerto aero){
            this.aeropuerto = aero;
    }
    public synchronized int getHora() {
        return this.hora;
    }

    public synchronized void avanzarTiempo() {
        if (this.hora == 24) {
            this.hora = 1; 
        } else {
            this.hora++;
        }
        Log.escribir("HORA ACTUAL DEL AEROPUERTO: " + this.hora + ":00hs");
        this.notifyAll(); // Notifica a los hilos vuelo esperando el cambio de hora
    }

    public synchronized void verificarHoraVuelo(int horaVuelo) throws InterruptedException {
        while (this.hora != horaVuelo) {
            this.wait(); // Espera hasta que la hora del vuelo coincida
        }
    }

    private void manejarEstadoAeropuerto() {
        if (hora == 6) {
            aeropuerto.abrirAeropuerto(); // Abrir el aeropuerto a las 6
        } else if (hora == 22) {
            aeropuerto.cerrarAeropuerto(); // Cerrar el aeropuerto a las 22
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000); // Simula 1 hora con un intervalo de 10 segundos
                avanzarTiempo(); // Incrementa la hora
                manejarEstadoAeropuerto(); // Verifica si debe abrir o cerrar el aeropuerto
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    
}

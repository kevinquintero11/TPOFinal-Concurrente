package Utilidades;

import Aeropuerto.Aeropuerto;

public class Reloj implements Runnable {
    private int hora;
    private Aeropuerto aeropuerto; // Referencia al aeropuerto
    
        public Reloj(int hora) {
            this.hora = hora;
           // this.aeropuerto = aeropuerto; // Asignar el aeropuerto
        }
    
        public void setAeropuerto(Aeropuerto aero){
            this.aeropuerto = aero;
    }
    public synchronized int getHora() {
        return this.hora;
    }

    public synchronized void avanzarTiempo() {
        if (this.hora == 22) {
            this.hora = 5; // Reinicia a las 5 para simular el cierre nocturno
        } else {
            this.hora++;
        }
        Log.escribir("Hola actual Aeropuerto: " + this.hora);
        System.out.println("Hora actual: " + this.hora);
        this.notifyAll(); // Notificar a los posibles hilos esperando el cambio de hora
    }

    public synchronized void verificarHoraVuelo(int horaVuelo) throws InterruptedException {
        while (this.hora != horaVuelo) {
            this.wait(); // Esperar hasta que la hora del vuelo coincida
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

    private void manejarEstadoAeropuerto() {
        if (hora == 6) {
            aeropuerto.abrirAeropuerto(); // Abrir el aeropuerto a las 6
        } else if (hora == 22) {
            aeropuerto.cerrarAeropuerto(); // Cerrar el aeropuerto a las 22
        }
    }
}

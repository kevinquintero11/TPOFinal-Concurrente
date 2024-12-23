package Aeropuerto;

import java.util.List;

import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.PuestoAtencion.PuestoAtencion;
import Aeropuerto.Terminal.Terminal;
import Aeropuerto.Tren.Tren;
import Pasajero.Pasajero;
import Utilidades.Log;
import Utilidades.Reloj;

public class Aeropuerto{
    private String nombreAeropuerto;
    private Tren tren;
    private PuestoInforme puestoInforme;
    private List<Aerolinea> listaAerolineas;
    private List<Terminal> listaTerminales;
    private List<PuestoAtencion> listaPuestos;
    private Reloj reloj;
    private boolean abierto = false;

    public Aeropuerto(String nombre, List<Aerolinea> aerolineas, List<Terminal> terminales, List<PuestoAtencion> puestos, Tren trencito){
        this.nombreAeropuerto = nombre;
        this.listaAerolineas = aerolineas;
        this.listaTerminales = terminales;
        this.listaPuestos = puestos;
        this.tren = trencito;
        this.puestoInforme = new PuestoInforme();
        //this.reloj = rel;
    }

    public String getNombre(){
        return this.nombreAeropuerto;
    }

    public void setNombre(String nombre){
        this.nombreAeropuerto = nombre;
    }

    public List<Aerolinea> getAerolineas(){
        return this.listaAerolineas;
    }

    public void setAerolineas(List<Aerolinea> aerolineas){
        this.listaAerolineas = aerolineas;
    }

    public List<Terminal> getTerminales(){
        return this.listaTerminales;
    }

    public void setTerminales (List<Terminal> terminales){
        this.listaTerminales = terminales;
    }

    public PuestoInforme getPuestoInforme(){
        return this.puestoInforme;
    }

    public void setPuestoInforme(PuestoInforme puesto){
        this.puestoInforme = puesto;
    }

    public List<PuestoAtencion> getPuestosAtencion(){
        return this.listaPuestos;
    }

    public Reloj getReloj(){
        return this.reloj;
    }

    public synchronized void ingresarAeropuerto() throws InterruptedException {
        while (!abierto) { // Evaluar la condición
            Log.escribir("El aeropuerto todavia no esta abierto");
            System.out.println("El aeropuerto todavía no está abierto");
            this.wait(); // Los pasajeros esperan aquí
        }
        System.out.println("Pasajero entra al aeropuerto.");
    }

    public void abrirAeropuerto() {
        if (!abierto) { // Evitar notificaciones redundantes si ya está abierto
            abierto = true; // Cambiar el estado
            Log.escribir("El aeropuerto está ahora abierto.");
            System.out.println("El aeropuerto está ahora abierto.");
            notifyAll(); // Despertar a todos los pasajeros que están esperando
        }
    }

    public void cerrarAeropuerto() {
        if (abierto) {
            abierto = false; // Cambiar el estado
            System.out.println("El aeropuerto está ahora cerrado.");
            Log.escribir("El aeropuerto esta ahora cerrado");
        }
    }

    public PuestoAtencion ingresarPuestoInforme(Pasajero pasajero) throws InterruptedException{
       PuestoAtencion puestoAtencion = puestoInforme.atenderPasajero(pasajero, this.listaPuestos);
        return puestoAtencion;
    }

    public void irTerminal(Pasajero pasajero, Terminal terminal) throws InterruptedException{
        this.tren.subir(pasajero);
       // Terminal term = (Terminal) lista.get(0);
        this.tren.bajar(terminal, pasajero);
    }

    
}
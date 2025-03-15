package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import Aeropuerto.Aeropuerto;
import Aeropuerto.Aerolinea.Aerolinea;
import Aeropuerto.Aerolinea.Reserva;
import Aeropuerto.Aerolinea.Vuelo;
import Aeropuerto.PuestoAtencion.Guardia;
import Aeropuerto.PuestoAtencion.Hall;
import Aeropuerto.PuestoAtencion.PuestoAtencion;
import Aeropuerto.Terminal.PuestoEmbarque;
import Aeropuerto.Terminal.Terminal;
import Aeropuerto.Terminal.FreeShop.FreeShop;
import Aeropuerto.Tren.Tren;
import Pasajero.Pasajero;
import Utilidades.Log;
import Utilidades.Reloj;

public class Main {
    
    final static int CANTIDAD_PASAJEROS = 15; 
    final static int CANTIDAD_TERMINALES = 3; // Terminales A, B y C segun enunciado
    final static int CANTIDAD_AEROLINEAS = 1;  
    final static int CANTIDAD_VUELOS = 3; // Cantidad de vuelos por aerolinea
    final static int CANTIDAD_PUESTOSATENCION = CANTIDAD_AEROLINEAS; // Un puesto por cada aerolinea
    final static int CAPACIDAD_PUESTOATENCION = 3; // Capacidad de pasajeros que pueden haber esperando en la cola de cada puesto
    final static int CAPACIDAD_TREN = 5; // Cantidad de pasajeros que soporta el tren
    final static int CAPACIDAD_FREESHOP = 2; // Cantidad de lugares disponibles en las tiendas
    final static List<PuestoEmbarque> listaPuestosEmbarques = new LinkedList<>();
    final static List<Terminal> listaTerminales = new LinkedList<>();
    final static List<Aerolinea> listaAerolineas = new LinkedList<>();
    final static List<PuestoAtencion> listaPuestosAtencion = new LinkedList<>();
    final static List<Pasajero> listaPasajeros = new LinkedList<>();
    final static List<Thread> listaHilos = new LinkedList<>();
    final static Reloj reloj = new Reloj(5);
    
    public static void main(String[] args) {
       
        Log logSistema = new Log();
        Aeropuerto aeropuerto;
        
        crearPuestosEmbarque();
        crearTerminales();
        crearAerolineas();
        Hall hall = new Hall(listaAerolineas);
        crearPuestosAtencion(hall);
        Tren tren = new Tren(CAPACIDAD_TREN, listaTerminales);
        aeropuerto = new Aeropuerto("Viaje Bonito", listaAerolineas, listaTerminales, listaPuestosAtencion, tren, reloj);
        reloj.setAeropuerto(aeropuerto);
        crearPasajeros(aeropuerto);
        

        Thread hiloReloj = new Thread(reloj, "Reloj");
        listaHilos.add(hiloReloj);

        Thread hiloTren = new Thread(tren, "Tren");
        listaHilos.add(hiloTren);


        int cantHilos = listaHilos.size();
        for (int i = 0; i < cantHilos; i++) {
            listaHilos.get(i).start();
        }
    }
    
    public static void crearAerolineas(){
        String[] AEROLINEAS = {
            "Aerolíneas Argentinas", "American Airlines", "British Airways", "Air France",
            "Lufthansa", "Emirates", "Qantas Airways", "Japan Airlines",
            "Iberia", "KLM Royal Dutch Airlines"
        };

        for(int i = 0; i < CANTIDAD_AEROLINEAS; i++){       
            Aerolinea aerolinea = new Aerolinea(AEROLINEAS[i],  null);
            List<Vuelo> listaVuelos = crearVuelos(aerolinea);
            aerolinea.setVuelos(listaVuelos);
            listaAerolineas.add(aerolinea);
        }

    }
    
    public static List<Vuelo> crearVuelos(Aerolinea aerolinea){
          
        List<String> destinosDisponibles = new ArrayList<>(Arrays.asList("Buenos Aires", "Madrid", "Nueva York", "Tokio", "Londres", "París", "Roma", "Sídney", "Dubai", "Berlín"));

        List<Vuelo> listaVuelos = new LinkedList<>();
        Random random = new Random();

        for (int i = 0; i < CANTIDAD_VUELOS && !destinosDisponibles.isEmpty(); i++) {
            int indiceDestino = random.nextInt(destinosDisponibles.size()); 
            String destinoSeleccionado = destinosDisponibles.remove(indiceDestino); 

            int horaSalida = 8 + random.nextInt(14);
            int numAleatorio = random.nextInt(2) + 1;
            PuestoEmbarque puesto = listaPuestosEmbarques.get(numAleatorio);
            CountDownLatch latchDespegue = new CountDownLatch(1);
            int terminalAleatoria = random.nextInt(listaTerminales.size());

            Vuelo vuelo = new Vuelo(horaSalida, destinoSeleccionado, puesto, reloj, latchDespegue, aerolinea, listaTerminales.get(terminalAleatoria));
            Thread hiloVuelo = new Thread(vuelo, "Vuelo " + destinoSeleccionado);
            listaHilos.add(hiloVuelo);
            listaVuelos.add(vuelo);
        }
        return  listaVuelos;
    }

    public static void crearPuestosEmbarque(){
        for(int i = 0; i < CANTIDAD_TERMINALES; i++){
            PuestoEmbarque puerto = new PuestoEmbarque(i);
            listaPuestosEmbarques.add(puerto);
        }
    }

    public static void crearTerminales(){
        char[] nombresTerminales = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
            'U', 'V', 'W', 'X', 'Y', 'Z'
        };     
        
        for(int i = 0; i < CANTIDAD_TERMINALES; i++){
            FreeShop tienda = new FreeShop(CAPACIDAD_FREESHOP,  2, "Tienda Terminal " + nombresTerminales[i]);
            Terminal terminal = new Terminal(listaPuestosEmbarques.get(i), nombresTerminales[i], tienda);
            listaTerminales.add(terminal);
        }
    }

    public static void crearPuestosAtencion(Hall hall){

        for(int i = 0; i < CANTIDAD_PUESTOSATENCION; i++){
            PuestoAtencion puesto = new PuestoAtencion(listaAerolineas.get(i), hall, CAPACIDAD_PUESTOATENCION);
            Thread hiloPuesto = new Thread(puesto, "Puesto " + puesto.getAerolinea().getNombre());
            Guardia guardia = new Guardia(puesto);
            Thread hiloGuardia = new Thread(guardia, "Guardia " + puesto.getAerolinea().getNombre());
            listaPuestosAtencion.add(puesto);
            listaHilos.add(hiloGuardia);
            listaHilos.add(hiloPuesto);
        }
    }

    public static void crearPasajeros(Aeropuerto aeropuerto){
        Random random = new Random();

        for(int i = 0; i < CANTIDAD_PASAJEROS; i++){
            int  numAleatorio = random.nextInt(listaAerolineas.size());
            Aerolinea aerolinea = listaAerolineas.get(numAleatorio);
            int  vueloAleatorio = random.nextInt(aerolinea.getVuelos().size());
            Vuelo vuelo = aerolinea.getVuelos().get(vueloAleatorio);
            Reserva reserva = new Reserva(aeropuerto, aerolinea, vuelo, i+100, vuelo.getTerminal());
            Pasajero pasajero = new Pasajero(i, reserva);
            listaPasajeros.add(pasajero);
            Thread pasajeroHilo = new Thread(pasajero, "Pasajero " + pasajero.getIdPasajero());
           
            // Agrego el pasajero a la lista de hilos
            listaHilos.add(pasajeroHilo);  
        }
    }

}

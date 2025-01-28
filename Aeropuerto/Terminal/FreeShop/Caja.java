package Aeropuerto.Terminal.FreeShop;

// CLASE QUE SIMULA UNA CAJA REGISTRADORA DE UNA TIENDA

public class Caja {
    private int idCaja;
    private double balance;

    public Caja(int id) {
        this.idCaja=id;
        this.balance = 0;
    }
   
    public synchronized void actualizarBalance(double monto) {
        this.balance+= monto;
    }

    public int getIdCaja(){
        return this.idCaja;
    }
}

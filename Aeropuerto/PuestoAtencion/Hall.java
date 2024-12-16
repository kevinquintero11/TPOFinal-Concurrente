package Aeropuerto.PuestoAtencion;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Hall {
    
     private Lock lock = new ReentrantLock(true);
    private Condition[] puestosAtencion;

    public Hall(int cantPuestos) {
        this.puestosAtencion = new Condition[cantPuestos];
        for (int i = 0; i < cantPuestos; i++) {
            puestosAtencion[i] = lock.newCondition();
        }
    }
}

package ejercicios_moodle;

import java.util.Random;

public class NumeroOculto {
    private final int numPropuesto;
    private boolean adivinado = false;

    public NumeroOculto() {
        this.numPropuesto = generarNumeroAleatorio();
    }

    public boolean isAdivinado() {
        return adivinado;
    }

    public synchronized void propuestaNumero(int num) {
        this.adivinado = num == numPropuesto;
    }

    public int getNumPropuesto() {
        return numPropuesto;
    }

    public static int generarNumeroAleatorio(){
        return new Random().nextInt(101);
    }

    public static void main(String[] args) {
        NumeroOculto n = new NumeroOculto();
        Thread[] jugadores = new Thread[10];

        System.out.println("El número para adivinar es el " + n.getNumPropuesto());

        for (int i = 0; i < jugadores.length; i++) {
            int finalI = i;
            jugadores[i] = new Thread(() -> {
                while (!n.isAdivinado()) {
                    int propuesta = generarNumeroAleatorio();
                    System.out.println("El jugador " + finalI + " propone el número " + propuesta);

                    synchronized (n){
                        n.propuestaNumero(propuesta);
                    }
                }
                System.out.println("El jugador " + finalI + " ha adivinado el número " + n.getNumPropuesto());
                synchronized (n){
                    n.adivinado = true;
                }
            });
        }

        for (Thread j : jugadores) {
            j.start();
        }
    }
}

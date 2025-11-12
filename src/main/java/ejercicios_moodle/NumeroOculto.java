package ejercicios_moodle;

import java.util.Random;

public class NumeroOculto {
    private final int numPropuesto;
    private int estado;
    private static boolean adivinado = false;

    public NumeroOculto() {
        this.numPropuesto = generarNumeroAleatorio();
        this.estado = 0;
    }

    public boolean isAdivinado() {
        return adivinado;
    }

    public synchronized int propuestaNumero(int num) {
        if (num == this.numPropuesto && !isAdivinado()) {
            adivinado = true;
            // La variable estado es necesaria para que el bucle while de cada Thread pueda acceder a este valor
            setEstado(1);
            return 1;
        }
        return adivinado ? -1 : 0;
    }

    public int getNumPropuesto() {
        return numPropuesto;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
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
                while (n.getEstado() == 0) {
                    int estado;
                    int propuesta = generarNumeroAleatorio();

                    synchronized (n) {
                         estado = n.propuestaNumero(propuesta);
                    }
                    switch (estado) {
                        case 1:
                            System.out.println("El jugador " + finalI + " ha adivinado el número " + n.getNumPropuesto());
                            break;
                        case -1:
                            Thread.currentThread().interrupt();
                            break;
                        default:
                            System.out.println("El jugador " + finalI + " propone el número " + propuesta);
                            break;
                    }
                }
            });
        }

        for (Thread j : jugadores) {
            j.start();
        }
    }
}

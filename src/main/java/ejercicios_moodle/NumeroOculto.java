package ejercicios_moodle;

import java.util.Random;

public class NumeroOculto {
    private final int numPropuesto;
    private boolean adivinado = false;
    private int estado;

    public NumeroOculto() {
        this.numPropuesto = generarNumeroAleatorio();
    }

    public int getNumPropuesto() {
        return numPropuesto;
    }

    public int getEstado() {
        return estado;
    }

    public void propuestaNumero(int num) {
        if (num == numPropuesto) {
            adivinado = true;
            estado = 1;
        } else {
            estado = adivinado ? -1 : 0;
        }
    }

    public static int generarNumeroAleatorio(){
        return new Random().nextInt(101);
    }

    public static void main(String[] args) {
        NumeroOculto n = new NumeroOculto();
        Thread[] jugadores = new Thread[10];

        for (int i = 0; i < jugadores.length; i++) {
            jugadores[i] = new Thread(() -> {
                n.propuestaNumero(generarNumeroAleatorio());
            });
        }

        for (int i = 0; i < jugadores.length; i++) {
            jugadores[i].start();
        }

        while (true) {
            switch (n.getEstado()) {
                case 1:
                    return;
                case -1:
                    return;
                default:
                    return;
            }
        }
    }
}

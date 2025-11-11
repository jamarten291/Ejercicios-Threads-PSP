package primera_tanda;

import java.util.Random;

class Hilo implements Runnable {
    private final String nombre;
    Hilo(String nombre) {
        this.nombre = nombre;
    }
    @Override
    public void run() {
        System.out.printf("Hola, soy el hilo: %s.\n", this.nombre);
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            int pausa = 10 + r.nextInt(500 - 10);
            System.out.printf("primera_tanda.Hilo: %s empieza la vuelta %d.\n",
                    this.nombre, i+1);

            try {
                Thread.sleep(pausa);
                System.out.printf("primera_tanda.Hilo: %s termina la vuelta %d en %d milisegundos.\n",
                        this.nombre, i+1, pausa);
            } catch (InterruptedException e) {
                System.out.printf("primera_tanda.Hilo %s interrumpido.\n", this.nombre);
            }
        }
        System.out.printf("primera_tanda.Hilo %s terminado.\n", this.nombre);
    }
}
public class LanzaHilosYEsperaQueTerminen {
    public static void main(String[] args) {
        Thread h1 = new Thread(new Hilo("H1"));
        Thread h2 = new Thread(new Hilo("H2"));
        Thread h3 = new Thread(new Hilo("H3"));
        h1.start();
        h2.start();
        h3.start();

        try {
            h1.join();
            h2.join();
            h3.join();
        } catch (InterruptedException e) {
            System.out.println("primera_tanda.Hilo principal interrumpido.");
        }
        System.out.println("primera_tanda.Hilo principal terminado.");
    }
}
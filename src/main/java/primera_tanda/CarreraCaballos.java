package primera_tanda;

import java.util.Random;

class Caballo implements Runnable {
    private int vueltasDadas = 0;
    private static int cont_id = 0;
    private final int ID;
    private final String NOMBRE;
    private final int NUM_VUELTAS;

    public Caballo(int vueltasPorDar) {
        this.ID = cont_id++;
        this.NOMBRE = "primera_tanda.Caballo" + this.ID;
        this.NUM_VUELTAS = vueltasPorDar;
    }

    public String getNombre() {
        return NOMBRE;
    }

    @Override
    public void run() {
        try {
            while (vueltasDadas < NUM_VUELTAS) {
                int randomMilis = new Random().nextInt(100, 200);

                System.out.println("El primera_tanda.Hilo " + this.getNombre() + " se toma un descanso de " + randomMilis + " ms");
                Thread.sleep(randomMilis);
                vueltasDadas++;

                System.out.println("El primera_tanda.Hilo " + this.getNombre() + " finaliza la vuelta " + this.vueltasDadas);
            }
        } catch (InterruptedException e) {
            System.out.printf("El primera_tanda.Hilo %s ha sido interrumpido.\n", this.NOMBRE);
            Thread.currentThread().interrupt();
        }
    }
}

public class CarreraCaballos {
    private static int NUM_CABALLOS = 10;
    private static int NUM_VUELTAS = 10;

    public static void main(String[] args) {
        Thread[] caballos = new Thread[NUM_CABALLOS];

        for (int i  = 0; i < NUM_CABALLOS; i++) {
            caballos[i] = new Thread(new Caballo(NUM_VUELTAS));
            caballos[i].start();
        }

        for (int i = 0; i < NUM_CABALLOS * NUM_VUELTAS / 2; i++) {
            int randomIndex = new Random().nextInt(NUM_CABALLOS - 1);
            caballos[randomIndex].interrupt();
        }

        try {
            for (Thread t : caballos) {
                t.join();
            }
        } catch (InterruptedException e) {
            System.out.println("primera_tanda.Hilo principal interrumpido.");
        }

        System.out.println("Carrera finalizada.");
    }
}
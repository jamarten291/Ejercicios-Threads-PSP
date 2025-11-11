package simulador_puente;

import java.util.Random;

public class SimuladorPuente {
    private static int NUM_PERSONAS=10;
    private static int BALAS = 5;
    private static int MAX_DELAY_SNIPER = 5000;
    private static int MIN_DELAY_SNIPER = 2500;
    private static Random random = new Random();

    public static void main(String[] args) {
        Puente puente = new Puente();
        Thread[] personas = new Thread[NUM_PERSONAS];

        for (int i = 0; i < NUM_PERSONAS; i++) {
            personas[i] = new Persona(puente);
            personas[i].start();
        }

        for (int i = 0; i < BALAS; i++) {
            try {
                Thread.sleep(MIN_DELAY_SNIPER, MAX_DELAY_SNIPER);
                personas[random.nextInt(NUM_PERSONAS)].interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

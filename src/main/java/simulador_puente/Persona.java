package simulador_puente;

import java.util.Random;

public class Persona extends Thread {
    private static int contId = 0;
    private Random r = new Random();

    private final Puente puente;

    private final int ID;
    private final int PESO;
    private final int DELAY_LLEGADA;
    private final int DELAY_CRUCE;
    private final int DELAY_VUELTA;

    public Persona(Puente puente) {
        this.ID = ++contId;
        this.PESO = r.nextInt(40,120);
        this.DELAY_LLEGADA = r.nextInt(500, 1000);
        this.DELAY_CRUCE = r.nextInt(1000,3000);
        this.DELAY_VUELTA = r.nextInt(1000,3000);
        this.puente = puente;
    }

    public int getIdPersona() {
        return ID;
    }

    public int getPeso() {
        return PESO;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(DELAY_LLEGADA);
            synchronized (puente) {
                puente.pasoPersona(this);
            }
            Thread.sleep(DELAY_CRUCE);
            synchronized (puente) {
                puente.terminarPaso(this);
            }
        } catch (InterruptedException e) {
            // Se interrumpe el run y la persona no puede terminar la tarea
            System.out.println("Persona: " + this.getIdPersona() + " ha sido disparada por un francotirador");
        }
    }
}

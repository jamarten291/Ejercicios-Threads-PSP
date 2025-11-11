package simulador_puente;

public class Puente {
    private int numPersonas = 0;
    private int pesoTotal = 0;
    private static final int MAX_PERSONAS = 3;
    private static final int MAX_PESO = 200;

    public synchronized boolean pasoPersona(Persona persona) {
        while (numPersonas >= MAX_PERSONAS || pesoTotal >= MAX_PESO) {
            try {
                wait();
            } catch (InterruptedException e) {
                // Lanza la RuntimeException para que la clase Persona la capture
                throw new RuntimeException(e);
            }
        }
        numPersonas++;
        pesoTotal += persona.getPeso();
        System.out.println("PASO PERSONA: " + persona.getIdPersona() + " CON PESO: " + persona.getPeso());
        return true;
    }

    public synchronized void terminarPaso(Persona persona) {
        numPersonas--;
        pesoTotal -= persona.getPeso();
        System.out.println("Persona: " + persona.getIdPersona() + " CON PESO: " + persona.getPeso() + " ha cruzado el puente");
        notifyAll();
    }
}

package simulador_puente;

public class SimuladorPuente {
    private static int NUM_PERSONAS=10;

    public static void main(String[] args) {
        Puente puente = new Puente();
        for (int i = 0; i < NUM_PERSONAS; i++) {
            new Persona(puente).start();
        }
    }
}

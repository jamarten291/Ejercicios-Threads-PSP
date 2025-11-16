package ejercicios_moodle;

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.ThreadLocalRandom;

class Tool {
    final String name;
    final ReentrantLock lock = new ReentrantLock();

    Tool(String name) { this.name = name; }

    boolean acquire() {
        lock.lock();
        return true;
    }

    boolean tryAcquire(long timeoutMs) throws InterruptedException {
        return lock.tryLock(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    void release() { lock.unlock(); }

    @Override
    public String toString() { return name; }
}

class Agent implements Runnable {
    private final String id;
    private final List<Tool> resources; // debe estar en orden global ascendente
    private final int minMs = 50;
    private final int maxMs = 200;

    Agent(String id, List<Tool> resources) {
        this.id = id;
        // defensiva: asegurar orden por nombre o por referencia (orden global)
        resources.sort(Comparator.comparing(t -> t.name));
        this.resources = List.copyOf(resources);
    }

    private void doWork() throws InterruptedException {
        int ms = ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
        Thread.sleep(ms);
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.printf("[%s] intentando adquirir %s%n", id, resources);
                // Adquirir en orden global
                for (Tool t : resources) {
                    t.acquire();
                    System.out.printf("[%s] adquirió %s%n", id, t);
                }

                // realizar tarea
                System.out.printf("[%s] realizando tarea con %s%n", id, resources);
                doWork();
                System.out.printf("[%s] tarea completada%n", id);

                // liberar en orden inverso
                ListIterator<Tool> it = resources.listIterator(resources.size());
                while (it.hasPrevious()) {
                    Tool t = it.previous();
                    t.release();
                    System.out.printf("[%s] liberó %s%n", id, t);
                }

                // Breve pausa opcional antes de repetir para variar el comportamiento
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 51));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("[%s] interrumpido y finalizando%n", id);
        }
    }
}

public class SimulacionHerramientas {
    public static void main(String[] args) {
        // Crear herramientas (asegurar orden consistente por nombre)
        Tool destornillador = new Tool("Destornillador");
        Tool taladro = new Tool("Taladro");
        Tool alicates = new Tool("Alicates");

        // Definir actividades (listas de herramientas)
        List<Tool> A1tools = Arrays.asList(destornillador, taladro); // A1: destornillador + taladro
        List<Tool> A2tools = Arrays.asList(destornillador, alicates); // A2: destornillador + alicates
        List<Tool> A3tools = Arrays.asList(taladro, destornillador, alicates); // A3: taladro + destornillador + alicates

        // Crear agentes: 2 para A1, 1 para A2, 1 para A3
        Thread a1_1 = new Thread(new Agent("A1-1", new ArrayList<>(A1tools)));
        Thread a1_2 = new Thread(new Agent("A1-2", new ArrayList<>(A1tools)));
        Thread a2   = new Thread(new Agent("A2",   new ArrayList<>(A2tools)));
        Thread a3   = new Thread(new Agent("A3",   new ArrayList<>(A3tools)));

        // Iniciar hilos
        a1_1.start();
        a1_2.start();
        a2.start();
        a3.start();

        // El programa corre indefinidamente; para una demo corta podrías unirte y después interrumpir threads.
    }
}

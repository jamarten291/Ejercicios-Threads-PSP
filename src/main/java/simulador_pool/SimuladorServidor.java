package simulador_pool;

import java.util.Random;

class PoolConexiones {
    final int MAX_CONEXIONES = 3;
    int conexiones = 0;

    private PoolConexiones() {

    }

    public static PoolConexiones getInstance() {
        return new PoolConexiones();
    }

    private int conexionesDisponibles() {
        return MAX_CONEXIONES - conexiones;
    }

    public synchronized void conectar(String thread) throws InterruptedException {
        while (conexiones >= MAX_CONEXIONES) {
            System.out.printf("[%s] esperando conexión...\n", thread);
            wait();
        }
        conexiones++;
        System.out.printf("[%s] obtuvo conexión. Disponibles: %d \n", thread, this.conexionesDisponibles());
    }

    public synchronized void desconectar(String thread) {
        conexiones--;
        System.out.printf("[%s] liberó conexión. Disponibles: %d \n", thread, this.conexionesDisponibles());
        notifyAll();
    }
}

class TareaBBDD extends Thread {
    static int contId = 0;
    final int id;
    final String nombre;
    final PoolConexiones pool;
    final Random r;

    public TareaBBDD(String nombre, PoolConexiones pool) {
        super(nombre);
        this.nombre = nombre;
        this.id = contId++;
        this.pool = pool;
        this.r = new Random();
    }

    @Override
    public void run() {
        while (true) {
            // Intenta conectarse al pool
            try {
                pool.conectar(this.nombre);
            } catch (InterruptedException e) {
                // Si es interrumpida mientras espera, lo informa y termina
                System.out.printf("TIMEOUT: %s ha sido interrumpida mientras esperaba.\n", this.nombre);
                Thread.currentThread().interrupt();
            }

            // Se conecta y hace un sleep simulando una consula SQL compleja
            boolean ok = false;
            System.out.printf("[%s] ejecutando consulta SQL compleja...\n", this.nombre);
            try {
                Thread.sleep(r.nextInt(1000, 2001));
                ok = true;
            } catch (InterruptedException e) {
                // Si es interrumpida mientras realiza una consulta, lo informa
                System.out.printf("TIMEOUT: Query de %s cancelada.\n", this.nombre);
            } finally {
                pool.desconectar(this.nombre);

                // Informa que ha terminado dependiendo del resultado de la consulta
                System.out.printf((ok ?
                        "[%s] terminó correctamente.\n" :
                        "[%s] terminó sin completar la query.\n"),
                        this.nombre);
            }
        }
    }
}

class AdminBBDD implements Runnable {
    TareaBBDD[] listaTareas;
    Random r;

    AdminBBDD(TareaBBDD[] listaTareas) {
        this.listaTareas = listaTareas;
        this.r = new Random();
    }

    @Override
    public void run() {
        while (true) {
            int segundos = r.nextInt(1000,2001);
            try {
                Thread.sleep(segundos);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int index = r.nextInt(listaTareas.length);
            TareaBBDD tareaInterrumpida = listaTareas[index];
            tareaInterrumpida.interrupt();
        }
    }
}

public class SimuladorServidor {
    public static void main(String[] args) {
        TareaBBDD[] tareas = new TareaBBDD[10];
        PoolConexiones pool = PoolConexiones.getInstance();

        for (int i = 0; i < tareas.length; i++) {
            tareas[i] = new TareaBBDD("Tarea-" + i, pool);
        }

        Thread admin = new Thread(new AdminBBDD(tareas));

        for (TareaBBDD t : tareas) {
            t.start();
        }

        admin.start();
    }
}

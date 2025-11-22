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

    public synchronized void conectar(String thread) throws InterruptedException {
        while (conexiones >= MAX_CONEXIONES) {
            System.out.printf("[%s] esperando conexión...\n", thread);
            wait();
        }
        conexiones++;
        System.out.printf("[%s] obtuvo conexión. Disponibles: %d \n", thread, this.conexiones);
    }

    public synchronized void desconectar(String thread) {
//        while (conexiones < 0) {
//            try {
//                wait();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(PoolConexiones.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        conexiones--;
        System.out.printf("[%s] liberó conexión. Disponibles: %d \n", thread, this.conexiones);
        notifyAll();
    }
}

class TareaBBDD extends Thread {
    static int contId = 0;
    final int id;
    final String nombre;
    final PoolConexiones pool;
    final Random r;

    public TareaBBDD(String nombre) {
        super(nombre);
        this.nombre = nombre;
        this.id = contId++;
        this.pool = PoolConexiones.getInstance();
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

                // Informa de que ha terminado dependiendo del code
                System.out.printf((ok ? "[%s] terminó correctamente.\n" :
                        "[%s] terminó sin completar la query"),
                        this.nombre
                );
            }
        }
    }
}

public class SimuladorServidor {
}

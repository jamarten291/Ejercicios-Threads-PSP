package laboratorio;

import java.util.*;

class Tarro {
    static int contId = 0;
    int id;
    String contenido;
    boolean usado;

    Tarro(String contenido) {
        this.id = ++contId;
        this.contenido = contenido;
        this.usado = false;
    }

    public String getContenido() {
        return contenido;
    }

    public int getId() {
        return id;
    }

    public boolean enUso() {
        return usado;
    }

    public void setUsado(boolean enUso) {
        this.usado = enUso;
    }

    public synchronized void usarContenido() {
        while(enUso()) {
            try {
                wait();
            } catch (InterruptedException _) {}
        }
        setUsado(true);
    }

    public synchronized void liberarContenido() {
        while(!enUso()) {
            try {
                wait();
            } catch (InterruptedException _) {}
        }
        setUsado(false);
        notifyAll();
    }
}

class Cientifico extends Thread {
    String nombre;
    List<Tarro> tarros;
    List<Tarro> tarrosEnUso;
    Random r = new Random();

    Cientifico(String nombre, List<Tarro> tarros) {
        this.nombre = nombre;
        this.tarros = tarros;
        this.tarrosEnUso = new ArrayList<>();
    }

    public void realizarExperimento() {
        int cantTarros = r.nextInt(2,4);
        int cont = 0;
        while (cont < cantTarros) {
            int randomIndex = r.nextInt(0, tarros.size());
            if (!tarrosEnUso.contains(tarros.get(randomIndex))) {
                tarrosEnUso.add(tarros.get(randomIndex));
                System.out.println('[' + this.nombre + ']' + " adquirió " + tarros.get(randomIndex).getContenido());
                cont++;
            }
        }
        List<Tarro> aux = new ArrayList<>(tarrosEnUso);
        aux.sort(Comparator.comparing(Tarro::getId));
        tarrosEnUso = List.copyOf(aux);

        tarrosEnUso.forEach(Tarro::usarContenido);
    }

    public void liberar() {
        for (Tarro tarro : tarrosEnUso) {
            tarro.liberarContenido();
            System.out.println('[' + this.nombre + ']' + " liberó " + tarro.getContenido());
        }
        tarrosEnUso = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            System.out.println('[' + this.nombre + ']' + " realiza un experimento...");
            realizarExperimento();
            try {
                Thread.sleep(r.nextInt(1000, 3000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            liberar();
            System.out.println('[' + this.nombre + ']' + " ha terminado un experimento.");
        }
    }
}

public class SimuladorLaboratorio {
    public static void main(String[] args) {
        List<Tarro> tarros = new ArrayList<>();
        tarros.add(new Tarro("Catalizador Alfa"));
        tarros.add(new Tarro("Sulfato Rutilio"));
        tarros.add(new Tarro("Estabilizador Delta"));
        tarros.add(new Tarro("Base Zircón"));
        tarros.add(new Tarro("Ácido Tétrico"));
        tarros.add(new Tarro("Oxidante Kriptón"));
        tarros.add(new Tarro("Cloruro Fénix"));
        tarros.add(new Tarro("Solvente H24"));

        Thread c1 = new Thread(new Cientifico("C1", tarros));
        Thread c2 = new Thread(new Cientifico("C2", tarros));
        Thread c3 = new Thread(new Cientifico("C3", tarros));

        c1.start();
        c2.start();
        c3.start();
    }
}

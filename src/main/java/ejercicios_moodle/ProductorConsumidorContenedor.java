package ejercicios_moodle;

class Container<T> {
    private T value;

    public Container() {
    }

    public synchronized T getValue() {
        while(this.value == null){
            try {
                // Espera a que haya un recurso en la variable "valor"
                wait();
            } catch (InterruptedException _) {}
        }

        T result = this.value;
        this.value = null;

        // Al vaciar la variable y recoger su dato, notifica a los demás hilos
        // Esto para que cualquier productor que esté esperando coloque un valor
        notifyAll();
        return result;
    }

    public synchronized void setValue(T value) {
        while(this.value != null){
            try {
                // En caso de que haya un recurso, espera a que sea liberado
                wait();
            } catch (InterruptedException _) {}
        }

        this.value = value;

        // Notifica que hay un valor en la variable
        // Esto para que cualquier consumidor acceda al valor y lo consuma
        notifyAll();
    }
}

class ConsumerThread extends Thread {
    String name;
    final Container<Integer> container;

    public ConsumerThread(String name, Container<Integer> container) {
        this.name = name;
        this.container = container;
    }

    @Override
    public void run() {
        while (true) {
            Integer data;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException _) {}

            // Se accede al lock del container mediante el bloque synchronized
            synchronized (this.container) {
                data = container.getValue();
            }

            System.out.println(this.name + " ha consumido el dato: " + data);
        }
    }
}

class ProducerThread extends Thread {
    static Integer num = 0;
    String name;
    final Container<Integer> container;

    public ProducerThread(String name, Container<Integer> container) {
        this.name = name;
        this.container = container;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException _) {}

            // Se accede al lock del container mediante el bloque synchronized
            synchronized (this.container) {
                container.setValue(num++);
            }

            System.out.println(this.name + " ha producido el dato: " + num);
        }
    }
}

public class ProductorConsumidorContenedor {
    public static void main(String[] args) {
        Container<Integer> contenedor = new Container<>();

        ConsumerThread c1 = new ConsumerThread("Consumer 1", contenedor);
        ConsumerThread c2 = new ConsumerThread("Consumer 2", contenedor);
        ProducerThread p1 = new ProducerThread("Producer 1", contenedor);
        ProducerThread p2 = new ProducerThread("Producer 2", contenedor);

        c1.start();
        c2.start();
        p1.start();
        p2.start();
    }
}
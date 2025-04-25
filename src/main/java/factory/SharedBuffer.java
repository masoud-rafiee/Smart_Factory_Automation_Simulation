package factory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A bounded buffer where ConveyorBelts put items and Robots take them.
 */
public class SharedBuffer<T> {
    private final BlockingQueue<T> queue;

    public SharedBuffer(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    public void put(T item) throws InterruptedException {
        queue.put(item);
    }

    public T take() throws InterruptedException {
        return queue.take();
    }
}

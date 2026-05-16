package queueadt;

public class Main {

    public static void main(String[] args) {

        QueueADT queue = new LinkedQueue();

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        System.out.println("Queue size: " + queue.size());

        System.out.println("Dequeued: " + queue.dequeue());
        System.out.println("Dequeued: " + queue.dequeue());

        System.out.println("Queue size after dequeue: " + queue.size());

        System.out.println("Is queue empty? " + queue.isEmpty());
    }
}
package queueadt;

public interface QueueADT {
    void enqueue(int x);
    int dequeue();
    boolean isEmpty();
    int size();
}
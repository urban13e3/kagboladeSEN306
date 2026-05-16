package queueadt;

import java.util.ArrayList;

public class ArrayListQueue implements QueueADT {

    private ArrayList<Integer> data;

    public ArrayListQueue() {
        data = new ArrayList<>();
    }

    @Override
    public void enqueue(int x) {
        data.add(x);
    }

    @Override
    public int dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }

        return data.remove(0);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int size() {
        return data.size();
    }
}
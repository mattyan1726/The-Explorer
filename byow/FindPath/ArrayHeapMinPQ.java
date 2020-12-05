package byow.FindPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {

    private ArrayList<Node> items;
    private int size;
    private HashMap<T, Node> list;

    private class Node implements Comparable<Node> {
        T item;
        double priority;
        int index; // the index of the Node in ArrayList items;

        Node(T item, double priority, int index) {
            this.item = item;
            this.priority = priority;
            this.index = index;
        }

        @Override
        /** return negative value if this node has smaller priority */
        public int compareTo(Node other) {
            if (other == null) {
                return 0;
            }
            return Double.compare(this.priority, other.priority);
        }
    }

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        items.add(null);
        list = new HashMap<>();
        size = 0;
    }

    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException("The item already exists.");
        }
        Node newNode = new Node(item, priority, size + 1);
        items.add(newNode);
        list.put(item, newNode);
        size += 1;
        swimUp(newNode);
    }

    private void swimUp(Node n) {
        Node parent = parent(n);
        if (n.compareTo(parent) < 0) {
            swap(n, parent);
            swimUp(n);
        }
    }

    private void swimDown(Node n) {
        Node smallerChild = smallerChild(n);
        if (n.compareTo(smallerChild) > 0) {
            swap(n, smallerChild);
            swimDown(n);
        }
    }

    private void swap(Node a, Node b) {
        items.set(a.index, b);
        items.set(b.index, a);
        int temp = a.index;
        a.index = b.index;
        b.index = temp;
    }

    @Override
    public boolean contains(T item) {
        return list.containsKey(item);
    }

    @Override
    public T getSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException("The list is empty.");
        }
        return items.get(1).item;
    }

    @Override
    public T removeSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException("The list is empty.");
        }
        T smallest = items.get(1).item;
        Node last = items.get(size);
        items.set(1, last);
        last.index = 1;
        items.remove(size);
        list.remove(smallest);
        size -= 1;
        swimDown(last);
        return smallest;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException("The item does not exist.");
        } else {
            Node target = list.get(item);
            double prevPriority = target.priority;
            target.priority = priority;
            if (prevPriority < priority) {
                swimDown(target);
            } else if (prevPriority > priority) {
                swimUp(target);
            }
        }
    }

    private Node leftChild(Node n) {
        int index = n.index * 2;
        if (index > size) {
            return null;
        } else {
            return items.get(index);
        }
    }

    private Node rightChild(Node n) {
        int index = n.index * 2 + 1;
        if (index > size) {
            return null;
        } else {
            return items.get(index);
        }
    }

    private Node parent(Node n) {
        int index = n.index / 2;
        return items.get(index);
    }

    private Node smallerChild(Node n) {
        Node leftChild = leftChild(n);
        Node rightChild = rightChild(n);
        if (leftChild == null) {
            return null;
        } else if (leftChild.compareTo(rightChild) < 0) {
            return leftChild;
        } else {
            return rightChild;
        }
    }
}

package deque;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private class Node {
        public Node prev;
        public T item;
        public Node next;

        public Node(T i, Node n, Node p) {
            item = i;
            next = n;
            prev = p;
        }

    }

    private class LLDIterator implements Iterator<T> {
        private Node cur;

        public LLDIterator() {
            cur = sentF.next;
        }
        @Override
        public boolean hasNext() {
            return cur != sentL;
        }

        @Override
        public T next() {
            T returnItem = cur.item;
            cur = cur.next;
            return returnItem;
        }
    }

    /*The first item, if it exists, is at sentinel.next. */
    private Node sentF;
    private Node sentL;
    private int size;

    public Iterator<T> iterator() {
        return new LLDIterator();
    }

    public LinkedListDeque(T x) {
        sentF = new Node(null, null, null);
        sentL = new Node(null, null, null);
        Node new_node = new Node(x, sentL, sentF);
        sentF.next = new_node;
        sentL.prev = new_node;
        size = 1;
    }

    /* Create an empty list */
    public LinkedListDeque() {
        sentF = new Node(null, null, null);
        sentL = new Node(null, null, null);
        sentF.next = sentL;
        sentL.prev = sentF;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node new_first = new Node(item, sentF.next, sentF);
        sentF.next.prev = new_first;
        sentF.next = new_first;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node new_last = new Node(item, sentL, sentL.prev);
        sentL.prev.next = new_last;
        sentL.prev = new_last;
        size += 1;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentF;
        while (p.next != sentL) {
            p = p.next;
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) return null;
        T item = sentF.next.item;
        sentF.next = sentF.next.next;
        sentF.next.prev = sentF;
        size--;
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) return null;
        T item = sentL.prev.item;
        sentL.prev = sentL.prev.prev;
        sentL.prev.next = sentL;
        size--;
        return item;
    }

    @Override
    public T get(int index) {
        if (index >= size) return null;
        int tmp = index;
        Node cur = sentF.next;
        while(tmp > 0) {
            tmp--;
            cur = cur.next;
        }
        return cur.item;
    }

    /**
     * a private helper method that interacts
     * with the underlying naked recursive data structure.
     */
    private T getRecursive(int cur, int index, Node p) {
        if (cur == index) return p.item;
        return getRecursive(cur + 1, index, p.next);
    }
    public T getRecursive(int index) {
        if (index >= size) return null;
        return getRecursive(0, index, sentF.next);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof LinkedListDeque)) {
            return false;
        }

        LinkedListDeque<T> o = (LinkedListDeque<T>) other;

        if (o.size() != this.size()) {
            return false;
        }

        Node cur_other = o.sentF.next;
        for (T item : this) {
            if (!item.equals(cur_other.item)) {
                return false;
            }
            cur_other = cur_other.next;
        }
        return true;
    }

    public static void main(String[] args) {
        /* Create a list of 1 integer */
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        L.addFirst(15);
        L.addFirst(27);
        L.addLast(39);
        L.printDeque();

        //iteration
        for (int i : L) {
            System.out.println(i);
        }
    }
}

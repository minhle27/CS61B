package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private class Node {
        private Node prev;
        private T item;
        private Node next;

        Node(T i, Node n, Node p) {
            item = i;
            next = n;
            prev = p;
        }

    }

    private class LLDIterator implements Iterator<T> {
        private Node cur;

        LLDIterator() {
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
        Node newFirst = new Node(item, sentF.next, sentF);
        sentF.next.prev = newFirst;
        sentF.next = newFirst;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node newLast = new Node(item, sentL, sentL.prev);
        sentL.prev.next = newLast;
        sentL.prev = newLast;
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
        if (size == 0) {
            return null;
        }
        T item = sentF.next.item;
        sentF.next = sentF.next.next;
        sentF.next.prev = sentF;
        size--;
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T item = sentL.prev.item;
        sentL.prev = sentL.prev.prev;
        sentL.prev.next = sentL;
        size--;
        return item;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int tmp = index;
        Node cur = sentF.next;
        while (tmp > 0) {
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
        if (cur == index) {
            return p.item;
        }
        return getRecursive(cur + 1, index, p.next);
    }
    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursive(0, index, sentF.next);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof ArrayDeque) {
            ArrayDeque<T> o = (ArrayDeque<T>) other;
            if (o.size() != this.size()) {
                return false;
            }
            Iterator<T> it = o.iterator();
            for (T item : this) {
                if (!item.equals(it.next())) {
                    return false;
                }
            }
            return true;
        } else if (other instanceof LinkedListDeque) {
            LinkedListDeque<T> o = (LinkedListDeque<T>) other;
            if (o.size() != this.size()) {
                return false;
            }
            Iterator<T> it = o.iterator();
            for (T item : this) {
                if (!item.equals(it.next())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

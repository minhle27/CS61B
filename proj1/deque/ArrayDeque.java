package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T> {
    private int size;
    private int nextF;
    private int nextL;
    private T[] items;

    private class ADIterator implements Iterator<T> {
        private int cur;
        public ADIterator() {
            cur = findCurFirst();
        }
        @Override
        public boolean hasNext() {
            return cur != nextL;
        }

        @Override
        public T next() {
            T returnItem = items[cur];
            cur++;
            if (cur >= items.length) cur = 0;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new ADIterator();
    }

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextF = 4;
        nextL = 5;
    }

    private int findCurFirst() {
        int curFirst = nextF + 1;
        if (curFirst >= items.length) curFirst = 0;
        return curFirst;
    }

    private int findCurLast() {
        int curLast = nextL - 1;
        if (curLast < 0) curLast = items.length - 1;
        return curLast;
    }

    private double findUsageRatio() {
        return (size * 1.0) / items.length;
    }

    /** Resize the underlying array to a target capacity */
    private void resize(int cap) {
        T[] tmp = (T[]) new Object[cap];
        // copy old array into new a new array
        int curFirst = findCurFirst();
        int curLast = findCurLast();
        int k = 0;
        if (curFirst <= curLast) {
            for (int i = curFirst; i <= curLast; i++) {
                tmp[k++] = items[i];
            }
        }
        else {
            for (int i = curFirst; i < items.length; i++) {
                tmp[k++] = items[i];
            }
            for (int i = 0; i <= curLast; i++) {
                tmp[k++] = items[i];
            }
        }
        nextF = tmp.length - 1;
        nextL = k;
        items = tmp;
    }

    public void addFirst(T item) {
        items[nextF] = item;
        size++;
        nextF--;
        // circulate
        if (nextF < 0) nextF = items.length - 1;
        // need to resize as array is full now
        if (size == items.length) resize(size * 2);
    }

    public void addLast(T item) {
        items[nextL] = item;
        size++;
        nextL++;
        if (nextL >= items.length) nextL = 0;
        if (size == items.length) resize(size * 2);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (size == 0) {
            System.out.println("Empty Deque");
            return;
        }
        for (T item : this) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) return null;
        int curFirst = findCurFirst();
        T firstItem = items[curFirst];
        nextF = curFirst;
        size--;
        if (findUsageRatio() < 0.25 && size > 0) resize(items.length / 2);
        return firstItem;
    }

    public T removeLast() {
        if (size == 0) return null;
        int curLast = findCurLast();
        T lastItem = items[curLast];
        nextL = curLast;
        size--;
        if (findUsageRatio() < 0.25 && size > 0) resize(items.length / 2);
        return lastItem;
    }

    public T get(int index) {
        if (index >= size) return null;
        int curFirst = findCurFirst();
        if (curFirst + index >= items.length) {
            return items[index - (items.length - curFirst)];
        }
        return items[curFirst + index];
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ArrayDeque)) {
            return false;
        }

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
    }


    public static void main(String[] args) {
        /* Create a list of 1 integer */
        ArrayDeque<Character> L = new ArrayDeque<>();
        L.addLast('a');
        L.addLast('b');
        L.addFirst('c');
        L.addLast('d');
        L.addLast('e');
        L.addFirst('f');
        L.addLast('g');
        L.addLast('h');
        L.addLast('z');
        L.addFirst('w');
        L.addFirst('w');
        L.addFirst('w');
        L.addLast('d');
        L.addLast('d');
        L.addLast('d');
        L.addLast('l');
        L.addLast('i');
        L.addLast('k');
        L.addFirst('l');
        L.printDeque();
    }
}

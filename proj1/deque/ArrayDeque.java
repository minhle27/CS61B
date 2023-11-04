package deque;

public class ArrayDeque<T> {
    private int size;
    private int nextF;
    private int nextL;
    private T[] items;
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
        if (curLast < 0) curLast = items.length;
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
        int curFirst = findCurFirst();
        int curLast = findCurLast();
        if (curFirst <= curLast) {
            for (int i = curFirst; i <= curLast; i++) {
                System.out.print(items[i] + " ");
            }
        }
        else {
            for (int i = curFirst; i < items.length; i++) {
                System.out.print(items[i] + " ");
            }
            for (int i = 0; i <= curLast; i++) {
                System.out.print(items[i] + " ");
            }
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) return null;
        int curFirst = findCurFirst();
        nextF = curFirst;
        size--;
        if (findUsageRatio() < 0.25) resize(items.length / 2);
        return items[curFirst];
    }

    public T removeLast() {
        if (size == 0) return null;
        int curLast = findCurLast();
        nextL = curLast;
        size--;
        if (findUsageRatio() < 0.25) resize(items.length / 2);
        return items[curLast];
    }

    public T get(int index) {
        if (index >= size) return null;
        int curFirst = findCurFirst();
        int curLast = findCurLast();
        if (curFirst + index >= items.length) {
            return items[index - (items.length - curFirst)];
        }
        return items[curFirst + index];
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
        System.out.println(L.size());
        for (int i = 0; i < 18; i++) {
            L.removeFirst();
            L.printDeque();
        }
    }
}

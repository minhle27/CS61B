package deque;

public class LinkedListDeque<T> {
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

    /*The first item, if it exists, is at sentinel.next. */
    private Node sentF;
    private Node sentL;
    private int size;

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

    public void addFirst(T item) {
        Node new_first = new Node(item, sentF.next, sentF);
        sentF.next.prev = new_first;
        sentF.next = new_first;
        size += 1;
    }

    public void addLast(T item) {
        Node new_last = new Node(item, sentL, sentL.prev);
        sentL.prev.next = new_last;
        sentL.prev = new_last;
        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = sentF;
        while (p.next != sentL) {
            p = p.next;
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) return null;
        T item = sentF.next.item;
        sentF.next = sentF.next.next;
        sentF.next.prev = sentF;
        size--;
        return item;
    }

    public T removeLast() {
        if (size == 0) return null;
        T item = sentL.prev.item;
        sentL.prev = sentL.prev.prev;
        sentL.prev.next = sentL;
        size--;
        return item;
    }

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

    public static void main(String[] args) {
        /* Create a list of 1 integer */
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        System.out.println("yes");
        L.printDeque();
        L.addFirst(15);
        L.addFirst(27);
        L.addLast(39);
        L.printDeque();
    }
}

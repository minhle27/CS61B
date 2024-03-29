package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> dqComparator;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        dqComparator = c;
    }

    public T max() {
        if (this.size() == 0) {
            return null;
        }
        T curmax = this.get(0);
        for (T item : this) {
            if (dqComparator.compare(curmax, item) < 0) {
                curmax = item;
            }
        }
        return curmax;
    }

    public T max(Comparator<T> c) {
        if (this.size() == 0) {
            return null;
        }
        T curmax = this.get(0);
        for (T item : this) {
            if (c.compare(curmax, item) < 0) {
                curmax = item;
            }
        }
        return curmax;
    }
}

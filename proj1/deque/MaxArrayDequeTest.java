package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    public class Dog {
        private String name;
        private int size;

        public Dog(String n, int s) {
            name = n;
            size = s;
        }

        private static class NameComparator implements Comparator<Dog> {
            public int compare(Dog a, Dog b) {
                return a.name.compareTo(b.name);
            }
        }

        public static Comparator<Dog> getNameComparator() {
            return new NameComparator();
        }

        private static class SizeComparator implements Comparator<Dog> {
            public int compare(Dog a, Dog b) {
                return a.size - b.size;
            }
        }

        public static Comparator<Dog> getSizeComparator() {
            return new SizeComparator();
        }

    }

    @Test
    public void constructorCmpTest() {
        Comparator<Dog> name_cmp = Dog.getNameComparator();
        MaxArrayDeque<Dog> L = new MaxArrayDeque<>(name_cmp);

        L.addFirst(new Dog("Yasuo", 13));
        L.addFirst(new Dog("Leesin", 26));
        L.addLast(new Dog("Zed", 19));
        Dog maxDog = L.max();
        assertEquals(maxDog.name, "Zed");
    }

    @Test
    public void parameterCmpTest() {
        Comparator<Dog> name_cmp = Dog.getNameComparator();
        MaxArrayDeque<Dog> L = new MaxArrayDeque<>(name_cmp);

        L.addFirst(new Dog("Yasuo", 13));
        L.addFirst(new Dog("Leesin", 26));
        L.addLast(new Dog("Zed", 19));

        Comparator<Dog> size_cmp = Dog.getSizeComparator();
        Dog maxDog = L.max(size_cmp);
        assertEquals(maxDog.name, "Leesin");
    }
}

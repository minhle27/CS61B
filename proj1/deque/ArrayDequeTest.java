package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;

public class ArrayDequeTest {
    @Test
    /* Adds a few things to the list, checking isEmpty() and size() are correct,
      finally printing the results. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {
        ArrayDeque<String>  lld1 = new ArrayDeque<String>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        ArrayDeque<String> ad3 = new ArrayDeque<>();
        ArrayDeque<Integer> ad4 = new ArrayDeque<>();
        LinkedListDeque<Integer> lld5 = new LinkedListDeque<>();

        // init
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
            ad2.addLast(i);
            ad3.addLast("abcd");
            ad4.addLast(1000000 - i - 1);
            lld5.addLast(i);
        }

        // Test
        assertTrue(ad1.equals(ad2));
        assertTrue(!ad1.equals(null));
        assertTrue(!ad1.equals(ad3));
        assertTrue(!ad1.equals(ad4));
        assertTrue(!ad1.equals(lld5));
    }

    @Test
    public void randomizedComparisonTest() {
        ArrayDeque<Integer> l1 = new ArrayDeque<>();
        LinkedListDeque<Integer> l2 = new LinkedListDeque<>();
        int N = 5000000;

        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                l1.addFirst(randVal);
                l2.addFirst(randVal);
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                l1.addFirst(randVal);
                l2.addFirst(randVal);
            } else if (operationNumber == 2) {
                // isEmpty
                boolean isEmpty_l1 = l1.isEmpty();
                boolean isEmpty_l2 = l2.isEmpty();
                assertEquals(isEmpty_l1, isEmpty_l2);
            } else if (operationNumber == 3) {
                // size
                int sz_l1 = l1.size();
                int sz_l2 = l2.size();
                assertEquals(sz_l1, sz_l2);
            } else if (operationNumber == 4) {
                // removeFirst
                if (l1.isEmpty()) {
                    assertTrue(l2.isEmpty()); continue;
                }
                int removedFirstItem_l1 = l1.removeFirst();
                int removedFirstItem_l2 = l2.removeFirst();
                assertEquals(removedFirstItem_l1, removedFirstItem_l2);
            } else if (operationNumber == 5) {
                // removeLast
                if (l1.isEmpty()) {
                    assertTrue(l2.isEmpty()); continue;
                }
                int removedLastItem_l1 = l1.removeLast();
                int removedLastItem_l2 = l2.removeLast();
                assertEquals(removedLastItem_l1, removedLastItem_l2);
            } else if (operationNumber == 6) {
                // get
                if (l1.isEmpty()) {
                    assertTrue(l2.isEmpty()); continue;
                }
                int randIndex = StdRandom.uniform(0, l1.size());
                int item_l1 = l1.get(randIndex);
                int item_l2 = l2.get(randIndex);
                assertEquals(item_l1, item_l2);
            }
        }
    }
}

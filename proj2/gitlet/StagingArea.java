package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class StagingArea implements Serializable, Dumpable {
    TreeMap<String, String> addition;
    TreeMap<String, String> removal;

    public StagingArea() {
        addition = new TreeMap<>();
        removal = new TreeMap<>();
    }

    @Override
    public void dump() {
        System.out.printf("Addition Area: %s%nRemoval Area: %s%n", addition, removal);
    }
}

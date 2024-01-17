package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class CommitMapping implements Serializable, Dumpable {
    TreeMap<String, String> mapping;
    public CommitMapping() {
        mapping = new TreeMap<>();
    }

    @Override
    public void dump() {
        System.out.printf("Commit tracked files: %n%s%n", mapping);
    }

    @Override
    public String toString() {
        return mapping.toString();
    }
}

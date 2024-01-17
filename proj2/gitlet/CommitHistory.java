package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommitHistory implements Serializable, Dumpable {
    List<String> curList;
    public CommitHistory() {
        curList = new ArrayList<>();
    }

    @Override
    public void dump() {
        System.out.println("Commit List: \n" + curList);
    }
}

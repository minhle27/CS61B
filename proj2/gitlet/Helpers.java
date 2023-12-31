package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** This class contains useful helper methods
 *
 *  @author Minh Le
 */

public class Helpers {
    public static void saveCommit(Commit obj) throws IOException {
        // TODO (hint: don't forget dog names are unique)
        File saveFile = join(OBJECTS_DIR, sha1(obj.toString()));
        saveFile.createNewFile();
        writeObject(saveFile, obj);
    }
}

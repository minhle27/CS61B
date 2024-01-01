package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** This class contains useful helper methods
 *
 *  @author Minh Le
 */

public class Helpers {
    /* Helper methods that help save things to file system */
    public static void saveCommit(Commit obj) throws IOException {
        File saveFile = join(OBJECTS_DIR, sha1(obj.toString()));
        saveFile.createNewFile();
        writeContents(saveFile, obj.toString());
    }

    public static void saveStaging() {
        writeObject(INDEX_FILE, stagingArea);
    }

    public static void saveBlob(String fileContent, String uid) throws IOException {
        File saveFile = join(OBJECTS_DIR, uid);
        saveFile.createNewFile();
        writeContents(saveFile, fileContent);
    }

    public static void saveHead(String commitId) {
        writeContents(HEAD_FILE, commitId);
    }

    public static void saveMaster(String commitId) throws IOException {
        File saveFile = join(REFS_DIR, "heads", "master");
        saveFile.getParentFile().mkdirs();
        saveFile.createNewFile();
        writeContents(saveFile, commitId);
    }

    /* Helper methods that help load state from file system */
    /**
     * Retrieve current state of the staging area
     */
    public static TreeMap retrieveStagingArea() {
        return readObject(INDEX_FILE, TreeMap.class);
    }

//    public static String retrieveMasterCommit() {
//
//    }
}

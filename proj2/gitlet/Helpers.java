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
        writeObject(saveFile, obj);
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

    public static void saveCommitMapping(String uid, TreeMap<String, String> mappingTree) throws IOException {
        File saveFile = join(OBJECTS_DIR, uid);
        saveFile.createNewFile();
        writeObject(saveFile, mappingTree);
    }

    /* Helper methods that help load state from file system */
    /**
     * Retrieve current state of the staging area
     */
    public static StagingArea retrieveStagingArea() {
        return readObject(INDEX_FILE, StagingArea.class);
    }

    /**
     * Retrieve commitObj from commitId
     */
    public static Commit retrieveCommitObj(String commitId) {
        File savedCommitFile = join(OBJECTS_DIR, commitId);
        return readObject(savedCommitFile, Commit.class);
    }

    /**
     * Retrieve mapping tree of a commit
     */
    public static TreeMap<String, String> retrieveMappingTree(String commitId) {
        Commit curCommit = retrieveCommitObj(commitId);
        String mappingTreeId = curCommit.getMappingTree();

        if (mappingTreeId.isEmpty()) {
            return new TreeMap<>();
        }
        File mappingTreeFile = join(OBJECTS_DIR, mappingTreeId);
        return readObject(mappingTreeFile, TreeMap.class);
    }

    /**
     * Retrieve commitId of head or master pointer
     */
    public static String retrieveHeadCommitID() {
        return readContentsAsString(HEAD_FILE);
    }

    public static String retrieveMasterCommitID() {
        return readContentsAsString(join(REFS_DIR, "heads", "master"));
    }

    /** Retrieve blob id a file in a particular commit */
    public static String getBlobIdOfFileInACommit(String commitId, String filename) {
        TreeMap<String, String> mappingTree = retrieveMappingTree(commitId);
        return mappingTree.get(filename);
    }

    /* Other Helper methods */
    public static void assertInitialized() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void assertFileExists(File target) {
        if (!target.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
    }
}

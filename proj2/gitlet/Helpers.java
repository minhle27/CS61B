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
        File saveFile = join(OBJECTS_DIR, "commit", sha1(obj.toString()));
        saveFile.getParentFile().mkdirs();
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

    public static void saveCommitMapping(String uid, CommitMapping mappingTree) throws IOException {
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
        File savedCommitFile = join(OBJECTS_DIR, "commit", commitId);
        return readObject(savedCommitFile, Commit.class);
    }

    /**
     * Retrieve mapping tree of a commit
     */
    public static CommitMapping retrieveMappingTree(String commitId) {
        Commit curCommit = retrieveCommitObj(commitId);
        String mappingTreeId = curCommit.getMappingTree();

        if (mappingTreeId.isEmpty()) {
            return new CommitMapping();
        }
        File mappingTreeFile = join(OBJECTS_DIR, mappingTreeId);
        return readObject(mappingTreeFile, CommitMapping.class);
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
        CommitMapping mappingTree = retrieveMappingTree(commitId);
        return mappingTree.mapping.get(filename);
    }

    /** Retrieve mapping tree of the head commit */
    public static CommitMapping getMappingOfHead() {
        return retrieveMappingTree(retrieveHeadCommitID());
    }

    /* Assert Helper methods */
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

    /* Other Helper methods */
    /** Print out info of current commit and return its parent commitId */
    public static String printCommitInfo(String curId) {
        Commit cur = retrieveCommitObj(curId);
        System.out.println("===");
        System.out.println("commit " + curId);
        System.out.println("Date: " + cur.getTimestamp());
        System.out.println(cur.getMessage());
        System.out.println();
        return cur.getPar();
    }
}

package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;


import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** This class contains useful helper methods
 *
 *  @author Minh Le
 */

public class Helpers {
    /* Helper methods that help save things to file system */
    public static String getObjectDir(String ObjId) {
        return ObjId.substring(0, 2);
    }

    public static String getObjectFilename(String ObjId) {
        return ObjId.substring(2);
    }
    public static void saveCommit(Commit obj, String commitId) throws IOException {
        File saveFile = join(OBJECTS_DIR, getObjectDir(commitId), getObjectFilename(commitId));
        saveFile.getParentFile().mkdirs();
        saveFile.createNewFile();
        writeObject(saveFile, obj);
    }

    public static void saveStaging() {
        writeObject(INDEX_FILE, stagingArea);
    }

    public static void saveBlob(String fileContent, String uid) throws IOException {
        File saveFile = join(OBJECTS_DIR, getObjectDir(uid), getObjectFilename(uid));
        saveFile.getParentFile().mkdirs();
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
        File saveFile = join(OBJECTS_DIR, getObjectDir(uid), getObjectFilename(uid));
        saveFile.getParentFile().mkdirs();
        saveFile.createNewFile();
        writeObject(saveFile, mappingTree);
    }

    public static void saveCommitsList() {
        writeObject(COMMITS_LIST_FILE, commitHistory);
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
        File savedCommitFile = join(OBJECTS_DIR, getObjectDir(commitId), getObjectFilename(commitId));
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
        File mappingTreeFile = join(OBJECTS_DIR, getObjectDir(mappingTreeId), getObjectFilename(mappingTreeId));
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

    /** Retrieve a list of id of all commits ever made */
    public static CommitHistory getAllCommits() {
        return readObject(COMMITS_LIST_FILE, CommitHistory.class);
    }

    public static String getBlobContent(String blobId) {
        File saveFile = join(OBJECTS_DIR, getObjectDir(blobId), getObjectFilename(blobId));
        return readContentsAsString(saveFile);
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

        StringBuilder formattedDate = new StringBuilder();
        Formatter formatter = new Formatter(formattedDate);
        formatter.format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", cur.getTimestamp());
        System.out.println("Date: " + formattedDate);

        System.out.println(cur.getMessage());
        System.out.println();
        return cur.getPar();
    }

    /** Modify the content of a file in CWD to its version in CommitId
     * If the file does not exist, create a new file
     * */
    public static void changeFileVer(String filename, String commitId) throws IOException {
        CommitMapping cur = retrieveMappingTree(commitId);
        if (!cur.mapping.containsKey(filename)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        String blobId = cur.mapping.get(filename);
        File curFile = join(CWD, filename);
        if (!curFile.exists()) {
            curFile.createNewFile();
        }
        writeContents(curFile, getBlobContent(blobId));
    }

    /** Return the full object id from the abbreviated version */
    public static String getFullId(String objId) {
        String objSubDir = getObjectDir(objId);
        List<String> fileList = plainFilenamesIn(join(OBJECTS_DIR, objSubDir));
        assert fileList != null;
        for (String each : fileList) {
            if (each.startsWith(objId.substring(2))) {
                return objSubDir + each;
            }
        }
        return "";
    }
}

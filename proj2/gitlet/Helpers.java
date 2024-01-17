package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;


import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** This class contains useful helper methods
 *
 *  @author Minh Le
 */

public class Helpers {
    /* Helper methods that help save things to file system */
    /** Given a SHA1 hash, isolates the first two char for dir name and the remaining for file name. */
    public static String getObjectDir(String ObjId) {
        return ObjId.substring(0, 2);
    }

    public static String getObjectFilename(String ObjId) {
        return ObjId.substring(2);
    }

    /** Save a Commit obj in OBJECTS_DIR. */
    public static void saveCommit(Commit obj, String commitId) throws IOException {
        File saveFile = join(OBJECTS_DIR, getObjectDir(commitId), getObjectFilename(commitId));
        saveFile.getParentFile().mkdirs();
        saveFile.createNewFile();
        writeObject(saveFile, obj);
    }

    /** Save the staging area in INDEX. */
    public static void saveStaging() {
        writeObject(INDEX_FILE, stagingArea);
    }

    /** Save Blob. */
    public static void saveBlob(String fileContent, String uid) throws IOException {
        File saveFile = join(OBJECTS_DIR, getObjectDir(uid), getObjectFilename(uid));
        saveFile.getParentFile().mkdirs();
        saveFile.createNewFile();
        writeContents(saveFile, fileContent);
    }

    public static void saveHead(String branchName) {
        writeContents(HEAD_FILE, branchName);
    }

    public static void saveBranch(String commitId, String branchName) throws IOException {
        File saveFile = join(REFS_DIR, "heads", branchName);
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
     * Retrieve commitId of HEAD of the current branch
     */
    public static String retrieveHeadCommitID() {
        String branch = readContentsAsString(HEAD_FILE);
        return getHeadOfBranch(branch);
    }

    /**
     * Retrieve commitId of HEAD of a given branch
     */
    public static String getHeadOfBranch(String branchName) {
        return readContentsAsString(join(REFS_DIR, "heads", branchName));
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

    public static String getCurBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    /* Assert Helper methods */
    public static void assertInitialized() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void assertStringNotEmpty(String message) {
        if (message.isEmpty()) {
            message("Please enter provide a non-empty string.");
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
        return cur.getPar1();
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
        modifyFile(filename, getBlobContent(blobId));
    }

    public static void modifyFile(String filename, String contents) throws IOException {
        File curFile = join(CWD, filename);
        if (!curFile.exists()) {
            curFile.createNewFile();
        }
        writeContents(curFile, contents);
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

    /** Check if a branch name exist */
    public static boolean isBranchExist(String branchName) {
        List<String> allBranchNames = plainFilenamesIn(join(REFS_DIR, "heads"));
        assert allBranchNames != null;
        return allBranchNames.contains(branchName);
    }

    public static List<String> listUntracked() {
        CommitMapping commitMapping = retrieveMappingTree(retrieveHeadCommitID());
        List<String> cwdFiles = plainFilenamesIn(CWD);
        List<String> res = new ArrayList<>();
        assert cwdFiles != null;
        StagingArea cur = retrieveStagingArea();
        for (String filename : cwdFiles) {
            if (!commitMapping.mapping.containsKey(filename) ||
                cur.removal.containsKey(filename)
            ) {
                res.add(filename);
            }
        }
        return res;
    }

    public static void resetToACommit(String commitId) {
        if (!listUntracked().isEmpty()) {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        List<String> cwdFile = plainFilenamesIn(CWD);
        CommitMapping commitMapping = retrieveMappingTree(commitId);
        assert cwdFile != null;
        for (String filename : cwdFile) {
            if (!commitMapping.mapping.containsKey(filename)) {
                restrictedDelete(join(CWD, filename));
            }
            else {
                String content = getBlobContent(commitMapping.mapping.get(filename));
                writeContents(join(CWD, filename), content);
            }
        }
        stagingArea = retrieveStagingArea();
        stagingArea.addition.clear();
        stagingArea.removal.clear();
        saveStaging();
    }

    private static void bfs(String s, Map<String, Integer> color) {
        Queue<String> q = new LinkedList<>();
        Set<String> vis = new TreeSet<>();
        q.add(s);
        vis.add(s);

        while (!q.isEmpty()) {
            String curId = q.remove();
            if (color.containsKey(curId)) {
                int curCol = color.get(curId);
                color.put(curId, curCol + 1);
            } else {
                color.put(curId, 1);
            }
            Commit curCommit = retrieveCommitObj(curId);
            String par1Id = curCommit.getPar1();
            String par2Id = curCommit.getPar2();
            if (!par1Id.isEmpty() && !vis.contains(par1Id)) {
                vis.add(par1Id);
                q.add(par1Id);
            }
            if (!par2Id.isEmpty() && !vis.contains(par2Id)) {
                vis.add(par2Id);
                q.add(par2Id);
            }
        }
    }
    public static String findLCA(String node1, String node2) {
        Map<String, Integer> color = new TreeMap<>();
        bfs(node1, color);
        bfs(node2, color);
        Set<String> notLCA = new TreeSet<>();

        for(Map.Entry<String, Integer> entry : color.entrySet()) {
            if (entry.getValue() == 2) {
                Commit cur = retrieveCommitObj(entry.getKey());
                String par1Id = cur.getPar1();
                String par2Id = cur.getPar2();
                notLCA.add(par1Id);
                notLCA.add(par2Id);
            }
        }

        for(Map.Entry<String, Integer> entry : color.entrySet()) {
            if (entry.getValue() == 2 && !notLCA.contains(entry.getKey())) {
                return entry.getKey();
            }
        }
        return "";
    }

    public static boolean isSE(String s1, String s2) {
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }

    public static boolean isSNE(String s1, String s2) {
        if (s1 == null || s2 == null) return false;
        return !s1.equals(s2);
    }
}

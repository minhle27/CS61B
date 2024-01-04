package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Helpers.*;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Minh Le
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File INDEX_FILE = join(GITLET_DIR, "index");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static StagingArea stagingArea;
    public static final String masterRef = "ref: refs/heads/master\n";

    /** Set up files and directories to persist data */
    public static void setupPersistence() throws IOException {
        GITLET_DIR.mkdir();
        HEAD_FILE.createNewFile();
        OBJECTS_DIR.mkdirs();
        INDEX_FILE.createNewFile();
        REFS_DIR.mkdirs();
    }

    /** Init Gitlet */
    public static void initGitLet() throws IOException {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        setupPersistence();
        Commit initialCommit = new Commit();
        saveCommit(initialCommit);
        String uid = sha1(initialCommit.toString());
        saveHead(uid);
        saveMaster(uid);

        stagingArea = new StagingArea();
        saveStaging();
    }

    /** Add File to staging area */
    public static void addFile(String filename) throws IOException {
        File toAdd = join(CWD, filename);
        assertFileExists(toAdd);
        stagingArea = retrieveStagingArea();
        String currentBlob = readContentsAsString(toAdd);
        String uid = sha1(currentBlob);

        String curBlobID = getBlobIdOfFileInACommit(retrieveMasterCommitID(), filename);
        boolean noChange = false;
        if (uid.equals(curBlobID)) {
            noChange = true;
        }

        if (noChange) {
            if (stagingArea.addition.containsKey(filename)) {
                stagingArea.addition.remove(filename);
                saveStaging();
            }
            else {
                message("No changes in this file.");
            }
            System.exit(0);
        }

        if (stagingArea.addition.containsKey(filename)) {
            String stagedBlobID = stagingArea.addition.get(filename);
            if (uid.equals(stagedBlobID)) {
                message("Already added the same content of this file.");
                System.exit(0);
            }
        }

        stagingArea.addition.put(filename, uid);
        saveBlob(currentBlob, uid);
        saveStaging();
    }

    public static void commit(String message) throws IOException {
        // Handle failure cases
        if (message.isEmpty()) {
            message("Please enter a commit message.");
            System.exit(0);
        }
        stagingArea = retrieveStagingArea();
        if (stagingArea.addition.isEmpty() && stagingArea.removal.isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }

        // Retrieve mapping tree of current commit
        String curCommitId = retrieveHeadCommitID();
        CommitMapping mappingTree = retrieveMappingTree(curCommitId);

        // combine cur commit mapping with staging area
        for(Map.Entry<String, String> entry : stagingArea.addition.entrySet()) {
            String filename = entry.getKey();
            String blob = entry.getValue();
            mappingTree.mapping.put(filename, blob);
        }

        for(Map.Entry<String, String> entry : stagingArea.removal.entrySet()) {
            String filename = entry.getKey();
            mappingTree.mapping.remove(filename);
        }

        String mappingTreeUid = sha1(mappingTree.toString());
        Commit newCommit = new Commit(message, curCommitId, mappingTreeUid);
        saveCommit(newCommit);
        saveCommitMapping(mappingTreeUid, mappingTree);
        stagingArea.addition.clear();
        stagingArea.removal.clear();
        saveStaging();

        // Advances head and master pointers
        String commitId = sha1(newCommit.toString());
        saveHead(commitId);
        saveMaster(commitId);
    }

    public static void rm(String filename) {
        File toRm = join(CWD, filename);
        assertFileExists(toRm);
        stagingArea = retrieveStagingArea();
        CommitMapping headMapping = getMappingOfHead();

        boolean isFailure = true;
        if (stagingArea.addition.containsKey(filename)) {
            stagingArea.addition.remove(filename);
            isFailure = false;
        }
        if (headMapping.mapping.containsKey(filename)) {
            stagingArea.removal.put(filename, headMapping.mapping.get(filename));
            // remove file from cwd
            restrictedDelete(join(CWD, filename));
            isFailure = false;
        }
        if (isFailure) {
            message("No reason to remove the file.");
            System.exit(0);
        }
        saveStaging();
    }

    public static void log() {
        String curId = retrieveHeadCommitID();
        while(!curId.isEmpty()) {
            curId = printCommitInfo(curId);
        }
    }

    public static void globalLog() {
        List<String> allCommits = plainFilenamesIn(join(OBJECTS_DIR, "commit"));
        if (allCommits == null) {
            message("Something went wrong.");
            System.exit(0);
        }
        for (String commitId : allCommits) {
            printCommitInfo(commitId);
        }
    }

    public static void find(String message) {
        List<String> allCommits = plainFilenamesIn(join(OBJECTS_DIR, "commit"));
        if (allCommits == null) {
            message("Something went wrong.");
            System.exit(0);
        }
        System.out.println("Commit with the message: " + message);
        boolean haveCommits = false;
        for (String commitId : allCommits) {
            Commit cur = retrieveCommitObj(commitId);
            if (cur.getMessage().equals(message)) {
                System.out.println(commitId);
                haveCommits = true;
            }
        }
        if (!haveCommits) {
            message("Found no commit with that message.");
        }
    }
}

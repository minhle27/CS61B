package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
    public static final File COMMITS_LIST_FILE = join(OBJECTS_DIR, "commitList");
    public static StagingArea stagingArea;
    public static CommitHistory commitHistory;
    public static final String masterRef = "ref: refs/heads/master\n";

    /** Set up files and directories to persist data */
    public static void setupPersistence() throws IOException {
        GITLET_DIR.mkdir();
        HEAD_FILE.createNewFile();
        OBJECTS_DIR.mkdirs();
        INDEX_FILE.createNewFile();
        REFS_DIR.mkdirs();
        COMMITS_LIST_FILE.createNewFile();
    }

    /** Init Gitlet */
    public static void initGitLet() throws IOException {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        setupPersistence();
        Commit initialCommit = new Commit();
        String uid = sha1(initialCommit.toString());
        saveCommit(initialCommit, uid);
        saveHead(uid);
        saveMaster(uid);

        stagingArea = new StagingArea();
        commitHistory = new CommitHistory();
        commitHistory.curList.add(uid);
        saveStaging();
        saveCommitsList();
    }

    /** Add File to staging area */
    public static void addFile(String filename) throws IOException {
        File toAdd = join(CWD, filename);
        assertFileExists(toAdd);
        stagingArea = retrieveStagingArea();
        String currentBlob = readContentsAsString(toAdd);
        String uid = sha1(currentBlob);

        String curBlobID = getBlobIdOfFileInACommit(retrieveMasterCommitID(), filename);
        boolean noChange = uid.equals(curBlobID);

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

        // retrieve persistence data
        stagingArea = retrieveStagingArea();
        commitHistory = getAllCommits();

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
        String newCommitId = sha1(newCommit.toString());
        saveCommit(newCommit, newCommitId);
        saveCommitMapping(mappingTreeUid, mappingTree);
        stagingArea.addition.clear();
        stagingArea.removal.clear();
        commitHistory.curList.add(newCommitId);
        saveStaging();
        saveCommitsList();

        // Advances head and master pointers
        saveHead(newCommitId);
        saveMaster(newCommitId);
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
        CommitHistory allCommits = getAllCommits();
        if (allCommits.curList.isEmpty()) {
            message("No History.");
            System.exit(0);
        }
        for (String commitId : allCommits.curList) {
            printCommitInfo(commitId);
        }
    }

    public static void find(String message) {
        CommitHistory allCommits = getAllCommits();
        if (allCommits.curList.isEmpty()) {
            message("No commit history.");
            System.exit(0);
        }
        System.out.println("Commit with the message: " + message);
        boolean haveCommits = false;
        for (String commitId : allCommits.curList) {
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

    public static void status() {

    }
}

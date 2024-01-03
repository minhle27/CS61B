package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

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
        Helpers.saveCommit(initialCommit);
        String uid = sha1(initialCommit.toString());
        Helpers.saveHead(uid);
        Helpers.saveMaster(uid);

        /** Set up staging area */
        stagingArea = new StagingArea();
        Helpers.saveStaging();
    }

    /** Add File to staging area */
    public static void addFile(String filename) throws IOException {
        Helpers.assertInitialized();
        File toAdd = join(CWD, filename);
        Helpers.assertFileExists(toAdd);
        stagingArea = Helpers.retrieveStagingArea();
        String currentBlob = readContentsAsString(toAdd);
        String uid = sha1(currentBlob);

        String curBlobID = Helpers.getBlobIdOfFileInACommit(Helpers.retrieveMasterCommitID(), filename);
        boolean noChange = false;
        if (uid.equals(curBlobID)) {
            noChange = true;
        }

        if (noChange) {
            if (stagingArea.addition.containsKey(filename)) {
                stagingArea.addition.remove(filename);
                Helpers.saveStaging();
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
        Helpers.saveBlob(currentBlob, uid);
        Helpers.saveStaging();
    }

    public static void commit(String message) throws IOException {
        // Handle failure cases
        Helpers.assertInitialized();
        if (message.isEmpty()) {
            message("Please enter a commit message.");
            System.exit(0);
        }
        stagingArea = Helpers.retrieveStagingArea();
        if (stagingArea.addition.isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }

        // Retrieve mapping tree of current commit
        String curCommitId = Helpers.retrieveHeadCommitID();
        CommitMapping mappingTree = Helpers.retrieveMappingTree(curCommitId);

        // combine cur commit mapping with staging area
        for(Map.Entry<String, String> entry : stagingArea.addition.entrySet()) {
            String filename = entry.getKey();
            String blob = entry.getValue();
            mappingTree.mapping.put(filename, blob);
        }

        String mappingTreeUid = sha1(mappingTree.toString());
        Commit newCommit = new Commit(message, curCommitId, mappingTreeUid);
        Helpers.saveCommit(newCommit);
        Helpers.saveCommitMapping(mappingTreeUid, mappingTree);
        stagingArea.addition.clear();
        Helpers.saveStaging();

        // Advances head and master pointers
        String commitId = sha1(newCommit.toString());
        Helpers.saveHead(commitId);
        Helpers.saveMaster(commitId);
    }

    public static void rm(String filename) {
        Helpers.assertInitialized();
        File toRm = join(CWD, filename);
        Helpers.assertFileExists(toRm);
        stagingArea = Helpers.retrieveStagingArea();

    }

}

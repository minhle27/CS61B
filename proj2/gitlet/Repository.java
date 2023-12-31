package gitlet;

import java.io.File;
import java.io.IOException;
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
    public static TreeMap<String, String> stagingArea;

    /* TODO: fill in the rest of this class. */
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

        /** Set up staging area */
        stagingArea = new TreeMap<>();
        Helpers.saveStaging();
    }

    /** Add File to staging area */
    public static void addFile(String filename) throws IOException {
        File toAdd = join(CWD, filename);
        if (!toAdd.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        stagingArea = Helpers.retrieveStagingArea();
        String currentBlob = readContentsAsString(toAdd);
        String uid = sha1(currentBlob);

        String masterCommitBlobID = "todo";
        boolean noChange = false;
        if (uid.equals(masterCommitBlobID)) {
            noChange = true;
        }

        if (noChange) {
            if (stagingArea.containsKey(filename)) {
                stagingArea.remove(filename);
                Helpers.saveStaging();
            }
            else {
                message("No changes in this file.");
            }
            System.exit(0);
        }

        if (stagingArea.containsKey(filename)) {
            String stagedBlobID = stagingArea.get(filename);
            if (uid.equals(stagedBlobID)) {
                message("Already added the same content of this file.");
                System.exit(0);
            }
        }

        stagingArea.put(filename, uid);
        Helpers.saveBlob(currentBlob, uid);
        Helpers.saveStaging();
    }
}

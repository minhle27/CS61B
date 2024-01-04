package gitlet;

import java.io.IOException;

import static gitlet.Utils.message;
import static gitlet.Helpers.assertInitialized;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Minh Le
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs("init", args, 1);
                Repository.initGitLet();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs("init", args, 2);
                assertInitialized();
                String filename = args[1];
                Repository.addFile(filename);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                validateNumArgs("commit", args, 2);
                assertInitialized();
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                assertInitialized();
                String fileToRm = args[1];
                Repository.rm(fileToRm);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                assertInitialized();
                Repository.log();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                assertInitialized();
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs("find", args, 2);
                assertInitialized();
                String find_message = args[1];
                Repository.find(find_message);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                assertInitialized();
                Repository.status();
                break;
            case "checkout":
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
            default:
                message("No command with that name exists.");
                break;
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command that is being validated
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            message(String.format("Invalid number of arguments for: %s.", cmd));
            System.exit(0);
        }
    }
}

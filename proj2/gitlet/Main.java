package gitlet;

import java.io.IOException;

import static gitlet.Helpers.*;
import static gitlet.Utils.message;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Minh Le
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                try {
                    Repository.initGitLet();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs("init", args, 2);
                assertInitialized();
                String filename = args[1];
                try {
                    Repository.addFile(filename);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                assertInitialized();
                String message = args[1];
                assertCommitNotEmpty(message);
                try {
                    Repository.commit(message, "");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                assertInitialized();
                if (args.length <= 1 || args.length >= 5) {
                    message("Invalid number of arguments for checkout.");
                    System.exit(0);
                }
                try {
                    Repository.checkout(args);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "branch":
                validateNumArgs("status", args, 2);
                assertInitialized();
                String branchName = args[1];
                assertStringNotEmpty(branchName);
                try {
                    Repository.branch(branchName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                assertInitialized();
                assertStringNotEmpty(args[1]);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                assertInitialized();
                assertStringNotEmpty(args[1]);
                try {
                    Repository.reset(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "merge":
                validateNumArgs("merge", args, 2);
                assertInitialized();
                assertStringNotEmpty(args[1]);
                try {
                    Repository.merge(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Minh Le
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    /** The timestamp of this Commit. */
    private Date timestamp;

    /** The parent reference of this Commit. */
    private String par1;

    private String par2; // for merge commit

    /** The reference of mapping data structure. */
    private String mapping;

    public Commit(String message, String par1, String mapping) {
        this.message = message;
        this.timestamp = new Date();
        this.par1 = par1;
        this.mapping = mapping;
        this.par2 = "";
    }

    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.par1 = "";
        this.par2 = "";
        this.mapping = "";
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getPar1() {
        return par1;
    }

    public String getPar2() {
        return par2;
    }

    public String getMappingTree() {
        return mapping;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Commit Message: ").append(message).append("\n");
        sb.append("Timestamp: ").append(timestamp).append("\n");
        sb.append("Parent Commit: ").append(par1).append("\n");
        sb.append("Commit Mapping: ").append(mapping).append("\n");
        sb.append("2nd Parent Commit: ").append(par2).append("\n");
        return sb.toString();
    }

    @Override
    public void dump() {
        System.out.println(this);
    }
}

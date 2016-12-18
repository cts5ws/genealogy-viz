package model;

/*
 * Links used to represent edges for D3. Source and target are indexes in the node
 * list and the note is used to pass special messages to the front end.
 */
public class Link {
    private int source;
    private int target;
    private String note;

    public Link(int source, int target){
        this.source = source;
        this.target = target;
        this.note = "";
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note){
        this.note = note;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}

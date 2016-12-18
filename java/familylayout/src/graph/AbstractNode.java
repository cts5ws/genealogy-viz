package graph;


/*
 * The AbstractNode class stores the common features of all nodes
 * used to store and represent family data.
 */
public abstract class AbstractNode {

    //x and y location
    private int x;
    private int y;

    //types : family and person
    private String type;

    //uid
    private String id;

    //constructors
    public AbstractNode(int x, int y, String type, String id) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.id = id;

    }

    public AbstractNode(AbstractNode a){
        this.x = a.getX();
        this.y = a.getY();
        this.type = a.getType();
        this.id = getId();
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return "Location: (" + this.getX() + "," + this.getY() + "), ID: " +
                this.getId() + ", Type: " + this.getType();
    }
}

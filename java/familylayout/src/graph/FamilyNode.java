package graph;

import java.util.ArrayList;

/*
 * The FamilyNode class stores information for each family that will be
 * represented on the graph
 */
public class FamilyNode extends AbstractNode {

    //Incoming and outgoing edges
    private ArrayList<PersonNode> in;
    private ArrayList<PersonNode> out;

    //used in BFS layout to store number of hops from origin node
    int numHopsAway;
    //used in BGS layout to store level relative to origin node
    int level;

    //Constructor
    public FamilyNode(int x, int y, String type, String id, ArrayList<PersonNode> in, ArrayList<PersonNode> out) {
        super(x, y, type, id);
        this.in = in;
        this.out = out;
        this.numHopsAway = -99;
        this.level = -99;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNumHopsAway() {
        return numHopsAway;
    }

    public void setNumHopsAway(int numHopsAway) {
        this.numHopsAway = numHopsAway;
    }

    public ArrayList<PersonNode> getIn() {
        return in;
    }

    public void setIn(ArrayList<PersonNode> in) {
        this.in = in;
    }

    public ArrayList<PersonNode> getOut() {
        return out;
    }

    public void setOut(ArrayList<PersonNode> out) {
        this.out = out;
    }

    //equals method compares based on the id of each node
    public boolean equals(Object o){
        if(o instanceof FamilyNode){
            FamilyNode f = (FamilyNode)o;
            return this.getId().equals(f.getId());
        } else {
            return false;
        }
    }

    //sets location of node based on average x and y of incoming and outgoing edges
    public void setCoordinateAverage(){
        int xTotal = 0;
        int yTotal = 0;
        int numEdgesX = 0;
        int numEdgesY = 0;


        for(PersonNode p : this.getIn()){
            if(p.getX() != -1.0){
                System.out.println(p.getX());
                xTotal += p.getX();
                numEdgesX++;
            }
            if(p.getY() != -1.0){
                System.out.println(p.getY());
                yTotal += p.getY();
                numEdgesY++;
            }
        }
        for(PersonNode p : this.getOut()){
            if(p.getX() != -1.0){
                System.out.println(p.getX());
                xTotal += p.getX();
                numEdgesX++;
            }
            if(p.getY() != -1.0){
                System.out.println(p.getY());
                yTotal += p.getY();
                numEdgesY++;
            }
        }

        int newX = (int)(xTotal*1.0 / numEdgesX * 1.0);
        int newY = (int)(yTotal*1.0 / numEdgesY * 1.0);

        this.setX(newX);
        this.setY(newY);
    }

    //sets average based on x location of incoming and outgoing edges
    public void setCoordinateAverageX(){
        int xTotal = 0;
        int numEdgesX = 0;


        for(PersonNode p : this.getIn()){
            if(p.getX() != -1.0){
                xTotal += p.getX();
                numEdgesX++;
            }
        }
        for(PersonNode p : this.getOut()){
            if(p.getX() != -1.0){
                xTotal += p.getX();
                numEdgesX++;
            }
        }

        int newX = (int)(xTotal*1.0 / numEdgesX * 1.0);

        this.setX(newX);
    }

    //sets location based on incoming/outgoing and adjacent nodes
    public void setAverageX(int leftX, int rightX){
        int xTotal = 0;
        int numEdges = 0;

        for(PersonNode p : this.getIn()){
            if(p.getX() != -1){
                xTotal += p.getX();
                numEdges++;
                break;
            }
        }
        for(PersonNode p : this.getOut()){
            if(p.getX() != -1){
                xTotal += p.getX();
                numEdges++;
            }
        }

        xTotal += leftX;
        xTotal += rightX;
        numEdges+=2;

        if(numEdges == 0)
            return;
        int newX = xTotal / numEdges;

        this.setX(newX);
    }

    //calculates number of descendants of a node
    public int getDescTotal(){
        int retVal = 0;
        for(PersonNode p : this.getOut()){
            retVal += p.getNumDesc();
        }
        return retVal;
    }
}

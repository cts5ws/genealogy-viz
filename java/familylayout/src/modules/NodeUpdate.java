package modules;

import graph.FamilyNode;
import graph.PersonNode;

import java.util.ArrayList;

/*
 * This classes houses all functions that modify a pre-existing node. Any code that updates
 * some aspect of a node is stored here
 */
public class NodeUpdate {

    static final int NUM_HOPS_DEFAULT = -99;
    ArrayList<PersonNode> pers;
    ArrayList<FamilyNode> fams;

    public NodeUpdate(ArrayList<PersonNode> pers, ArrayList<FamilyNode> fams){
        this.pers = pers;
        this.fams = fams;
    }

    /*
     * Input :
     * Output :
     * Description : Compares every family against every person and adds the appropriate
     *               incoming and outgoing edges to each node when a matching fam id is found
     */
    public void setNodeEdges(){
        for(FamilyNode fam : this.fams) {
            if(fam != null){
                for (PersonNode per : this.pers) {
                    if(per != null){
                        if (per.getFamC() != null && per.getFamC().equals(fam.getId())) {
                            fam.getOut().add(per);
                            per.getIn().add(fam);
                        }
                        if (per.getFamS() != null && per.getFamS().equals(fam.getId())) {
                            per.getOut().add(fam);
                            fam.getIn().add(per);
                        }
                    }
                }
            }
        }
    }

    /*
     * Input : list of PersonNodes, integer of index of root node
     * Output :
     * Description : initializes numHopsAway and Level for the root node, used in the BFS layout
     */
    public void setHopsAwayInit(ArrayList<PersonNode> p, int rootNode){
        PersonNode root = p.get(rootNode);

        //set level and hop distance for root
        root.setNumHopsAway(0);
        root.setLevel(0);
    }

    /*
     * Input : list of PersonNodes, integer of number of edges to include
     * Output :
     * Description : iterates through nodes numEdges times and calls the appropriate helper method
     *               based on the numHopsAway attribute of each node
     */
    public void setHopsAway(ArrayList<PersonNode> p, int numEdges){
        for(int i = 0; i < numEdges; i++){
            for(PersonNode per : p){
                if(per != null){
                    if(per.getNumHopsAway() == i && !per.getVisited()){
                        if(per.getLevel() > 0)
                            setHopsPos(per, per.getLevel());
                        else if (per.getLevel() < 0)
                            setHopsNeg(per, per.getLevel());
                        else
                            setHopsInit(per, per.getLevel());
                    }
                }
            }

            for(PersonNode per : p){
                if(per != null){
                    per.setVisited(false);
                }
            }
        }
    }

    /*
     * Input : a PersonNode and an integer corresponding to the current level
     * Output :
     * Description : Updates level and numHopsAway of each node either incoming or outgoing from p.
     *               This function is the first one called on the root. numHopsAway is updated by
     *               one for all, but level is adjusted relative to the origin node being at level 0.
     */
    public void setHopsInit(PersonNode p, int level){
        for(FamilyNode fam : p.getIn()){
            if(fam.getNumHopsAway() == NUM_HOPS_DEFAULT){
                fam.setNumHopsAway(level + 1);
                fam.setLevel(level + 1);
            }
            for(PersonNode per : fam.getIn()){
                if(per.getNumHopsAway() == NUM_HOPS_DEFAULT){
                    per.setNumHopsAway(level + 1);
                    per.setLevel(level + 1);
                    per.setVisited(true);
                }
            }
        }
        for(FamilyNode fam : p.getOut()){
            if(fam.getNumHopsAway() == NUM_HOPS_DEFAULT) {
                fam.setNumHopsAway(level + 1);
                fam.setLevel(level - 1);
            }
            for(PersonNode per : fam.getOut()){
                if(per.getNumHopsAway() == NUM_HOPS_DEFAULT){
                    per.setNumHopsAway(level + 1);
                    per.setLevel(level - 1);
                    per.setVisited(true);
                }
            }
        }
    }

    /*
     * Input : a PersonNode and a specific level
     * Output :
     * Description : same situation as init method above, but used when level is positive number
     */
    public void setHopsPos(PersonNode p, int level){
        for(FamilyNode fam : p.getIn()){
            if(fam.getNumHopsAway() == NUM_HOPS_DEFAULT){
                fam.setNumHopsAway(level + 1);
                fam.setLevel(level + 1);
            }
            for(PersonNode per : fam.getIn()){
                if(per.getNumHopsAway() == NUM_HOPS_DEFAULT){
                    per.setNumHopsAway(level + 1);
                    per.setLevel(level + 1);
                    per.setVisited(true);
                }
            }
        }
        for(FamilyNode fam : p.getOut()){
            if(fam.getNumHopsAway() == NUM_HOPS_DEFAULT){
                fam.setNumHopsAway(level + 1);
                fam.setLevel(level);
            }
            for(PersonNode per : fam.getOut()){
                if(per.getNumHopsAway() == NUM_HOPS_DEFAULT){
                    per.setNumHopsAway(level + 1);
                    per.setLevel(level - 1);
                    per.setVisited(true);
                }
            }
        }
    }

    /*
     * Input : a PersonNode and a specific level
     * Output :
     * Description : same situation as init method, but used when level is negative number
     */
    public void setHopsNeg(PersonNode p, int level){
        for(FamilyNode fam : p.getIn()){
            if(fam.getNumHopsAway() == NUM_HOPS_DEFAULT){
                fam.setNumHopsAway(Math.abs(level) + 1);
                fam.setLevel(level);
            }
            for(PersonNode per : fam.getIn()) {
                //System.out.println(per.getName() + " " + per.getNumHopsAway() + " " + per.getLevel());
                if (per.getNumHopsAway() == NUM_HOPS_DEFAULT) {
                    per.setNumHopsAway(Math.abs(level)+ 1);
                    per.setLevel(level + 1);
                    per.setVisited(true);
                }
            }
        }
        for(FamilyNode fam : p.getOut()){
            if(fam.getNumHopsAway() == NUM_HOPS_DEFAULT){
                fam.setNumHopsAway(Math.abs(level) + 1);
                fam.setLevel(level - 1);
            }
            for(PersonNode per : fam.getOut()){
                if(per.getNumHopsAway() == NUM_HOPS_DEFAULT){
                    per.setNumHopsAway(Math.abs(level) + 1);
                    per.setLevel(level - 1);
                    per.setVisited(true);
                }
            }
        }
    }
}

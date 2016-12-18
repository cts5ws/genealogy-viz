package graph;

import model.Birthday;
import java.util.ArrayList;

/*
 * The PersonNode class is used to store and represent people in a family history layout
 */
public class PersonNode extends AbstractNode implements Comparable<PersonNode>{

    //birthday information
    private Birthday birthday;

    //family id shared by siblings
    private String famC;

    //family id shared by spouses
    private String famS;

    //full name
    private String name;

    //gender
    private String sex;

    //incoming and outgoing family nodes
    private ArrayList<FamilyNode> in;
    private ArrayList<FamilyNode> out;

    //default 0, used when node is graphed multiple times
    private int copyNum;

    //used in BFS layout to determine number of hops from origin node
    private int numHopsAway;

    //used in BFS layout to determine level relative to origin
    private int level;

    //used by BFS layout to determine if a node has been checked yet
    private boolean visited;

    //min and max X value used when determining location
    private int minX;
    private int maxX;

    //number of descendants
    private int numDesc;

    //spouse
    private PersonNode spouse;

    public PersonNode(int x, int y, String type, String id, ArrayList<FamilyNode> in, ArrayList<FamilyNode> out,
                      Birthday birthday, String famC, String famS, String name, String sex) {
        super(x, y, type, id);
        this.in = in;
        this.out = out;
        this.birthday = birthday;
        this.famC = famC;
        this.famS = famS;
        this. name = name;
        this.sex = sex;
        this.copyNum = 0;
        this.numHopsAway = -99;
        this.level = -99;
        this.visited = false;
        this.minX = -1;
        this.maxX = -1;
        this.numDesc = -1;
        this.spouse = null;
    }

    public PersonNode(PersonNode p){
        super(p.getX(), p.getY(), p.getType(), p.getId());
        this.in = p.getIn();
        this.out = p.getOut();
        this.birthday = p.getBirthday();
        this.famC = p.getFamC();
        this.famS = p.getFamS();
        this.name = p.getName();
        this.sex = p.getSex();
        this.copyNum = p.getCopyNum();
        this.numHopsAway = p.getNumHopsAway();
        this.level = p.getLevel();
        this.visited = p.getVisited();
        this.minX = p.getMinX();
        this.maxX = p.getMaxX();
        this.numDesc = p.getNumDesc();
        this.spouse = p.getSpouse();
    }

    public PersonNode getSpouse(){
        return this.spouse;
    }

    public void setSpouse(PersonNode spouse){
        this.spouse = spouse;
    }

    public int getNumDesc(){
        return numDesc;
    }

    public void setNumDesc(int numDesc){
        this.numDesc = numDesc;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public boolean getVisited() {
        return  this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
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

    public int getCopyNum(){
        return copyNum;
    }

    public void setCopyNum(int copyNum) {
        this.copyNum = copyNum;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public void setBirthday(Birthday birthday) {
        this.birthday = birthday;
    }

    public String getFamC() {
        return famC;
    }

    public void setFamC(String famC) {
        this.famC = famC;
    }

    public String getFamS() {
        return famS;
    }

    public void setFamS(String famS) {
        this.famS = famS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public ArrayList<FamilyNode> getIn() {
        return in;
    }

    public void setIn(ArrayList<FamilyNode> in) {
        this.in = in;
    }

    public ArrayList<FamilyNode> getOut() {
        return out;
    }

    public void setOut(ArrayList<FamilyNode> out) {
        this.out = out;
    }

    @Override
    public String toString(){
        return "Location: (" + this.getX() + "," + this.getY() + "), ID: " +
                this.getId() + ", Type: " + this.getType() + ", In: " + this.getIn() +
                ", Out: " + this.getOut() + ", famC: " + this.getFamC() + ", famS: " + this.getFamS()
                + ", Name: " + this.getName() + ", Sex: " + this.getSex() + ", " + this.copyNum;
    }

    //equals method based on ID
    public boolean equals(Object o){
        if(o instanceof PersonNode){
            PersonNode p = (PersonNode)o;
            return p.getId().equals(this.getId());
        } else
            return false;
    }

    //calculates number of descendants for a person
    public static int calcNumDesc(PersonNode p){
        int numDesc = 0;
        for(FamilyNode f : p.getOut()){
            for(PersonNode per : f.getOut()){
                if(per.getOut() != null){
                    numDesc += calcNumDesc(per);
                }
            }
        }
        return numDesc == 0 ? 1 : numDesc;
    }

    //finds spouse given a list of potential candidates
    public void updateSpouse(ArrayList<PersonNode> p){
        for(PersonNode per : p){
            if(this.getFamS() != null && this.getSpouse() == null && !this.equals(per) && this.getFamS().equals(per.getFamS())){
                this.setSpouse(per);
                per.setSpouse(this);
            }
        }

    }

    public int compareTo(PersonNode personNode){
        return (this.getX() - personNode.getX());
    }

}

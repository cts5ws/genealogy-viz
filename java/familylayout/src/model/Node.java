package model;


/*
 * The Node class is used for writing JSON that is used by D3, the front end javascript library.
 */
public class Node {
    //fulle name
    private String name;

    //x and y coordinate
    private double x;
    private double y;

    //identifcation number of person and family nodes
    private String id;

    //family id shared by siblings
    private String famC;

    //family id shared by spouses
    private String famS;

    //index in the list of nodes
    private String index;

    //initials which are derived from name
    private String initials;

    private String gender;
    private Birthday birthday;

    //copy number used when a node is graphed more than once
    private int copyNum;


    public Node(String name, double x, double y, String id, String famC, String famS, String index, String initials, String gender, Birthday birthday, int copyNum){
        this.name = name;
        this.x = x;
        this.y = y;
        this.id = id;
        this.famC = famC;
        this.famS = famS;
        this.index = index;
        this.initials = initials;
        this.gender = gender;
        this.birthday = birthday;
        this.copyNum = copyNum;
    }

    public Node(Node n){
        this.name = n.getName();
        this.x = n.getX();
        this.y = n.getY();
        this.id = n.getId();
        this.famC = n.getFamC();
        this.famS = n.getFamS();
        this.index = n.getIndex();
        this.initials = n.getInitials();
        this.gender = n.getGender();
        this.birthday = n.getBirthday();
        this.copyNum = n.getCopyNum();
    }

    public boolean equals(Object o){
        if(o instanceof Node){
            Node n = (Node)o;

            return this.getId().equals(n.getId());

        } else {
            return false;
        }
    }

    public int getCopyNum() {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInitials(){
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}

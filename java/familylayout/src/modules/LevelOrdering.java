package modules;

import graph.FamilyNode;
import graph.PersonNode;
import model.Node;
import util.FamilyNodeUpdate;
import util.PersonNodeUpdate;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/*
 * This class houses all code that in some way modifies the different levels of person and
 * family nodes that are eventually used to determine location when graphing
 */
public class LevelOrdering {

    /*
     * pLevels contains all levels of PersonNodes in the graph. It maps a level number to an ArrayList of nodes
     * in that level
     */
    Map<Integer, ArrayList<PersonNode>> pLevels;

    /*
     * fLevels contains all levels of FamilyNodes in the graph. It maps a level number to an ArrayList of nodes
     * in that level
     */
    Map<Integer, ArrayList<FamilyNode>> fLevels;

    //pCurrent is used temporarily when updating pLevel
    ArrayList<PersonNode> pCurrent;

    //fCurrent is used temporarilyy when updating fLevel
    ArrayList<FamilyNode> fCurrent;

    public LevelOrdering(Map<Integer, ArrayList<PersonNode>> pLevels,
                         Map<Integer, ArrayList<FamilyNode>> fLevels,
                         ArrayList<PersonNode> pCurrent,
                         ArrayList<FamilyNode> fCurrent){
        this.pLevels = pLevels;
        this.fLevels = fLevels;
        this.pCurrent = pCurrent;
        this.fCurrent = fCurrent;
    }

    /*
     * Input : list of PersonNodes p and index of the queried descendant from the list p
     * Output :
     * Description : This function initializes the family tree by adding the descendant node to the first level of
     *              the tree
     */
    public void setpLevelsInit(final ArrayList<PersonNode> p, final int descendentIndex){
        pCurrent.add(p.get(descendentIndex));
        pLevels.put(0, new ArrayList<PersonNode>(){{add(p.get(descendentIndex));}});
    }

    /*
     * Input :
     * Output :
     * Description : This function adds nodes to the appropriate level of a descendancy tree. In each
     *               iteration the the appropriate PersonNodes and FamilyNodes are added. It works by adding
     *               outgoing edges from the current level to the next and iterating until nodes with no
     *               outgoing edges are reached.
     */
    public void setLevelsD(){
        int iter= 0;
        while(true){
            if(iter % 2 == 0){ //current = person nodes
                ArrayList<FamilyNode> next = new ArrayList<>();
                for(PersonNode per : pCurrent){
                    for(FamilyNode fam : per.getOut()){
                        if(!next.contains(fam)){
                            next.add(fam);
                        }
                    }
                }

                if(next.isEmpty())
                    break;//exit if no nodes in the next level
                else{
                    fLevels.put(pLevels.size() - 1, next);
                    pCurrent.clear();
                    fCurrent.addAll(next);
                }
            } else {//current = family nodes
                ArrayList<PersonNode> next = new ArrayList<>();
                for(FamilyNode fam : fCurrent){
                    ArrayList<PersonNode> noKids = new ArrayList<>();
                    int famCIndex = next.size();
                    for(PersonNode per : fam.getOut()){
                        if(!next.contains(per)){
                            per.setLevel(iter/2 + 1);
                            if(!per.getOut().isEmpty())
                                next.add(per);
                            else
                                noKids.add(per);
                        }
                    }
                    //puts nodes with no children on the edges of each famC list
                    for(int i = 0; i < noKids.size(); i++){
                        if(i % 2 == 0)
                            next.add(famCIndex, noKids.get(i));
                        else
                            next.add(noKids.get(i));
                    }
                }

                if(next.isEmpty())
                    break;
                else{
                    pLevels.put(fLevels.size(), next);
                    fCurrent.clear();
                    pCurrent.addAll(next);
                }
            }
            iter++;
        }
    }

    /*
     * Input :
     * Output :
     * Description : This function works identically to the function above, except in this case for an ancestry
     *               tree. Instead of identifying nodes closer to the present, this function takes the queried node
     *               and searches backwards for their ancestors.
     */
    public void setLevelsA(){
        int iter= 0;
        while(true){
            if(iter % 2 == 0){ //current = person nodes
                ArrayList<FamilyNode> next = new ArrayList<>();
                for(PersonNode per : pCurrent){
                    for(FamilyNode fam : per.getIn()){
                        if(!next.contains(fam)){
                            next.add(fam);
                        }
                    }
                }

                if(next.isEmpty())
                    break;
                else{
                    fLevels.put(pLevels.size() - 1, next);
                    pCurrent.clear();
                    fCurrent.addAll(next);
                }
            } else {//current = family nodes
                ArrayList<PersonNode> next = new ArrayList<>();
                for(FamilyNode fam : fCurrent){
                    ArrayList<PersonNode> noKids = new ArrayList<>();
                    int famCIndex = next.size();
                    for(PersonNode per : fam.getIn()){
                        if(!next.contains(per)){
                            if(!per.getOut().isEmpty())
                                next.add(per);
                            else
                                noKids.add(per);
                        }
                    }
                    //puts nodes with no children on the edges of each famC list
                    for(int i = 0; i < noKids.size(); i++){
                        if(i % 2 == 0)
                            next.add(famCIndex, noKids.get(i));
                        else
                            next.add(noKids.get(i));
                    }
                    //next.addAll(noKids);
                }

                if(next.isEmpty())
                    break;
                else{
                    pLevels.put(fLevels.size(), next);
                    fCurrent.clear();
                    pCurrent.addAll(next);
                }
            }
            iter++;
        }
    }

    /*
     * Input :
     * Output :
     * Description : Abnormal family relationships disrupt the placement logic used by the functions above. When
     *               an individual marries a family member their offspring tend to be place in the wrong generation.
     *               This function removes duplicate occurances in a descendancy tree.
     */
    public void removeDuplicatesD(){
        //fixes generation skipping issue. removes any nodes from one level up that are in the current level
        for(int i = pLevels.keySet().size() - 1; i > 0; i--){
            ArrayList<PersonNode> current = pLevels.get(i);
            ArrayList<PersonNode> oneUp = pLevels.get(i-1);
            for(PersonNode per : current){
                if(oneUp.contains(per)){
                    pLevels.get(i-1).remove(per);
                }
            }
        }
        //same as above but for familys
        for(int i = fLevels.keySet().size() - 1; i > 0; i--){
            ArrayList<FamilyNode> current = fLevels.get(i);
            ArrayList<FamilyNode> oneUp = fLevels.get(i-1);
            for(FamilyNode fam : current){
                if(oneUp.contains(fam)){
                    fLevels.get(i-1).remove(fam);
                }
            }
        }
    }

    /*
     * Input :
     * Output :
     * Description : This function removes FamilyNodes from the graph that do not have any outgoing edges.
     *               This is done in an effort to reduce clutter on the screen
     */
    public void removeLeafFamsD(){
        //removes family's with no children
        for(int i = 0; i < fLevels.keySet().size(); i++){
            ArrayList<FamilyNode> toBeRemoved = new ArrayList<>();
            for(FamilyNode fam : fLevels.get(i)){
                if(fam.getOut().isEmpty()){
                    toBeRemoved.add(fam);
                }
            }

            for(FamilyNode fam : toBeRemoved){
                fLevels.get(i).remove(fam);
            }
        }
    }

    /*
     * Input :
     * Output :
     * Description : This function works identically to the removeDuplicatesD function except this
     *               function is designed to work specifically for an ancestry tree.
     */
    public void removeDuplicatesA(){
        //fixes generation skipping issue. removes any nodes from one level up that are in the current level
        for(int i =  0; i < pLevels.keySet().size() - 1; i++){
            ArrayList<PersonNode> current = pLevels.get(i);
            ArrayList<PersonNode> oneDown = pLevels.get(i+1);
            for(PersonNode per : current){
                if(oneDown.contains(per)){
                    pLevels.get(i+1).remove(per);
                }
            }
        }
        //same as above but for familys
        for(int i =  0; i < fLevels.keySet().size() -1; i++){
            ArrayList<FamilyNode> current = fLevels.get(i);
            ArrayList<FamilyNode> oneDown = fLevels.get(i+1);
            for(FamilyNode fam : current){
                if(oneDown.contains(fam)){
                    fLevels.get(i+1).remove(fam);
                }
            }
        }
    }

    /*
     * Input :
     * Output :
     * Description : This function works identically to the removeLeafFamsD except that
     *               this function is used for a ancestry tree.
     */
    public void removeLeafFamsA(){
        for(int i = 0; i < fLevels.keySet().size(); i++){
            ArrayList<FamilyNode> toBeRemoved = new ArrayList<>();
            for(FamilyNode fam : fLevels.get(i)){
                if(fam.getOut().isEmpty()){
                    toBeRemoved.add(fam);
                }
            }

            for(FamilyNode fam : toBeRemoved){
                fLevels.get(i).remove(fam);
            }
        }
    }

    /*
     * Input : list of PersonNodes p
     * Output :
     * Description : This functions handles a generation span (people of different generations
     *               within the same family marrying) by creating two copies of the same node
     *               and placing them in both roles they have in the family. Typically these roles
     *               are offspring and spouse. The copyNum attribute is passed to the front end and
     *               upon mouseover both copies of the nodes have their names highlighted. They are
     *               also connected  by a blue line to indicate they represent the same individual.
     */
    public void handleGenerationSpan(ArrayList<PersonNode> p){

        int counter, currentLevel;

        Scanner scanner = new Scanner(System.in);
        //finds nodes that are not at the level they should be
        //creates a new node and inserts that at the appropriate spot
        System.out.println("Do you want to handle generation spanning?");
        String spanningAnswer = scanner.next();

        if(spanningAnswer.toLowerCase().equals("yes")){
            //Handle generation spanning here
            ArrayList<PersonNodeUpdate> spans = new ArrayList<>();
            for(int i = 0; i < pLevels.size(); i++){
                for(PersonNode per : pLevels.get(i)){
                    for(FamilyNode fam : per.getOut()){
                        for(int j = 0; j < fLevels.size(); j++){
                            for(FamilyNode fam1 : fLevels.get(j)){
                                if(fam.equals(fam1) && i != j){
                                    spans.add(new PersonNodeUpdate(j, 0, per));
                                }
                            }
                        }
                    }
                }
            }
            //reiterates to find where new copy should be placed
            for(PersonNodeUpdate per : spans){
                for(PersonNode per1: pLevels.get(per.getLevel())){
                    if(per1.getFamS() != null && per.getNode().getFamS() != null && per1.getFamS().equals(per.getNode().getFamS())){
                        per.setIndex(pLevels.get(per.getLevel()).indexOf(per1));
                    }
                }
            }

            counter = 0;
            currentLevel = 0;
            //for each span, add copy and adjust in/out fields
            for(PersonNodeUpdate per : spans){
                if(currentLevel != per.getLevel()){
                    counter = 0;
                    currentLevel = per.getLevel();
                }
                PersonNode copyNode = new PersonNode(per.getNode());
                copyNode.setCopyNum(1);
                copyNode.setIn(new ArrayList<FamilyNode>());
                p.add(copyNode);
                per.getNode().setOut(new ArrayList<FamilyNode>());
                pLevels.get(per.getLevel()).add(per.getIndex() + counter, copyNode);
                counter++;
            }
        }
    }

    /*
     * Input : list of PersonNodes p
     * Output :
     * Description : This function contains all code used to handle spouses. The two options available are
     *               uniform spacing and non-uniform spacing. In uniform spacing the spouses are treated like
     *               any other node in the level and spaced accordingly. In non-uniform spacing the nodes are
     *               graphed directly beside their spouse. Actual coordinate assignment is not handled in this
     *               portion of the code.
     */
    public ArrayList<PersonNodeUpdate> handleSpouses(ArrayList<PersonNode> p){

        int counter, currentLevel;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Would you like to see spouses? (yes/no)");
        String answer = scanner.next();
        boolean normalSpacing = true;
        String spouseParentAnswer = "";

        //collects spouses by level and index
        ArrayList<PersonNodeUpdate> spouseUpdate = new ArrayList<>();
        if(answer.toLowerCase().equals("yes")){
            for(Integer level : pLevels.keySet()){
                for(PersonNode per : pLevels.get(level)){
                    if(per.getFamS() != null){
                        for(PersonNode per2 : p){
                            if(per2.getFamS() != null && per2.getFamS().equals(per.getFamS()) && !per2.equals(per)){
                                int index = pLevels.get(level).indexOf(per);
                                spouseUpdate.add(new PersonNodeUpdate(level, index + 1, per2));
                            }
                        }
                    }
                }
            }

            System.out.println("Do you want uniform node spacing with respect to spouses? (yes/no)");
            String spacingAnswer = scanner.next();

            if(spacingAnswer.toLowerCase().equals("yes")){
                normalSpacing = true;
            } else {
                normalSpacing = false;
            }


            //inserts spouses into pLevel map beside their spouse, location handled by algorithm
            if(normalSpacing){
                counter = 0;
                currentLevel = 0;
                for(PersonNodeUpdate s : spouseUpdate){
                    if(currentLevel != s.getLevel()){
                        counter = 0;
                        currentLevel = s.getLevel();
                    }
                    pLevels.get(s.getLevel()).add(s.getIndex() + counter, s.getNode());
                    counter++;
                }

                System.out.println("Do you want to view parents of spouses");
                spouseParentAnswer = scanner.next();
            }
        }

        //view parents of spouses to see if there is any more new connections in the family
        if(spouseParentAnswer.toLowerCase().equals("yes")){
            /*
             * Steps:
             *  - iterate from bottom to top adding family nodes to the appropriate location
             *  - iterate from bottom to top adding parents of spouses based on the new family nodes
             */
            ArrayList<FamilyNodeUpdate> updates = new ArrayList<>();
            for(int i = pLevels.size() - 1; i > 0; i--){
                for(PersonNode per : pLevels.get(i)){
                    if(per.getIn().size() > 0){
                        ArrayList<FamilyNode> temp = per.getIn();
                        for(FamilyNode fam : temp){
                            if(!fLevels.get(i-1).contains(fam)){
                                updates.add(new FamilyNodeUpdate(i-1, 0, fam));
                            }
                        }
                    }
                }
            }

            currentLevel = 0;
            for(FamilyNodeUpdate fam : updates){
                if(currentLevel != fam.getLevel()){
                    currentLevel = fam.getLevel();
                }
                fLevels.get(fam.getLevel()).add(fam.getNode());
            }

            ArrayList<PersonNodeUpdate> personNodeUpdates = new ArrayList<>();
            for(int i = pLevels.size() - 1; i > 0; i--){
                for(FamilyNode fam : fLevels.get(i)){
                    ArrayList<PersonNode> temp = fam.getIn();
                    for(PersonNode per : temp){
                        if(!pLevels.get(i).contains(per)){
                            personNodeUpdates.add(new PersonNodeUpdate(i,0,per));
                        }
                    }
                }
            }

            currentLevel = 0;
            for(PersonNodeUpdate per : personNodeUpdates){
                if(currentLevel != per.getLevel()){
                    currentLevel = per.getLevel();
                }
                pLevels.get(per.getLevel()).add(per.getNode());
            }
        }

        if(normalSpacing){
            return new ArrayList<>();
        } else {
            return spouseUpdate;
        }
    }

    /*
     * Input : levelLimit - number of levels in graph,
     *         height - number of pixels tall,
     *         width - number of pixels wide,
     *         pixelBuffer - buffer to be used on all sides of the screen
     * Output :
     * Description : This function uses the nodes in pLevel and fLevel and assigns x and y coordinates
     *               based on their order in each level and specific parameters provided. The screen is split into
     *               levelLimit regions vertically and is split by the number of nodes in each level horizontally.
     */
    public void setCoordinates(int levelLimit, int height, int width, int pixelBuffer){
        int levelIter = 0;
        for(int i = 0; i < levelLimit; i++) {

            int xIter = 1;
            if(pLevels.get(i) == null)
                break;

            for(PersonNode per : pLevels.get(i)){
                double yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                double xVal = ((width - 2*pixelBuffer)*1.0 / (pLevels.get(i).size()+1))*1.0 * xIter;
                per.setX((int) xVal);
                per.setY((int) yVal);
                xIter++;
            }
            levelIter++;

            //first run through
            xIter = 1;
            if(fLevels.get(i) == null)
                break;
            for(FamilyNode fam : fLevels.get(i)){
                int yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                int xVal = ((width - 2*pixelBuffer) / (fLevels.get(i).size()+1)) * xIter;
                fam.setX(xVal);
                fam.setY(yVal);
                xIter++;
            }
            levelIter++;
        }
    }

    /*
     * Input : list of PersonNodeUpdate objects - the spouses to add in
     * Output :
     * Description : This function adds spouses to their appropriate location in the levels by using
     *               information in the PersonNodeUpdate object. This includes the person, level, and
     *               index in that level.
     */
    public void spouseUpdateUneven(ArrayList<PersonNodeUpdate> spouseUpdate){

        int counter, currentLevel;

        counter = 0;
        currentLevel = 0;
        for(PersonNodeUpdate s : spouseUpdate){
            if(currentLevel != s.getLevel()){
                counter = 0;
                currentLevel = s.getLevel();
            }
            PersonNode spouse = s.getNode();
            int spouseY = (int)pLevels.get(s.getLevel()).get(s.getIndex() + counter-1).getY();
            int spouseX = (int)pLevels.get(s.getLevel()).get(s.getIndex() + counter-1).getX() + 5;
            spouse.setX(spouseX);
            spouse.setY(spouseY);
            pLevels.get(s.getLevel()).add(s.getIndex() + counter, spouse);
            counter++;
        }
    }

    /*
     * Input : list of Node objects, height in pixels, and pixelBuffer
     * Output :
     * Description : This function serves as post processing and uses relative birth year to
     *               determine the Y coordinate of a node. The function works by finding min
     *               and max birth year in the set of nodes and placing nodes relative to eachother
     *               based on these numbers.
     */
    public String handleBirthYearOrdering(ArrayList<Node> nodes, int height, int pixelBuffer){
        Scanner scanner = new Scanner(System.in);

        //Y value by date - post processing
        System.out.println("Would you like the Y coordinate to correspond to year of birth?");
        String answer = scanner.next();
        if(answer.toLowerCase().equals("yes")){
            int max = 0, min = 3000;
            //first iteration through nodes finds min and max birth year
            for(Node n : nodes){
                if(n.getBirthday() != null){
                    String date = n.getBirthday().getDate();
                    String[] split = date.split(" ");

                    String year = split[split.length - 1];
                    if(year.length() > 4){
                        split = year.split("/");
                        year = split[split.length - 1];
                    }

                    int numYear = Integer.parseInt(year);

                    if(numYear > max) max = numYear;
                    if(numYear < min) min = numYear;
                }
            }
            int span = max - min;
            //second iteration sets Y based on min and max
            for(Node n : nodes){
                if(n.getBirthday() != null){
                    String date = n.getBirthday().getDate();
                    String[] split = date.split(" ");

                    String year = split[split.length - 1];
                    if(year.length() > 4){
                        split = year.split("/");
                        year = split[split.length - 1];
                    }

                    int numYear = Integer.parseInt(year);
                    int newY = (int)(((numYear - min) * 1.0 /span * 1.0) * (height - 2*pixelBuffer)) + pixelBuffer;

                    n.setY(newY);
                }
            }
        }
        return answer;
    }

}

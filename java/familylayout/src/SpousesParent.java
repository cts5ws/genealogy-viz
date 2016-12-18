import graph.FamilyNode;
import graph.PersonNode;
import model.*;
import modules.*;

import java.io.IOException;
import java.util.*;

/*
 * This class attempts to display the parents of spouses while not interfering with the rest of the graph.
 */
public class SpousesParent {

    public static void main(String [] args) throws IOException{

        //READING IN DATA **********************************************************************************************
        Person[] persons = StaticUtils.readInput();

        FamilyBookKeeping familyBookKeeping = new FamilyBookKeeping(persons);

        final ArrayList<PersonNode> p = familyBookKeeping.createPersonNodeList();
        final ArrayList<FamilyNode> f = familyBookKeeping.createFamilyNodeList();

        NodeUpdate nodeUpdate = new NodeUpdate(p, f);
        nodeUpdate.setNodeEdges();

        //DETERMINE X AND Y FOR EACH NODE*******************************************************************************

        //CREATING DIFFERENT LEVELS
        Map<Integer, ArrayList<PersonNode>> pLevels = new HashMap<>();
        Map<Integer, ArrayList<FamilyNode>> fLevels = new HashMap<>();
        ArrayList<PersonNode> pCurrent = new ArrayList<>();
        ArrayList<FamilyNode> fCurrent = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the index of person: ");
        final int descendentIndex = scanner.nextInt();

        System.out.println("number of levels?");
        int levelLimit = scanner.nextInt();

        LevelOrdering levelOrdering = new LevelOrdering(pLevels, fLevels, pCurrent, fCurrent);
        levelOrdering.setpLevelsInit(p, descendentIndex);
        levelOrdering.setLevelsD();

        levelOrdering.removeDuplicatesD();
        levelOrdering.removeLeafFamsD();

        int height = 500;
        int width = 1000;
        int pixelBuffer = 25;

        /*//calculate number of descendants for each person
        for(int i = 0; i < pLevels.keySet().size(); i++){
            for(PersonNode per : pLevels.get(i)){
                int numDesc = PersonNode.calcNumDesc(per);
                per.setNumDesc(numDesc);
            }

            for(PersonNode per : pLevels.get(i)){
                per.setNumDesc(per.getNumDesc() + 10);
            }
        }*/

        //set minX maxX for root node
        pLevels.get(0).get(0).setMinX(pixelBuffer);
        pLevels.get(0).get(0).setMaxX(width - pixelBuffer);
        pLevels.get(0).get(0).setX(width / 2);

        for(int i = 0; i < pLevels.keySet().size() - 1; i++){

            for(PersonNode per : pLevels.get(i)){
                int currentMin = per.getMinX();
                int tempMin = currentMin;
                int currentMax = per.getMaxX();
                int range = currentMax - currentMin;

                for(FamilyNode fam : per.getOut()){
                    int totalNumDesc = fam.getDescTotal();
                    for(PersonNode child : fam.getOut()){

                        child.setMinX(tempMin);
                        child.setMaxX((int)(tempMin + ((child.getNumDesc()*1.0/totalNumDesc)*range)));
                        tempMin = (int)(tempMin + ((child.getNumDesc()*1.0/totalNumDesc)*range));

                    }
                }
            }
        }

        //*******START ADD SPOUSES TO LEVELS**********************

        //determines spouse for each individual
        System.out.println("spouses?");
        String answer = scanner.next();
        if(answer.toLowerCase().equals("yes")){
            answer = "";
            for(int i = 0; i < pLevels.keySet().size(); i++){
                for(PersonNode per : pLevels.get(i)){
                    per.setLevel(i);
                    per.updateSpouse(p);
                }
            }

            for(int i = 0; i < levelLimit; i++){
                ArrayList<PersonNode> level = pLevels.get(i);
                ListIterator<PersonNode> iter = level.listIterator();

                while(iter.hasNext()){
                    PersonNode per = iter.next();

                    if(per.getSpouse() != null && per.getSpouse().getLevel() == -99){
                        int adjustment = (per.getMaxX() - per.getMinX()) / 4;
                        per.getSpouse().setMinX(per.getMinX() + adjustment);
                        per.getSpouse().setMaxX(per.getMaxX() + adjustment);

                        if(!level.contains(per.getSpouse())){
                            iter.add(per.getSpouse());
                        }
                    }
                }
            }
            System.out.println("parents of spouses?");
            answer = scanner.next();
        }

        //*******END ADD SPOUSES TO LEVELS**********************
        //*******START ADD PARENTS OF SPOUSES*************************

        if(answer.toLowerCase().equals("yes")){
            //iterating from bottom up, add family node if not already there
            for(int i = pLevels.keySet().size() - 1; i > 0; i--){
                for(PersonNode per : pLevels.get(i)){
                    if(per.getIn().size() > 0){

                        for(FamilyNode fam : per.getIn()){
                            if(!StaticUtils.isInMapF(fLevels, fam)){
                                fLevels.get(i - 1).add(fam);
                            }
                        }

                    }
                }
            }

            //same as above, but adding person nodes, i.e. parents of spouses
            for(int i = fLevels.keySet().size() - 1; i >= 0; i--){
                for(FamilyNode fam : fLevels.get(i)){

                    int min = 9999, max = -1;
                    for(PersonNode per : fam.getOut()){
                        if(per.getMinX() != -1 && per.getMinX() < min){
                            min = per.getMinX();
                        }
                        if(per.getMaxX() != - 1 && per.getMaxX() > max){
                            max = per.getMaxX();
                        }
                    }

                    int buffer = 0;
                    for(PersonNode per : fam.getIn()){
                        if(!StaticUtils.isInMapP(pLevels, per)){
                            pLevels.get(i).add(per);

                            per.setMinX(min + buffer);
                            per.setMaxX(max + buffer);
                            buffer += (fLevels.keySet().size() - i + 1) * 2;
                        }
                    }
                }
            }

            //removes any node that isn't connected
            for(int i = pLevels.keySet().size() - 1; i > 0; i--){

                ArrayList<PersonNode> level = pLevels.get(i);
                ListIterator<PersonNode> iter = level.listIterator();

                while(iter.hasNext()){
                    PersonNode per = iter.next();

                    for(FamilyNode fam : per.getIn()){
                        if(!StaticUtils.isInMapF(fLevels, fam)){
                            iter.remove();
                        }
                    }
                }
            }
        }

        //*******END ADD PARENTS OF SPOUSES*************************


        //determine an X for each node
        for(int i = 0; i < pLevels.keySet().size(); i++){
            for(PersonNode per : pLevels.get(i)){
                per.setX((int) ((per.getMaxX() + per.getMinX()) * 1.0 / 2));
            }
        }

        //ASSIGNING X AND Y BASED ON LEVELS

        ArrayList<Node> nodes = new ArrayList<>();
        Map<PersonNode, Integer> personToIndex = new HashMap<>();
        Map<FamilyNode, Integer> familyToIndex = new HashMap<>();

        IndexingUtil indexingUtil = new IndexingUtil(nodes, personToIndex, familyToIndex);
        indexingUtil.finalBookKeeping(height, pixelBuffer, pLevels, fLevels, levelLimit);

        ArrayList<Link> graphedLinks = StaticUtils.createLinks1(personToIndex, familyToIndex, p, f);

        //WRITES OUTPUT TO JSON*****************************************************************************************
        StaticUtils.writeOutput(nodes, graphedLinks);

    }

}


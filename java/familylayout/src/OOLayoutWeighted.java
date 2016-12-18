import graph.FamilyNode;
import graph.PersonNode;
import model.*;
import modules.*;
import util.AdjacentPersonUpdate;

import java.io.IOException;
import java.util.*;

/*
 * This class is the most realistic and best looking layout. It uses the number of descandants of a node
 * to determine it's relative location
 */

public class OOLayoutWeighted {

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

        for(PersonNode per : pLevels.get(levelLimit - 1)){
            per.setOut(new ArrayList<FamilyNode>());
        }

        //calculate number of descendants for each person
        for(int i = 0; i < pLevels.keySet().size(); i++){
            for(PersonNode per : pLevels.get(i)){
                int numDesc = PersonNode.calcNumDesc(per);
                per.setNumDesc(numDesc);
            }
        }

        ArrayList<PersonNode> workingSet = new ArrayList<>();
        for(int i = 0; i < levelLimit; i++){
            for(PersonNode per : pLevels.get(i)){
                workingSet.add(per);
            }
        }

        for(PersonNode per : workingSet){
            per.updateSpouse(workingSet);
        }

        Map<PersonNode, ArrayList<FamilyNode>> famStore = new HashMap<>();
        ArrayList<PersonNode> spouseStore = new ArrayList<>();
        for(int i = 0; i < levelLimit; i++){
            for(PersonNode per : pLevels.get(i)){

                if(per.getSpouse() != null && !spouseStore.contains(per)){
                    per.setNumDesc(1);

                    famStore.put(per, per.getOut());

                    per.setOut(new ArrayList<FamilyNode>());
                    spouseStore.add(per.getSpouse());
                }

            }
        }

        for(int i = 0; i < levelLimit; i++){
            for(PersonNode per : pLevels.get(i)){

                int numDesc = PersonNode.calcNumDesc(per);
                per.setNumDesc(numDesc);

            }
        }

        for(PersonNode per : famStore.keySet()){
            per.setOut(famStore.get(per));
        }



        int height = 500;
        int width = 1000;
        int pixelBuffer = 25;

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


        //determine an X for each node
        for(int i = 0; i < pLevels.keySet().size(); i++){
            for(PersonNode per : pLevels.get(i)){
                per.setX((int) ((per.getMaxX() + per.getMinX()) * 1.0 / 2));
            }
        }



        //-----------------------------INTER-FAMILY MARRIAGE-----------------------------


        System.out.println("Would you like to handle inter-family marriage?");
        String answer = scanner.next();
        if(answer.toLowerCase().equals("yes")){
            ArrayList<PersonNode> d = new ArrayList<>();
            for(int i = 0; i < pLevels.size(); i++){
                for(PersonNode per : pLevels.get(i)){
                    d.add(per);
                }
            }

            ArrayList<AdjacentPersonUpdate> adjacentPersonUpdates = new ArrayList<>();
            for(PersonNode node1 : d){
                if(node1.getFamS() != null){
                    for(PersonNode node2 : d){
                        if(node2.getFamS() != null){
                            if(!node1.equals(node2) && node1.getFamS().equals(node2.getFamS())){

                                AdjacentPersonUpdate temp = new AdjacentPersonUpdate(node1, node2);

                                if(!adjacentPersonUpdates.contains(temp)){
                                    adjacentPersonUpdates.add(temp);
                                    System.out.println(node1.getName() + "  <-->  " + node2.getName());
                                }
                            }
                        }
                    }
                }
            }

            for(AdjacentPersonUpdate adjacentPersonUpdate : adjacentPersonUpdates){
                //create identical copy and position beside spouse of lower generation
                PersonNode sourceCopy = new PersonNode(adjacentPersonUpdate.getSource());
                sourceCopy.setY(adjacentPersonUpdate.getTarget().getY());
                sourceCopy.setX(adjacentPersonUpdate.getTarget().getX() + 10);
                sourceCopy.setCopyNum(1);

                //add it to appropriate level so Y can be correctly determined
                pLevels.get(adjacentPersonUpdate.getTarget().getLevel()).add(sourceCopy);

                sourceCopy.getOut().get(0).getIn().remove(adjacentPersonUpdate.getSource());

                //adjust in and out edges of nodes
                sourceCopy.setIn(new ArrayList<FamilyNode>());
                adjacentPersonUpdate.getSource().setOut(new ArrayList<FamilyNode>());


                //System.out.println(adjacentPersonUpdate.getSource().getOut());
                //System.out.println(sourceCopy.getOut());
                //adjacentPersonUpdate.getSource().getOut().get(0).setOut(new ArrayList<PersonNode>());
                //sourceCopy.getIn().get(0).setIn(new ArrayList<PersonNode>());

                p.add(sourceCopy);
            }
        }

        //-----------------------------INTER-FAMILY MARRIAGE-----------------------------

        //ASSIGNING X AND Y BASED ON LEVELS

        ArrayList<Node> nodes = new ArrayList<>();
        Map<PersonNode, Integer> personToIndex = new HashMap<>();
        Map<FamilyNode, Integer> familyToIndex = new HashMap<>();

        IndexingUtil indexingUtil = new IndexingUtil(nodes, personToIndex, familyToIndex);
        indexingUtil.finalBookKeeping(height, pixelBuffer, pLevels, fLevels, levelLimit);

        levelOrdering.handleBirthYearOrdering(nodes, height, pixelBuffer);

        ArrayList<Link> graphedLinks = StaticUtils.createLinks1(personToIndex, familyToIndex, p, f);

        //WRITES OUTPUT TO JSON*****************************************************************************************
        StaticUtils.writeOutput(nodes, graphedLinks);

    }

}


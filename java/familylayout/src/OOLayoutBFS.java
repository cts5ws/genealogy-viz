import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import graph.FamilyNode;
import graph.PersonNode;
import model.*;
import modules.NodeUpdate;
import modules.FamilyBookKeeping;
import modules.StaticUtils;
import util.PersonNodeUpdate;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class OOLayoutBFS {

    static final int NUM_HOPS_DEFAULT = -99;

    public static void main(String [] args) throws IOException{

        //READING IN DATA **********************************************************************************************
        Person[] persons = StaticUtils.readInput();

        FamilyBookKeeping familyBookKeeping = new FamilyBookKeeping(persons);

        final ArrayList<PersonNode> p = familyBookKeeping.createPersonNodeList();
        final ArrayList<FamilyNode> f = familyBookKeeping.createFamilyNodeList();

        NodeUpdate nodeUpdate = new NodeUpdate(p, f);
        nodeUpdate.setNodeEdges();

        //DETERMINE X AND Y FOR EACH NODE*******************************************************************************

        //START DESCENDENCY CODE
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the index of the root node you would like to query?");
        final int rootNode = scanner.nextInt();
        System.out.println("How many edge hops would you like to visualize from the root?");
        int numEdges = scanner.nextInt();

        nodeUpdate.setHopsAwayInit(p, rootNode);
        nodeUpdate.setHopsAway(p, numEdges);

        Map<Integer, ArrayList<PersonNode>> levelToPers = new HashMap<>();
        Map<Integer, ArrayList<FamilyNode>> levelToFams = new HashMap<>();

        for(int i = numEdges; i >= numEdges*-1; i--){
            levelToPers.put(i, new ArrayList<PersonNode>());
            levelToFams.put(i, new ArrayList<FamilyNode>());
        }

        for(PersonNode per : p){
            if(per != null && per.getLevel() != NUM_HOPS_DEFAULT){
                levelToPers.get(per.getLevel()).add(per);
            }
        }
        for(FamilyNode fam : f){
            if(fam != null && fam.getLevel() != NUM_HOPS_DEFAULT){
                levelToFams.get(fam.getLevel()).add(fam);
            }
        }
        //START ORDERING LOGIC
        Map<Integer, ArrayList<PersonNode>> levelToPersOrd = new HashMap<>();
        Map<Integer, ArrayList<FamilyNode>> levelToFamsOrd = new HashMap<>();

        int putIndex = 0;
        for(int i = numEdges; i >= numEdges*-1; i--){
            levelToPersOrd.put(putIndex, levelToPers.get(i));
            levelToFamsOrd.put(putIndex, levelToFams.get(i));
            putIndex++;
        }

        for(int i = numEdges; i < numEdges*2 + 1; i++){
            levelToFamsOrd.put(i, levelToFamsOrd.get(i + 1));
        }

        int levelLimit = numEdges * 2 + 1;

        /*
         *
         * Need to add logic either above or below to account for people in a level that aren't descendants i.e. wifes
         *
         */
        //START ORDERING LOGIC
        Map<Integer, ArrayList<PersonNode>> levelToPersOrdFinal = new HashMap<>();
        Map<Integer, ArrayList<FamilyNode>> levelToFamsOrdFinal = new HashMap<>();

        for(int i = 0; i < levelLimit; i++){
            levelToFamsOrdFinal.put(i, new ArrayList<FamilyNode>());
            levelToPersOrdFinal.put(i, new ArrayList<PersonNode>());
        }

        levelToPersOrdFinal.put(0, levelToPersOrd.get(0));
        for(int i = 0; i < levelLimit; i++){
            for(PersonNode per : levelToPersOrdFinal.get(i)){
                for(FamilyNode fam : per.getOut()){
                    if(fam != null && levelToFamsOrd.get(i) != null && levelToFamsOrd.get(i).contains(fam) && !levelToFamsOrdFinal.get(i).contains(fam)){
                        levelToFamsOrdFinal.get(i).add(fam);
                    }
                }
            }

            //put root node at middle of middle level
            levelToPersOrdFinal.get((levelLimit - 1) / 2).remove(p.get(rootNode));
            levelToPersOrdFinal.get((levelLimit - 1) / 2).add(levelToPersOrdFinal.get((levelLimit - 1) / 2).size()/2, p.get(rootNode));


            for(FamilyNode fam : levelToFamsOrdFinal.get(i)){
                for(PersonNode per : fam.getOut()){
                    if(levelToPersOrd.get(i+1).contains(per) && !levelToPersOrdFinal.get(i+1).contains(per)){
                        levelToPersOrdFinal.get(i+1).add(per);
                    }
                }
            }
        }

        ArrayList<PersonNode> tempPer = new ArrayList<>();
        ArrayList<FamilyNode> tempFam = new ArrayList<>();

        for(int i = 0; i < levelLimit; i++){

            for(PersonNode per : levelToPersOrd.get(i)){
                if(!levelToPersOrdFinal.get(i).contains(per)){
                    //levelToPersOrdFinal.get(i).add(per);
                    tempPer.add(per);
                }
            }

            if(levelToFamsOrd.get(i) != null){
                for(FamilyNode fam : levelToFamsOrd.get(i)){
                    if(fam != null && !levelToFamsOrdFinal.get(i).contains(fam)){
                        levelToFamsOrdFinal.get(i).add(fam);
                        tempFam.add(fam);
                    }
                }
            }
        }

        ArrayList<PersonNodeUpdate> spouses = new ArrayList<>();
        for(int i = 0; i < levelLimit; i++){
            for(PersonNode per : levelToPersOrdFinal.get(i)){
                for(PersonNode per1 : tempPer){
                    if(per.getFamS() != null && per1.getFamS() != null && per.getFamS().equals(per1.getFamS())){
                        spouses.add(new PersonNodeUpdate(i, levelToPersOrdFinal.get(i).indexOf(per), per1));
                    }
                }
            }
        }

        int counter = 0;
        int currentLevel = 0;
        for(PersonNodeUpdate per : spouses){
            if(currentLevel != per.getLevel()){
                counter = 0;
                currentLevel = per.getLevel();
            }
            levelToPersOrdFinal.get(per.getLevel()).add(per.getIndex() + counter, per.getNode());
            counter++;
        }

        //END ORDERING LOGIC

        //ASSIGNING X AND Y BASED ON LEVELS
        ArrayList<Node> nodes = new ArrayList<>();
        Map<PersonNode, Integer> personToIndex = new HashMap<>();
        Map<FamilyNode, Integer> familyToIndex = new HashMap<>();

        int height = 500;
        int width = 1000;
        int pixelBuffer = 25;


        int levelIter = 0;
        for(int i = 0; i < levelLimit; i++) {

            int xIter = 1;
            if(levelToPersOrdFinal.get(i) == null)
                break;

            for(PersonNode per : levelToPersOrdFinal.get(i)){
                double yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                double xVal = ((width - 2*pixelBuffer)*1.0 / (levelToPersOrdFinal.get(i).size()+1))*1.0 * xIter;
                per.setX((int) xVal);
                per.setY((int) yVal);
                //nodes.add(new Node(per.getName(), xVal, yVal, per.getId(), per.getFamC(), per.getFamS()));
                xIter++;
            }
            levelIter++;

            //first run through
            xIter = 1;
            if(levelToFamsOrdFinal.get(i) == null)
                break;
            for(FamilyNode fam : levelToFamsOrdFinal.get(i)){
                int yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                int xVal = ((width - 2*pixelBuffer) / (levelToFamsOrdFinal.get(i).size()+1)) * xIter;
                fam.setX(xVal);
                fam.setY(yVal);
                //nodes.add(new Node(fam.getId(), xVal, yVal, fam.getId(), null, null));
                xIter++;
            }
            levelIter++;
        }

        int indexCount = 0;
        for(int i = 0; i < levelLimit; i++) {

            if(levelToPersOrdFinal.get(i) == null)
                break;
            for(PersonNode per : levelToPersOrdFinal.get(i)){
                nodes.add(new Node(per.getName(), per.getX(), per.getY(), per.getId(), per.getFamC(), per.getFamS(), indexCount + "", StaticUtils.getInitials(per.getName()), per.getSex(), per.getBirthday(), per.getCopyNum()));
                indexCount++;
                personToIndex.put(per, nodes.size() - 1);
            }
            //to revert to old layout locations uncomment this and comment next for loop
            /*for(FamilyNode fam : fLevels.get(i)){
                nodes.add(new Node(fam.getId(), fam.getX(), fam.getY(), fam.getId(), null, null, indexCount + "", fam.getId(), null));
            }*/
        }

        for(int i = 0; i < levelLimit; i++) {
            if(levelToFamsOrdFinal.get(i) == null)
                break;
            for(int j = 0; j < levelToFamsOrdFinal.get(i).size(); j++){
                FamilyNode fam = levelToFamsOrdFinal.get(i).get(j);
                if(levelToFamsOrdFinal.get(i).size() > 1){
                    for(int k = 0; k < 10; k ++){
                        if(j == 0)
                            fam.setAverageX(0, (int)levelToFamsOrdFinal.get(i).get(j+1).getX());
                        else if (j == levelToFamsOrdFinal.get(i).size() - 1)
                            fam.setAverageX((int)levelToFamsOrdFinal.get(i).get(j-1).getX(), width);
                        else
                            fam.setAverageX((int)levelToFamsOrdFinal.get(i).get(j-1).getX(), (int)levelToFamsOrdFinal.get(i).get(j+1).getX());
                    }
                }


                nodes.add(new Node(fam.getId(), fam.getX(), fam.getY(), fam.getId(), null, null, indexCount + "", fam.getId(), null, null, 0));
                indexCount++;
                familyToIndex.put(fam, nodes.size()-1);
            }
        }

        ArrayList<Link> graphedLinks = StaticUtils.createLinks1(personToIndex, familyToIndex, p, f);


        //WRITES OUTPUT TO JSON*****************************************************************************************
        StaticUtils.writeOutput(nodes, graphedLinks);

    }

}


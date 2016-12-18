
import graph.FamilyNode;
import graph.PersonNode;
import model.*;
import modules.NodeUpdate;
import modules.FamilyBookKeeping;
import modules.LevelOrdering;
import modules.StaticUtils;
import util.PersonNodeUpdate;

import java.io.IOException;
import java.util.*;

/*
 * This class incorporates both ancestry and descendancy trees to show all relatives of an individual..
 */

public class OOLayoutB {

    public static void main(String[] args) throws IOException {

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
        System.out.println("How many generations of descendants would you like to see?");
        int numD = scanner.nextInt();

        //CREATING DIFFERENT LEVELS
        Map<Integer, ArrayList<PersonNode>> pLevelsD = new HashMap<>();
        Map<Integer, ArrayList<FamilyNode>> fLevelsD = new HashMap<>();
        ArrayList<PersonNode> pCurrentD = new ArrayList<>();
        ArrayList<FamilyNode> fCurrentD = new ArrayList<>();

        pCurrentD.add(p.get(rootNode));
        pLevelsD.put(0, new ArrayList<PersonNode>() {{
            add(p.get(rootNode));
        }});

        LevelOrdering levelOrderingD = new LevelOrdering(pLevelsD, fLevelsD, pCurrentD, fCurrentD);
        levelOrderingD.setpLevelsInit(p, rootNode);
        levelOrderingD.setLevelsD();

        levelOrderingD.removeDuplicatesD();
        levelOrderingD.removeLeafFamsD();

        levelOrderingD.handleGenerationSpan(p);

        ArrayList<PersonNodeUpdate> spouseUpdate = levelOrderingD.handleSpouses(p);

        //END DESCENDENCY CODE

        System.out.println("How many generations of ancestors would you like to see?");
        int numA = scanner.nextInt();

        //CREATING DIFFERENT LEVELS
        Map<Integer, ArrayList<PersonNode>> pLevelsA = new HashMap<>();
        Map<Integer, ArrayList<FamilyNode>> fLevelsA = new HashMap<>();
        ArrayList<PersonNode> pCurrentA = new ArrayList<>();
        ArrayList<FamilyNode> fCurrentA = new ArrayList<>();

        LevelOrdering levelOrderingA = new LevelOrdering(pLevelsA, fLevelsA, pCurrentA, fCurrentA);
        levelOrderingA.setpLevelsInit(p, rootNode);
        levelOrderingA.setLevelsA();

        levelOrderingA.removeDuplicatesA();
        levelOrderingA.removeLeafFamsA();


        System.out.println("Do you want to see siblings of each individual?");
        String siblingAnswer = scanner.next();
        if (siblingAnswer.toLowerCase().equals("yes")) {
            //Map<Integer, ArrayList<PersonNode>> siblings = new HashMap<>();
            ArrayList<PersonNodeUpdate> siblingUpdates = new ArrayList<>();
            for (int i = 0; i < pLevelsA.size(); i++) {
                for (PersonNode per : pLevelsA.get(i)) {

                    if (per.getIn().size() > 0) {
                        ArrayList<PersonNode> temp = per.getIn().get(0).getOut();
                        int index = pLevelsA.get(i).indexOf(per);
                        for (PersonNode per2 : temp) {
                            siblingUpdates.add(new PersonNodeUpdate(i, index, per2));
                        }
                        //siblings.put(i, temp);
                    }
                }
            }

            int counter, currentLevel;
            counter = 0;
            currentLevel = 0;
            for (PersonNodeUpdate s : siblingUpdates) {
                if (currentLevel != s.getLevel()) {
                    counter = 0;
                    currentLevel = s.getLevel();
                }
                pLevelsA.get(s.getLevel()).add(s.getIndex() + counter, s.getNode());
                counter++;
            }
        }

        //ASSIGNING X AND Y BASED ON LEVELS
        ArrayList<Node> nodes = new ArrayList<>();

        Map<PersonNode, Integer> personToIndex = new HashMap<>();
        Map<FamilyNode, Integer> familyToIndex = new HashMap<>();

        int height = 500;
        int width = 1000;
        int pixelBuffer = 25;

        int levelIter = 1;
        for (int i = numA - 1; i >= 0; i--) {

            int xIter = 1;
            if (pLevelsA.get(i) == null)
                break;

            for (PersonNode per : pLevelsA.get(i)) {
                double yVal = (((height - 2 * pixelBuffer) / (2 * (numA + numD)) * (levelIter + 1)));
                double xVal = ((width - 2 * pixelBuffer) * 1.0 / (pLevelsA.get(i).size() + 1)) * 1.0 * xIter;
                per.setX((int) xVal);
                per.setY((int) yVal);

                xIter++;
            }
            levelIter++;

            //first run through
            xIter = 1;
            if (fLevelsA.get(i) == null)
                break;
            for (FamilyNode fam : fLevelsA.get(i)) {
                int yVal = (((height - 2 * pixelBuffer) / (2 * (numA + numD)) * (levelIter - 1)));
                int xVal = ((width - 2 * pixelBuffer) / (fLevelsA.get(i).size() + 1)) * xIter;
                fam.setX(xVal);
                fam.setY(yVal);

                xIter++;
            }
            levelIter++;
        }


        //create node objects
        int indexCount = 0;
        for (int i = 0; i < numA; i++) {

            if (pLevelsA.get(i) == null)
                break;
            for (PersonNode per : pLevelsA.get(i)) {
                nodes.add(new Node(per.getName(), per.getX(), per.getY(), per.getId(), per.getFamC(), per.getFamS(), indexCount + "", StaticUtils.getInitials(per.getName()), per.getSex(), per.getBirthday(), 0));
                indexCount++;
                personToIndex.put(per, nodes.size() - 1);
            }
        }

        //set family node locations
        for (int i = 0; i < numA; i++) {
            if (fLevelsA.get(i) == null)
                break;
            for (int j = 0; j < fLevelsA.get(i).size(); j++) {
                FamilyNode fam = fLevelsA.get(i).get(j);
                if (fLevelsA.get(i).size() > 1) {
                    for (int k = 0; k < 10; k++) {
                        if (j == 0)
                            fam.setAverageX(0, (int) fLevelsA.get(i).get(j + 1).getX());
                        else if (j == fLevelsA.get(i).size() - 1)
                            fam.setAverageX((int) fLevelsA.get(i).get(j - 1).getX(), width);
                        else
                            fam.setAverageX((int) fLevelsA.get(i).get(j - 1).getX(), (int) fLevelsA.get(i).get(j + 1).getX());
                    }
                }

                nodes.add(new Node(fam.getId(), fam.getX(), fam.getY(), fam.getId(), null, null, indexCount + "", fam.getId(), null, null, 0));
                indexCount++;
                familyToIndex.put(fam, nodes.size() - 1);
            }
        }


        for (int i = 0; i < numD; i++) {

            int xIter = 1;
            if (pLevelsD.get(i) == null)
                break;

            for (PersonNode per : pLevelsD.get(i)) {
                double yVal = (((height - 2 * pixelBuffer) / (2 * (numD + numA))) * (levelIter));
                double xVal = ((width - 2 * pixelBuffer) * 1.0 / (pLevelsD.get(i).size() + 1)) * 1.0 * xIter;
                per.setX((int) xVal);
                per.setY((int) yVal);
                xIter++;
            }
            if (i != 0)
                levelIter++;

            //first run through
            xIter = 1;
            if (fLevelsD.get(i) == null)
                break;
            for (FamilyNode fam : fLevelsD.get(i)) {
                int yVal = (((height - 2 * pixelBuffer) / (2 * (numD + numA))) * (levelIter));
                int xVal = ((width - 2 * pixelBuffer) / (fLevelsD.get(i).size() + 1)) * xIter;
                fam.setX(xVal);
                fam.setY(yVal);
                //nodes.add(new Node(fam.getId(), xVal, yVal, fam.getId(), null, null));
                xIter++;
            }
            levelIter++;
        }

        int counter, currentLevel;
        //adds spouses and assigns location directly beside husband/wife
        if (!spouseUpdate.isEmpty()) {
            counter = 0;
            currentLevel = 0;
            for (PersonNodeUpdate s : spouseUpdate) {
                if (currentLevel != s.getLevel()) {
                    counter = 0;
                    currentLevel = s.getLevel();
                }
                PersonNode spouse = s.getNode();
                int spouseY = (int) pLevelsD.get(s.getLevel()).get(s.getIndex() + counter - 1).getY();
                int spouseX = (int) pLevelsD.get(s.getLevel()).get(s.getIndex() + counter - 1).getX() + 5;
                spouse.setX(spouseX);
                spouse.setY(spouseY);
                pLevelsD.get(s.getLevel()).add(s.getIndex() + counter, spouse);
                counter++;
            }

        }

        indexCount = 0;
        for (int i = 0; i < numD; i++) {

            if (pLevelsD.get(i) == null)
                break;
            for (PersonNode per : pLevelsD.get(i)) {
                if (personToIndex.get(per) == null) {
                    nodes.add(new Node(per.getName(), per.getX(), per.getY(), per.getId(), per.getFamC(), per.getFamS(), indexCount + "", StaticUtils.getInitials(per.getName()), per.getSex(), per.getBirthday(), per.getCopyNum()));
                    indexCount++;
                    personToIndex.put(per, nodes.size() - 1);
                }
            }
        }

        for (int i = 0; i < numD; i++) {
            if (fLevelsD.get(i) == null)
                break;
            for (int j = 0; j < fLevelsD.get(i).size(); j++) {
                FamilyNode fam = fLevelsD.get(i).get(j);
                if (fLevelsD.get(i).size() > 1) {
                    for (int k = 0; k < 10; k++) {
                        if (j == 0)
                            fam.setAverageX(0, (int) fLevelsD.get(i).get(j + 1).getX());
                        else if (j == fLevelsD.get(i).size() - 1)
                            fam.setAverageX((int) fLevelsD.get(i).get(j - 1).getX(), width);
                        else
                            fam.setAverageX((int) fLevelsD.get(i).get(j - 1).getX(), (int) fLevelsD.get(i).get(j + 1).getX());
                    }
                }

                fam.setCoordinateAverageX();

                nodes.add(new Node(fam.getId(), fam.getX(), fam.getY(), fam.getId(), null, null, indexCount + "", fam.getId(), null, null, 0));
                indexCount++;
                familyToIndex.put(fam, nodes.size() - 1);
            }
        }

        ArrayList<Link> graphedLinks = StaticUtils.createLinks1(personToIndex, familyToIndex, p, f);


        //WRITES OUTPUT TO JSON*****************************************************************************************
        StaticUtils.writeOutput(nodes, graphedLinks);

    }
}


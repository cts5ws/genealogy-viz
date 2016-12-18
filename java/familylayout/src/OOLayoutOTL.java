import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import graph.FamilyNode;
import graph.PersonNode;
import model.*;
import modules.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class OOLayoutOTL {

    public static void main(String[] args) throws IOException {

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

        //set minX maxX for root node
        pLevels.get(0).get(0).setMinX(pixelBuffer);
        pLevels.get(0).get(0).setMaxX(width - pixelBuffer);
        pLevels.get(0).get(0).setX(width / 2);
        //set minX and maxX for each node based on the location of it's parent
        for (int i = 0; i < pLevels.keySet().size() - 1; i++) {

            for (PersonNode per : pLevels.get(i)) {
                int currentMin = per.getMinX();
                int tempMin = currentMin;
                int currentMax = per.getMaxX();

                int counter = 1;
                for (FamilyNode fam : per.getOut()) {

                    int numPer = fam.getOut().size();
                    int gap = -1;
                    if (numPer != 0) {
                        gap = (currentMax - currentMin) / numPer;
                    }

                    for (PersonNode child : fam.getOut()) {

                        child.setMinX(tempMin);
                        child.setMaxX(tempMin + gap * counter);
                        tempMin = tempMin + gap * counter;

                        //counter++;
                    }
                    counter++;
                }
            }
        }

        //determine an X for each node
        Map<Point, Integer> tempLoc = new HashMap<>();
        for (int i = 0; i < pLevels.keySet().size(); i++) {
            for (PersonNode per : pLevels.get(i)) {
                Point temp = new Point(per.getMinX(), per.getMaxX());
                if (tempLoc.get(temp) == null) {
                    tempLoc.put(temp, 1);
                } else {
                    tempLoc.put(temp, tempLoc.get(temp) + 1);
                }
            }

            Map<Point, Integer> tempLocCountDown = new HashMap<>(tempLoc);
            for (PersonNode per : pLevels.get(i)) {
                int range = per.getMaxX() - per.getMinX();
                int numKids = tempLoc.get(new Point(per.getMinX(), per.getMaxX()));
                int gap = range / numKids;

                Point temp = new Point(per.getMinX(), per.getMaxX());
                int counter = tempLocCountDown.get(temp);
                int x = per.getMinX() + (gap * counter) - gap / 2;
                per.setX(x);

                tempLocCountDown.put(temp, tempLocCountDown.get(temp) - 1);
            }

            tempLoc.clear();
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


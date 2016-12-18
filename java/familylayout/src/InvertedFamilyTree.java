import graph.FamilyNode;
import graph.PersonNode;
import model.Link;
import model.Node;
import model.Person;
import modules.FamilyBookKeeping;
import modules.LevelOrdering;
import modules.NodeUpdate;
import modules.StaticUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class InvertedFamilyTree {

    public static void main(String [] args) throws IOException {

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
        System.out.println("Enter the index of family: ");
        final int descendentIndex = scanner.nextInt();

        System.out.println("number of levels?");
        int levelLimit = scanner.nextInt();

        fCurrent.add(f.get(descendentIndex));
        fLevels.put(0, new ArrayList<FamilyNode>(){{add(f.get(descendentIndex));}});

        int iter= 0;
        while(iter < 2*levelLimit) {
            if (iter % 2 == 0) {//current = family nodes
                ArrayList<PersonNode> next = new ArrayList<>();
                for (FamilyNode fam : fCurrent) {
                    for(PersonNode per : fam.getIn()){
                        if(!StaticUtils.isInMapP(pLevels, per) && !next.contains(per)){
                            //per.setLevel(iter / 2 + 1);
                            next.add(per);
                        }
                    }
                    for (PersonNode per : fam.getOut()) {
                        if (!StaticUtils.isInMapP(pLevels, per) && !next.contains(per)) {
                            //per.setLevel(iter / 2 + 1);
                            next.add(per);
                        }
                    }
                }

                if (next.isEmpty())
                    break;
                else {
                    pLevels.put(fLevels.size() - 1, next);
                    fCurrent.clear();
                    pCurrent.addAll(next);
                }
            } else{ //current = person nodes
                ArrayList<FamilyNode> next = new ArrayList<>();
                for (PersonNode per : pCurrent) {
                    for (FamilyNode fam : per.getOut()) {
                        if (!StaticUtils.isInMapF(fLevels, fam) && !next.contains(fam) ) {
                            next.add(fam);
                        }
                    }
                }

                if (next.isEmpty())
                    break;
                else {
                    fLevels.put(pLevels.size(), next);
                    pCurrent.clear();
                    fCurrent.addAll(next);
                }
            }
            iter++;
        }

        System.out.println(fLevels);
        System.out.println(pLevels);

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
            if(fLevels.get(i) == null)
                break;

            for(FamilyNode fam : fLevels.get(i)){
                double yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                double xVal = ((width - 2*pixelBuffer)*1.0 / (fLevels.get(i).size()+1))*1.0 * xIter;
                fam.setX((int) xVal);
                fam.setY((int) yVal);
                xIter++;
            }
            levelIter++;

            //first run through
            xIter = 1;
            if(pLevels.get(i) == null)
                break;
            for(PersonNode per : pLevels.get(i)){
                int yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                int xVal = ((width - 2*pixelBuffer) / (pLevels.get(i).size()+1)) * xIter;
                per.setX(xVal);
                per.setY(yVal);
                xIter++;
            }
            levelIter++;
        }


        int indexCount = 0;
        for(int i = 0; i < levelLimit; i++) {
            if(fLevels.get(i) == null)
                break;
            for(FamilyNode fam : fLevels.get(i)){
                fam.setCoordinateAverageX();
                nodes.add(new Node(fam.getId(), fam.getX(), fam.getY(), fam.getId(), null, null, indexCount + "", fam.getId(), null, null, -1));
                indexCount++;
                familyToIndex.put(fam, nodes.size() - 1);
            }
        }

        for(int i = 0; i < levelLimit; i++) {
            if(pLevels.get(i) == null)
                break;
            for(int j = 0; j < pLevels.get(i).size(); j++){
                PersonNode per = pLevels.get(i).get(j);

                nodes.add(new Node(per.getName(), per.getX(), per.getY(), per.getId(), per.getFamC(), per.getFamS(), indexCount + "", StaticUtils.getInitials(per.getName()), per.getSex(), per.getBirthday(), 0));
                indexCount++;
                personToIndex.put(per, nodes.size()-1);
            }
        }


        ArrayList<Link> graphedLinks = StaticUtils.createLinks1(personToIndex, familyToIndex, p, f);


        //WRITES OUTPUT TO JSON*****************************************************************************************
        StaticUtils.writeOutput(nodes, graphedLinks);
    }

}

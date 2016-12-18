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


public class OOLayoutA {

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
        final int aIndex = scanner.nextInt();

        //set up levels
        pCurrent.add(p.get(aIndex));
        pLevels.put(0, new ArrayList<PersonNode>(){{add(p.get(aIndex));}});

        LevelOrdering levelOrdering = new LevelOrdering(pLevels, fLevels, pCurrent, fCurrent);
        levelOrdering.setpLevelsInit(p, aIndex);
        levelOrdering.setLevelsA();

        levelOrdering.removeDuplicatesA();
        levelOrdering.removeLeafFamsA();

        //handle siblings
        String answer;
        System.out.println("Do you want to see siblings of each individual?");
        answer = scanner.next();
        if(answer.toLowerCase().equals("yes")) {
            ArrayList<PersonNodeUpdate> siblingUpdates = new ArrayList<>();
            for (int i = 0; i < pLevels.size(); i++) {
                for (PersonNode per : pLevels.get(i)) {

                    if (per.getIn().size() > 0) {
                        ArrayList<PersonNode> temp = per.getIn().get(0).getOut();
                        int index = pLevels.get(i).indexOf(per);
                        for (PersonNode per2 : temp) {
                            siblingUpdates.add(new PersonNodeUpdate(i, index, per2));
                        }
                    }
                }
            }

            int counter = 0;
            int currentLevel = 0;
            for (PersonNodeUpdate s : siblingUpdates) {
                if (currentLevel != s.getLevel()) {
                    counter = 0;
                    currentLevel = s.getLevel();
                }
                pLevels.get(s.getLevel()).add(s.getIndex() + counter, s.getNode());
                counter++;
            }
        }

        //ASSIGNING X AND Y BASED ON LEVELS
        ArrayList<Node> nodes = new ArrayList<>();

        int height = 500;
        int width = 1000;
        int pixelBuffer = 25;
        System.out.println("How many generations of descendants would you like to see? ");
        int levelLimit = scanner.nextInt();

        int levelIter = 1;
        for(int i = levelLimit - 1; i >= 0; i--) {

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
                int yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter-1));
                int xVal = ((width - 2*pixelBuffer) / (fLevels.get(i).size()+1)) * xIter;
                fam.setX(xVal);
                fam.setY(yVal);

                xIter++;
            }
            levelIter++;
        }


        //create node objects
        int indexCount = 0;
        for(int i = 0; i < levelLimit; i++) {

            if(pLevels.get(i) == null)
                break;
            for(PersonNode per : pLevels.get(i)){
                nodes.add(new Node(per.getName(), per.getX(), per.getY(), per.getId(), per.getFamC(), per.getFamS(), indexCount + "", StaticUtils.getInitials(per.getName()), per.getSex(), per.getBirthday(),0));
                indexCount++;
            }

        }

        levelOrdering.handleBirthYearOrdering(nodes, height, pixelBuffer);

        //set location of family nodes
        for(int i = 0; i < levelLimit; i++) {
            if(fLevels.get(i) == null)
                break;
            for(int j = 0; j < fLevels.get(i).size(); j++){
                FamilyNode fam = fLevels.get(i).get(j);
                if(fLevels.get(i).size() > 1){
                    for(int k = 0; k < 10; k ++){
                        if(j == 0)
                            fam.setAverageX(0, (int)fLevels.get(i).get(j+1).getX());
                        else if (j == fLevels.get(i).size() - 1)
                            fam.setAverageX((int)fLevels.get(i).get(j-1).getX(), width);
                        else
                            fam.setAverageX((int)fLevels.get(i).get(j-1).getX(), (int)fLevels.get(i).get(j+1).getX());
                    }
                }

                nodes.add(new Node(fam.getId(), fam.getX(), fam.getY(), fam.getId(), null, null, indexCount + "", fam.getId(), null, null,0));
                indexCount++;
            }
        }



        //set up data structures used to determine links
        Map<String, Integer> idToNodeIndex = new HashMap<>();
        for(int i = 0; i < nodes.size(); i++){
            idToNodeIndex.put(nodes.get(i).getId(), i);
        }

        Set<String> famSet = new HashSet<>();
        for(FamilyNode fam: f) {
            if(fam != null)
                famSet.add(fam.getId());
        }

        ArrayList<Link> graphedLinks = StaticUtils.createLinks(nodes, famSet, idToNodeIndex);


        //WRITES OUTPUT TO JSON*****************************************************************************************
        StaticUtils.writeOutput(nodes, graphedLinks);

    }

}


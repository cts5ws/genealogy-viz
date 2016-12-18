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
 * This class shows descendants of a specified individual
 */
public class OOLayoutD {

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

        LevelOrdering levelOrdering = new LevelOrdering(pLevels, fLevels, pCurrent, fCurrent);
        levelOrdering.setpLevelsInit(p, descendentIndex);
        levelOrdering.setLevelsD();

        levelOrdering.removeDuplicatesD();
        levelOrdering.removeLeafFamsD();

        levelOrdering.handleGenerationSpan(p);

        ArrayList<PersonNodeUpdate> spouseUpdate = levelOrdering.handleSpouses(p);


        //ASSIGNING X AND Y BASED ON LEVELS
        ArrayList<Node> nodes = new ArrayList<>();
        Map<PersonNode, Integer> personToIndex = new HashMap<>();
        Map<FamilyNode, Integer> familyToIndex = new HashMap<>();

        int height = 500;
        int width = 1000;
        int pixelBuffer = 25;
        //int levelLimit = levels.keySet().size();
        System.out.println("How many generations of descendants would you like to see? ");
        int levelLimit = scanner.nextInt();

        levelOrdering.setCoordinates(levelLimit, height, width, pixelBuffer);


        //adds spouses and assigns location directly beside husband/wife
        if(!spouseUpdate.isEmpty()){
            levelOrdering.spouseUpdateUneven(spouseUpdate);
        }

        int indexCount = 0;
        for(int i = 0; i < levelLimit; i++) {
            if(pLevels.get(i) == null)
                break;
            for(PersonNode per : pLevels.get(i)){
                nodes.add(new Node(per.getName(), per.getX(), per.getY(), per.getId(), per.getFamC(), per.getFamS(), indexCount + "", StaticUtils.getInitials(per.getName()), per.getSex(), per.getBirthday(), per.getCopyNum()));
                indexCount++;
                personToIndex.put(per, nodes.size() - 1);
            }
        }

        String answer = levelOrdering.handleBirthYearOrdering(nodes, height, pixelBuffer);

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

                if(answer.toLowerCase().equals("yes")){
                        fam.setCoordinateAverage();
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


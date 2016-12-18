package modules;

import graph.FamilyNode;
import graph.PersonNode;
import model.Node;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/*
 * The purpose of this class is to house the code that does the final positioning of nodes
 * in many of the descendant layouts used. The code assigns a Y location based on the level,
 * adds a corresponding 'Node' to the list that will be written out, and takes care of necessary
 * book keeping with respect to creating the final links that will be written out
 */
public class IndexingUtil {

    ArrayList<Node> nodes;
    Map<PersonNode, Integer> personToIndex;
    Map<FamilyNode, Integer> familyToIndex;

    public IndexingUtil(ArrayList<Node> nodes,
                        Map<PersonNode, Integer> personToIndex,
                        Map<FamilyNode, Integer> familyToIndex){
        this.nodes = nodes;
        this.personToIndex = personToIndex;
        this.familyToIndex = familyToIndex;
    }

    /*
     * Input :
     *  int height : height of the windows
     *  int pixelBuffer : buffer on all edges of the graphed window
     *  Map<Integer,ArrayList<PersonNode>> pLevels : contains a map of level to list of PersonNodes
     *  Map<Integer,ArrayList<FamilyNode>> fLevels : contains a map of level to list of FamilyNodes
     * Output : n/a
     * Description : This function uses the inputted parameters to assign a Y value to all nodes, add these
     * nodes to the list for writing out, and popular the personToIndex and familyToIndex maps that are used
     * for assigning links
     */
    public void finalBookKeeping(int height, int pixelBuffer, Map<Integer,
            ArrayList<PersonNode>> pLevels, Map<Integer,
            ArrayList<FamilyNode>> fLevels, int levelLimit){

        Scanner scanner = new Scanner(System.in);

        int levelIter = 0;
        int indexCount = 0;
        for(int i = 0; i < levelLimit; i++) {
            for(PersonNode per : pLevels.get(i)){
                double yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                per.setY((int) yVal);

                nodes.add(new Node(per.getName(), per.getX(), per.getY(), per.getId(), per.getFamC(), per.getFamS(), indexCount + "", StaticUtils.getInitials(per.getName()), per.getSex(), per.getBirthday(), per.getCopyNum()));
                indexCount++;
                personToIndex.put(per, nodes.size() - 1);
            }
            levelIter += 2;
        }

        levelIter = 1;
        for(int i = 0; i < levelLimit-1; i++){
            for(FamilyNode fam : fLevels.get(i)){
                int yVal = (((height - 2*pixelBuffer) / (2*levelLimit)) * (levelIter+1));
                fam.setY(yVal);
                fam.setCoordinateAverageX();

                nodes.add(new Node(fam.getId(), fam.getX(), fam.getY(), fam.getId(), null, null, indexCount + "", fam.getId(), null, null, 0));
                indexCount++;
                familyToIndex.put(fam, nodes.size()-1);
            }
            levelIter += 2;
        }
    }

}

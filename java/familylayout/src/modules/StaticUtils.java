package modules;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import graph.FamilyNode;
import graph.PersonNode;
import model.Graph;
import model.Link;
import model.Node;
import model.Person;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/*
 * This class houses all functions that are not related in an OO sense. In
 * general these functions are chunks of code that were repeated multiple times
 * across many different classes
 */
public class StaticUtils {

    static String dataIn = "/home/cole/Desktop/research/genealogy-viz/java/familylayout/data/test_herman.json";
    static String dataOut = "/home/cole/Desktop/research/genealogy-viz/d3/layout.json";

    /*
     * Input : a nodes ID
     * Output : boolean
     * Description : returns true if ID is a family id, false otherwise
     */
    public static boolean isFamilyNode(String id){
        if(id.charAt(2) == 'F')
            return true;
        else
            return false;
    }

    /*
     * Input : Maps that map person/family nodes to their index in the Node list, list of person and family nodes
     * Output : list of links or edges
     * Description : creates link objects by working through outgoing edges of person and family nodes. Uses
     * indexes from Nodes list to create link objects
     */
    public static ArrayList<Link> createLinks1(Map<PersonNode, Integer> pIndex, Map<FamilyNode, Integer> fIndex, ArrayList<PersonNode> p, ArrayList<FamilyNode> f){
        ArrayList<Link> links = new ArrayList<>();

        //creating links that are outgoing from a PersonNode
        for(PersonNode per : p){
            for(FamilyNode fam : per.getOut()){
                if(pIndex.get(per) != null && fIndex.get(fam) != null){
                    Link temp = new Link(pIndex.get(per), fIndex.get(fam));
                    if(!links.contains(temp)){
                        links.add(temp);
                    }
                }
            }
        }

        //creating links taht are outgoing from a FamilyNode
        for(FamilyNode fam : f){
            if(fam != null){
                for(PersonNode per : fam.getOut()){
                    if(fIndex.get(fam) != null && pIndex.get(per) != null){
                        Link temp = new Link(fIndex.get(fam), pIndex.get(per));
                        if(!links.contains(temp)){
                            links.add(temp);
                        }
                    }
                }
            }
        }

        //adds a links for duplicate nodes
        for(PersonNode per : p){
            if(per.getCopyNum() == 1){
                for(PersonNode per1 : p){
                    if(per.equals(per1) && per.getCopyNum() != per1.getCopyNum()){
                        Link l = new Link(pIndex.get(per), pIndex.get(per1));
                        l.setNote("same");
                        links.add(l);
                    }
                }
            }
        }

        return links;
    }

    /*
     * Input : full name of individual
     * Output : initials
     * Description : This function parses the full name of a person and returns their initials
     */
    public static String getInitials(String name){
        String[] splited = name.split("\\s+");
        String initials = "";
        for(String word : splited){
            if(word.charAt(0) == '/' && word.length() > 1){
                initials += word.substring(1,2).toUpperCase();
            } else if(word.length() > 1)
                initials += word.substring(0,1).toUpperCase();
        }
        return initials;
    }

    /*
     * Input : list of nodes - store all nodes that will be written out,
     *         Set of Family ids,
     *         Mapping of node id to index in nodes list
     * Output : list of links used writing
     * Description : compares every family against every person, if a match occurs a link is created.
     */
    public static ArrayList<Link> createLinks(ArrayList<Node> nodes, Set<String> famSet, Map<String, Integer> idToNodeIndex){
        ArrayList<Link> links = new ArrayList<>();

        for(String fam : famSet){
            for(int i = 0; i < nodes.size(); i++){
                Node node = nodes.get(i);
                if(node.getFamC() != null && node.getId() != null && node.getFamC().equals(fam)){

                    int source = 0;
                    int target = 0;
                    if(idToNodeIndex.get(node.getFamC()) == null)
                        source = 0;
                    else
                        source = idToNodeIndex.get(node.getFamC());
                    if(idToNodeIndex.get(node.getId()) == null)
                        target = 0;
                    else
                        target = idToNodeIndex.get(node.getId());

                    if(isFamilyNode(nodes.get(source).getId()) || isFamilyNode(nodes.get(target).getId()))
                        links.add(new Link(source, target));
                }
                if(node.getFamS() != null && node.getId() != null && node.getFamS().equals(fam)){

                    int source = 0;
                    int target = 0;
                    if(idToNodeIndex.get(node.getId()) == null)
                        source = 0;
                    else
                        source = idToNodeIndex.get(node.getId());
                    if(idToNodeIndex.get(node.getFamS()) == null)
                        target = 0;
                    else
                        target = idToNodeIndex.get(node.getFamS());

                    if(isFamilyNode(nodes.get(source).getId()) || isFamilyNode(nodes.get(target).getId()))
                        links.add(new Link(source, target));
                }
            }
        }
        return links;
    }

    /*
     * Input :
     * Output : Array of Person Objects
     * Description : Reads in Person Objects from a JSON file
     */
    public static Person[] readInput() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Person[] persons = objectMapper.readValue(new File(dataIn), Person[].class);
        return persons;
    }

    /*
     * Input : list of nodes and links (edges)
     * Output :
     * Description : creates a Graph object which is written as JSON to a file
     */
    public static void writeOutput(ArrayList<Node> nodes, ArrayList<Link> graphedLinks) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Graph g = new Graph(nodes, graphedLinks);
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(dataOut), g);
    }

    /*
    * Input : Map of level number to list of nodes in that level, an individual person node
    * Output : boolean
    * Description : returns true if the PersonNode is a member of a level, false otherwise
    */
    public static boolean isInMapP(Map<Integer, ArrayList<PersonNode>> pLevels, PersonNode per){
        for(int i = 0; i < pLevels.keySet().size(); i++){
            for(PersonNode p : pLevels.get(i)){
                if(p.equals(per)){
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Input : Map of level number to list of nodes in that level, an individual family node
     * Output : boolean
     * Description : returns true if the FamilyNode is a member of a level, false otherwise
     */
    public static boolean isInMapF(Map<Integer, ArrayList<FamilyNode>> fLevels, FamilyNode fam){
        for(int i = 0; i < fLevels.keySet().size(); i++){
            for(FamilyNode f : fLevels.get(i)){
                if(f.equals(fam)){
                    return true;
                }
            }
        }
        return false;
    }

}

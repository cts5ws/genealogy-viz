import model.Link;
import model.Node;
import model.Person;
import modules.StaticUtils;

import java.util.*;

import java.io.IOException;

/*
 * The Layout class is the first attempt at a family tree. This approach became useless after a more
 * efficient object orientated approach was implemented with all other layout.
 */
public class Layout {
    public static void main(String[] args) throws IOException {

        //READING IN DATA **********************************************************************************************
        Person[] persons = StaticUtils.readInput();

        //SET UP DATA STRUCTURES FOR LINKING***************************************************************************
        ArrayList<String> nodeIDs = new ArrayList<>();
        Set<String> famSet = new HashSet<>();
        Map<String, String> idToName = new HashMap<>();
        ArrayList<Person> personLimitSubset = new ArrayList<>();
        Map<String, String> idToFamC = new HashMap<>();
        Map<String, String> idToFamS = new HashMap<>();
        Map<String, Integer> idToPersonsIndex = new HashMap<>();

        int iter = 0;
        int personLimit = persons.length;
        //int personLimit = 100;
        for (int i = 0; i < persons.length; i++) {
            Person person = persons[i];

            if (person.getId() != null) {
                famSet.add(person.getFamC());
                famSet.add(person.getFamS());
                nodeIDs.add(person.getId());

                idToFamC.put(person.getId(), person.getFamC());
                idToFamS.put(person.getId(), person.getFamS());

                idToPersonsIndex.put(person.getId(), i);

                if (!nodeIDs.contains(person.getFamC()) && person.getFamC() != null) {
                    nodeIDs.add(person.getFamC());
                }
                if (!nodeIDs.contains(person.getFamS()) && person.getFamS() != null) {
                    nodeIDs.add(person.getFamS());
                }

                idToName.put(person.getId(), person.getName());
                if (iter == personLimit) {
                    break;
                }
                personLimitSubset.add(person);
                iter++;
            }
        }

        ArrayList<Link> links = new ArrayList<>();
        ArrayList<Link> reverseLinks = new ArrayList<>();

        //LINKS parent nodes -> family node -> children nodes -> ... repeat ********************************************
        for (String fam : famSet) {
            for (Person person : personLimitSubset) {
                if (person.getFamC() != null && person.getFamC().equals(fam)) {
                    links.add(new Link(nodeIDs.indexOf(fam), nodeIDs.indexOf(person.getId())));
                    reverseLinks.add(new Link(nodeIDs.indexOf(person.getId()), nodeIDs.indexOf(fam)));
                }
                if (person.getFamS() != null && person.getFamS().equals(fam)) {
                    links.add(new Link(nodeIDs.indexOf(person.getId()), nodeIDs.indexOf(fam)));
                    reverseLinks.add(new Link(nodeIDs.indexOf(fam), nodeIDs.indexOf(person.getId())));
                }
            }
        }

        //DETERMINE X AND Y FOR EACH NODE*******************************************************************************

        //CREATING DIFFERENT LEVELS
        Map<Integer, Set<Integer>> levels = new HashMap<>();
        Set<Integer> currentLevelNodes = new HashSet<>();

        currentLevelNodes.add(189);
        levels.put(0, new HashSet<Integer>() {{
            add(189);
        }});

        while (true) {
            Set<Integer> nextLevelNodes = new HashSet<>();

            for (int node : currentLevelNodes) {
                for (Link link : links) {
                    if (node == link.getSource()) {
                        nextLevelNodes.add(link.getTarget());
                    }
                }
            }

            if (nextLevelNodes.isEmpty()) {
                break;
            } else {
                levels.put(levels.keySet().size(), nextLevelNodes);
                currentLevelNodes.clear();
                currentLevelNodes.addAll(nextLevelNodes);
            }
        }

        Map<Integer, Set<Integer>> tempLevels = new HashMap<>();
        for (int i = levels.keySet().size() - 1; i >= 0; i--) {
            tempLevels.put(i, new HashSet<Integer>());
        }

        for (int i = levels.keySet().size() - 1; i > 0; i--) {
            Set<Integer> currentLevel = levels.get(i);
            for (int node : currentLevel) {
                for (Link rLink : reverseLinks) {
                    if (node == rLink.getSource()) {
                        tempLevels.get(i - 1).add(rLink.getTarget());
                    }
                }
            }
        }

        for (int i = levels.keySet().size() - 1; i >= 0; i--) {
            levels.get(i).addAll(tempLevels.get(i));
        }


        //ASSIGNING X AND Y BASED ON LEVELS
        ArrayList<Node> nodes = new ArrayList<>();

        int height = 500;
        int width = 1000;
        int pixelBuffer = 25;
        //int levelLimit = levels.keySet().size();
        int levelLimit = 6;

        for (int i = 0; i < levelLimit; i++) {
            int yVal = (((height - 2 * pixelBuffer) / levelLimit) * (i + 1));
            int xIter = 0;

            //**************************************************************experimental

            ArrayList<Integer> level = new ArrayList<>();
            level.addAll(levels.get(i));
            ArrayList<Integer> sortedLevel = new ArrayList<>();

            if (i % 2 == 0) {//layer of people

                Map<String, ArrayList<Integer>> famSToIndex = new HashMap<>();
                Map<String, ArrayList<Integer>> famCToIndex = new HashMap<>();

                for (int j = 0; j < level.size(); j++) {

                    int index = idToPersonsIndex.get(nodeIDs.get(level.get(j)));

                    if (persons[index].getFamC() != null) {
                        if (famCToIndex.get(persons[index].getFamC()) == null) {
                            ArrayList<Integer> temp = new ArrayList<>();
                            temp.add(nodeIDs.indexOf(persons[index].getId()));
                            famCToIndex.put(persons[index].getFamC(), temp);
                        } else {
                            famCToIndex.get(persons[index].getFamC()).add(nodeIDs.indexOf(persons[index].getId()));
                        }
                    } else if (persons[index].getFamS() != null) {
                        if (famSToIndex.get(persons[index].getFamS()) == null) {
                            ArrayList<Integer> temp = new ArrayList<>();
                            temp.add(nodeIDs.indexOf(persons[index].getId()));
                            famSToIndex.put(persons[index].getFamS(), temp);
                        } else {
                            famSToIndex.get(persons[index].getFamS()).add(nodeIDs.indexOf(persons[index].getId()));
                        }
                    }
                    if (persons[index].getFamS() == null && persons[index].getFamC() == null) {
                        System.out.println("No families");
                    }
                }

                ArrayList<String> sortedFam = new ArrayList<>();
                sortedFam.addAll(famSToIndex.keySet());
                sortedFam.addAll(famCToIndex.keySet());


                Collections.sort(sortedFam, String.CASE_INSENSITIVE_ORDER);

                for (String fam : sortedFam) {
                    if (famSToIndex.get(fam) != null) {
                        for (int index : famSToIndex.get(fam)) {
                            //System.out.println(fam + " - " + index);
                            sortedLevel.add(index);

                        }
                    }

                }
                for (String fam : sortedFam) {
                    if (famCToIndex.get(fam) != null) {
                        for (int index : famCToIndex.get(fam)) {
                            sortedLevel.add(index);
                        }
                    }
                }

                System.out.println(sortedLevel);
                //ArrayList<Integer> adjustedSortedLevel = new ArrayList<>(sortedLevel);
                for (int j = sortedLevel.size() - 1; j >= 0; j--) {
                    int index = idToPersonsIndex.get(nodeIDs.get(level.get(j)));
                    String famS = persons[index].getFamS();
                    if (famS != null) {
                        for (int k = sortedLevel.size() - 1; k >= 0; k--) {
                            int tempIndex = idToPersonsIndex.get(nodeIDs.get(level.get(k)));
                            String tempFamS = persons[tempIndex].getFamS();
                            if (famS.equals(tempFamS)) {
                                sortedLevel.remove(level.get(j));
                                sortedLevel.add(k, level.get(j));
                            }
                        }
                    }
                }
                System.out.println(sortedLevel);

            } else {//layer of family nodes

                ArrayList<Integer> famLevel = new ArrayList<>();
                famLevel.addAll(levels.get(i - 1));

                Map<String, Integer> famToIndex = new HashMap<>();
                for (int j = 0; j < level.size(); j++) {
                    famToIndex.put(nodeIDs.get(level.get(j)), level.get(j));
                }
                //System.out.println(famToIndex);
                for (int j = 0; j < famLevel.size(); j++) {
                    int index = idToPersonsIndex.get(nodeIDs.get(famLevel.get(j)));
                    String famS = persons[index].getFamS();
                    System.out.println(famS);
                    if (famS != null && !sortedLevel.contains(famToIndex.get(famS))) {
                        sortedLevel.add(famToIndex.get(famS));
                    }
                }
                System.out.println("level size: " + level);
                System.out.println("sorted level size : " + sortedLevel);


            }

            //*************************************************Experimental

            for (int node : sortedLevel) {
                //for(int node : levels.get(i)){
                //System.out.print("node: " + node + " - ");
                String tempId;
                if (idToName.containsKey(nodeIDs.get(node))) {
                    tempId = idToName.get(nodeIDs.get(node));
                } else {
                    tempId = nodeIDs.get(node);
                }
                int xVal = ((width - 2 * pixelBuffer) / (levels.get(i).size() + 1)) * (xIter + 1);
                xIter++;
            }
        }

        Map<String, Integer> idToNodeIndex = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            idToNodeIndex.put(nodes.get(i).getId(), i);
        }

        ArrayList<Link> graphedLinks = StaticUtils.createLinks(nodes, famSet, idToNodeIndex);

        //WRITES OUTPUT TO JSON*****************************************************************************************
        StaticUtils.writeOutput(nodes, graphedLinks);
    }
}



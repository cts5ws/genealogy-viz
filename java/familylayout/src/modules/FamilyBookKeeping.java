package modules;

import graph.FamilyNode;
import graph.PersonNode;
import model.Person;

import java.util.ArrayList;

/*
 * The purpose of this class is to handle the inital book keeping that occurs
 * by iterating through the inputted array of 'Person Objects'
 */
public class FamilyBookKeeping {

    Person[] persons;

    public FamilyBookKeeping(Person[] persons){
        this.persons = persons;
    }

    /*
     * Input : none
     * Output : ArrayList<PersonNodes> containing all people that were read in
     * Description : This function iterates through the array of Person objects and
     * creates a PersonNode object for each. These all get added to an ArrayList and
     * returned.
     */
    public ArrayList<PersonNode> createPersonNodeList(){
        ArrayList<PersonNode> retVal = new ArrayList<>();

        for(int i = 0; i < persons.length; i ++) {
            Person person = persons[i];

            if(person.getId() != null){
                retVal.add(new PersonNode(-1,-1,person.getType(), person.getId(),
                        new ArrayList<FamilyNode>(), new ArrayList<FamilyNode>(),
                        person.getBirthday(), person.getFamC(), person.getFamS(),
                        person.getName(), person.getSex()));
            }
        }
        return  retVal;
    }

    /*
     * Input : none
     * Output : ArrayList<FamilyNode> containing all families from the inputted data
     * Description : This function checks the famC and famS of every inputted person
     * and returns a list of all families in the inputted data
     */
    public ArrayList<FamilyNode> createFamilyNodeList(){
        ArrayList<FamilyNode> retVal = new ArrayList<>();
        for(Person person : persons){

            FamilyNode famC = null;
            FamilyNode famS = null;

            if(person.getFamC() != null){
                famC = new FamilyNode(-1, -1, "FAM", person.getFamC(),
                        new ArrayList<PersonNode>(), new ArrayList<PersonNode>());
            }
            if(person.getFamS() != null){
                famS = new FamilyNode(-1, -1, "FAM", person.getFamS(),
                        new ArrayList<PersonNode>(), new ArrayList<PersonNode>());
            }


            if(!retVal.contains(famC)){
                retVal.add(famC);
            }
            if(!retVal.contains(famS)){
                retVal.add(famS);
            }
        }
        return retVal;
    }

}

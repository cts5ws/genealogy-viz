package util;

import graph.PersonNode;


/*
 * This class is used to handle inter-family marriage in the OOLayoutWeighted layout
 */
public class AdjacentPersonUpdate {

    PersonNode source;
    PersonNode target;

    public AdjacentPersonUpdate(PersonNode source, PersonNode target){
        this.source = source;
        this.target = target;
    }

    public boolean equals(Object o){
        if(o instanceof AdjacentPersonUpdate){
            AdjacentPersonUpdate a = (AdjacentPersonUpdate)o;

            return (this.getSource().equals(a.getSource()) && this.getTarget().equals(a.getTarget())) ||
                    (this.getSource().equals(a.getTarget()) && this.getTarget().equals(a.getSource()));

        } else {
            return false;
        }
    }

    public PersonNode getSource() {
        return source;
    }

    public void setSource(PersonNode source) {
        this.source = source;
    }

    public PersonNode getTarget() {
        return target;
    }

    public void setTarget(PersonNode target) {
        this.target = target;
    }
}

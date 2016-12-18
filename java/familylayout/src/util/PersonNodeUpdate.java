package util;

import graph.PersonNode;

/*
 * The PersonNodeUpdate class is used to add a node to specific level while preserving ordering.
 */
public class PersonNodeUpdate {

    //the level the node should be placed on
    int level;

    //the index of that level where the node belongs
    int index;

    //then node to be placed itself
    PersonNode node;

    public PersonNodeUpdate(int level, int index, PersonNode node) {
        this.level = level;
        this.index = index;
        this.node = node;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PersonNode getNode() {
        return node;
    }

    public void setNode(PersonNode node) {
        this.node = node;
    }
}

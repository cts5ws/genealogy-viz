package util;

import graph.FamilyNode;

/*
 * The FamilyNodeUpdate class is used to update family nodes in the graph while preserving orderingi
 */
public class FamilyNodeUpdate {

    //level to place the node
    int level;

    //index in the level to place the node
    int index;

    //the node to be placed
    FamilyNode node;

    public FamilyNodeUpdate(int level, int index, FamilyNode node) {
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

    public FamilyNode getNode() {
        return node;
    }

    public void setNode(FamilyNode node) {
        this.node = node;
    }
}

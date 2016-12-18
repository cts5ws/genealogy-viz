package model;

import java.util.ArrayList;

/*
 * Graph implementation consisting of nodes and edges, used to format for D3.
 * This is a POJO used only for output
 */
public class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Link> links;

    public Graph(ArrayList<Node> nodes, ArrayList<Link> links){
        this.nodes = nodes;
        this.links = links;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.collections.ObservableList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * The most naive approach for a layouting algorithm.
 * @author Tobias Mertz
 */
public class LayoutGeneratorNaive implements LayoutGenerator {
    
    private final boolean debug;
    private VFlow workflow;
    private VNode[] nodes;
    private int nodecount;
    private int conncount;
    private LinkedList<Tuple<Integer,Integer>> connectionList;
    
    /**
     * Default constructor.
     * Debug is set to false.
     */
    public LayoutGeneratorNaive() {
        this.debug = false;
    }
    
    /**
     * Constructor with debug-functionality.
     * @param pdebug Boolean: debugging output enable/disable
     */
    public LayoutGeneratorNaive(boolean pdebug) {
        this.debug = pdebug;
        if(this.debug) System.out.println("Creating layout generator");
    }
    
    /**
     * Sets up the node- and edge-model for the current workflow.
     * @param pworkflow VWorfklow: current workflow to be layouted.
     */
    @Override
    public void setUp(VFlow pworkflow) {
        if(this.debug) System.out.println("Setting up workflow for layout generation.");
        this.workflow = pworkflow;
        this.connectionList = new LinkedList<>();
        
        // Setting up nodes
        ObservableList<VNode> nodesTemp = this.workflow.getNodes(); 
        this.nodecount = nodesTemp.size();
        this.nodes = new VNode[this.nodecount];
        
        int i;
        for(i = 0; i < nodesTemp.size(); i++) {
            this.nodes[i] = nodesTemp.get(i);
        }
        
        // Setting up edges
        Connections controlConnections = this.workflow.getConnections("control");
        Connections dataConnections = this.workflow.getConnections("data");
        Connections eventConnections = this.workflow.getConnections("event");
        
        // For seperation of connection-types. Change from here.
        ObservableList<Connection> allConnections = controlConnections.getConnections();
        allConnections.addAll(dataConnections.getConnections());
        allConnections.addAll(eventConnections.getConnections());

        for(i = 0; i < allConnections.size(); i++) {
            Integer out = getNodeID(allConnections.get(i).getSender().getNode());
            Integer in = getNodeID(allConnections.get(i).getReceiver().getNode());
            this.connectionList.add(new Tuple<>(out, in));
        }
        if(this.debug) System.out.println("Setup complete with " + this.nodecount + " nodes and " + this.conncount + " edges.");
    }
    
    /**
     * Generates a Layout for the workflow given at SetUp.
     */
    @Override
    public void generateLayout() {
        if(this.debug) System.out.println("Generating layout.");
        int i, j, k;
        LinkedList<LinkedList<Integer>> layers = new LinkedList<>();
        
        // first layer contains all nodes without input
        LinkedList<Integer> noIn = new LinkedList<>();
        for(i = 0; i < this.nodecount; i++) {
            boolean in = false;
            for(j = 0; j < this.connectionList.size(); j++) {
                if(this.connectionList.get(j).y.equals((Integer) i))
                    in = true;
            }
            if(!in)
                noIn.add((Integer) i);
        }
        layers.add(noIn);
        
        // layer n contains all nodes with inputs from nodes in layer n-1
        i = 0;
        while(true) {
            LinkedList<Integer> currentLayer = new LinkedList<>();
            for(j = 0; j < layers.get(i).size(); j++) {
                Integer currentNode = layers.get(i).get(j);
                for(k = 0; k < this.connectionList.size(); k++) {
                    if(this.connectionList.get(k).x.equals(currentNode))
                        currentLayer.add(this.connectionList.get(k).y);
                }
            }
            if(currentLayer.size() > 0) {
                layers.add(currentLayer);
                i++;
            }
            else
                break;
        }
        
        // remove doubles
        boolean[] duplicates = new boolean[this.nodecount];
        for(i = 0; i < this.nodecount; i++) {
            duplicates[i] = false;
        }
        for(i = layers.size()-1; i >= 0; i--) {
            for(j = 0; j < layers.get(i).size(); j++) {
                if(duplicates[layers.get(i).get(j)]) {
                    layers.get(i).remove(j);
                    j--;
                }
                else
                    duplicates[layers.get(i).get(j)] = true;
            }
        }
        
        // Calculate size of layers.
        // In the naive approach all layers have the same size and nodes are equidistant.
        double sizeX = 0;
        double sizeY = 0;
        for(i = 0; i < layers.size(); i++) {
            double tempsizeY = 0;
            for(j = 0; j < layers.get(i).size(); j++) {
                VNode currentNode;
                for(k = 0; k < this.nodecount; k++) {
                    if(layers.get(i).get(j).equals((Integer) k)) {
                        currentNode = this.nodes[k];
                        if(sizeX < currentNode.getWidth())
                            sizeX = currentNode.getWidth();
                        tempsizeY += currentNode.getHeight();
                        break;
                    } 
                }
            }
            if(tempsizeY > sizeY)
                sizeY = tempsizeY;
        }
        
        // apply layout
        double posX = 0;
        double posY = 0;
        for(i = 0; i < layers.size(); i++) {
            double distX = 1.5 * sizeX;
            double distY = 1.5 * sizeY / layers.get(i).size();
            posY = 0;
            for(j = 0; j < layers.get(i).size(); j++) {
                for(k = 0; k < this.nodecount; k++) {
                    if(layers.get(i).get(j).equals((Integer) k)) {
                        VNode currentNode = this.nodes[k];
                        currentNode.setX(posX);
                        currentNode.setY(posY);
                        if(this.debug) System.out.println(currentNode.getId() + " | X: " + posX + " Y: " + posY);
                        posY += distY;
                        break;
                    }
                }
            }
            posX += distX;
        }
    }
    
    /**
     * Gets the model-ID for the given Node.
     * @param pnode VNode: the given Node.
     * @return Integer: ID of the given Node.
    */
    private Integer getNodeID(VNode pnode) {
        int i;
        for(i = 0; i < this.nodecount; i++) {
            if(this.nodes[i].equals(pnode))
                return i;
        }
        return -1;
    }
    
    /**
     * Generic Tuple class.
     * @param <X> Type of first entry in the Tuple.
     * @param <Y> Type of second entry in the Tuple.
     */
    class Tuple<X, Y> {
        public final X x;
        public final Y y;
        
        /**
         * Constructor setting both entries.
         * @param px X: first entry.
         * @param py Y: second entry.
         */
        public Tuple(X px, Y py) {
            this.x = px;
            this.y = py;
        }
        
        @Override
        public String toString() {
            return "{" + this.x.toString() + ", " + this.y.toString() + "}";
        }
        
        @Override
        public boolean equals(Object other) {
            if(other == null) {
                return false;
            }
            if(other == this) {
                return true;
            }
            if(!(other instanceof Tuple)) {
                return false;
            }
            Tuple<Object, Object> this_ = (Tuple<Object, Object>) this;
            Tuple<Object, Object> other_ = (Tuple<Object, Object>) other;
            return (this_.x.equals(other_.x) && this_.y.equals(other_.y));
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.x);
            hash = 97 * hash + Objects.hashCode(this.y);
            return hash;
        }
    }
    
}

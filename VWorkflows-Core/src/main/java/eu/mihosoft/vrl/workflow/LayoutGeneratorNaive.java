/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.collections.ObservableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import javafx.collections.ObservableMap;

/**
 * @author Tobias Mertz
 * The most naive approach for a layouting algorithm.
 * Idea:
 * - nodes are arranged in columns
 * - nodes with in-degree of 0 are placed on first column
 * - other nodes are placed one column after their latest parent node
 */
public class LayoutGeneratorNaive implements LayoutGenerator {
    
    private VFlow workflow;
    private LinkedList<Tuple<Integer,Integer>> connectionList;
    private boolean recursive;
    private boolean autoscaleNodes;
    private boolean launchRemoveCycles;
    private double scaling;
    private boolean debug;
    
    private VNode[] nodes;
    private int nodecount;
    private int conncount;
    private boolean cycle;
    
    /**
     * Default constructor.
     * Debug is set to false.
     */
    public LayoutGeneratorNaive() {
        this.debug = false;
        initialize();
    }
    
    /**
     * Constructor with workflow parameter.
     * @param pworkflow VFlow: workflow to be setup.
     */
    public LayoutGeneratorNaive(VFlow pworkflow) {
        this.workflow = pworkflow;
        this.debug = false;
        initialize();
    }
    
    /**
     * Constructor with debug-functionality.
     * @param pdebug Boolean: debugging output enable/disable
     */
    public LayoutGeneratorNaive(boolean pdebug) {
        this.debug = pdebug;
        initialize();
        if(this.debug) System.out.println("Creating layout generator");
    }
    
    /**
     * Constructor with workflow parameter and debug-functionality.
     * @param pworkflow VFlow: workflow to be setup.
     * @param pdebug Boolean: debugging-output enable/disable.
     */
    public LayoutGeneratorNaive(VFlow pworkflow, boolean pdebug) {
        this.workflow = pworkflow;
        this.debug = pdebug;
        initialize();
        if(this.debug) System.out.println("Creating layout generator");
    }
    
    /**
     * 
     */
    private void initialize() {
        this.recursive = true;
        this.autoscaleNodes = false;
        this.launchRemoveCycles = true;
        this.scaling = 1.5;
    }
    
    // <editor-fold defaultstate="collapsed" desc="getter">
    /**
     * Get the workflow to layout.
     * @return VFlow workflow.
     */
    @Override
    public VFlow getWorkflow() {
        return this.workflow;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public boolean getRecursive() {
        return this.recursive;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public boolean getAutoscaleNodes() {
        return this.autoscaleNodes;
    }
    
    /**
     * 
     * @return 
     */
    public boolean getLaunchRemoveCycles() {
        return this.launchRemoveCycles;
    }
    
    /**
     * 
     * @return 
     */
    public double getScaling() {
        return this.scaling;
    }
    
    /**
     * Get status of debugging output.
     * @return Boolean debugging output enabled/disabled.
     */
    @Override
    public boolean getDebug() {
        return this.debug;
    }
    
    /**
     * Get the Graph modeled after the workflow.
     * @return LinkedList<Tuple<Integer, Integer>> model graph.
     */
    public LinkedList<Tuple<Integer, Integer>> getModelGraph() {
        return this.connectionList;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="setter">
    /**
     * Set the workflow to be layouted.
     * @param pworkflow VFlow.
     */
    @Override
    public void setWorkflow(VFlow pworkflow) {
        this.workflow = pworkflow;
    }
    
    /**
     * 
     * @param precursive 
     */
    @Override
    public void setRecursive(boolean precursive) {
        this.recursive = precursive;
    }
    
    /**
     * 
     * @param pautoscaleNodes 
     */
    @Override
    public void setAutoscaleNodes(boolean pautoscaleNodes) {
        this.autoscaleNodes = pautoscaleNodes;
    }
    
    /**
     * 
     * @param plaunchRemoveCycles 
     */
    public void setLaunchRemoveCycles(boolean plaunchRemoveCycles) {
        this.launchRemoveCycles = plaunchRemoveCycles;
    }
    
    /**
     * 
     * @param pscaling 
     */
    public void setScaling(double pscaling) {
        this.scaling = pscaling;
    }
    
    /**
     * Set status of debugging output.
     * @param pdebug Boolean.
     */
    @Override
    public void setDebug(boolean pdebug) {
        this.debug = pdebug;
    }
    
    /**
     * Set the model graph to be layouted.
     * @param pconnectionList LinkedList<Tuple<Integer, Integer>>
     */
    public void setModelGraph(LinkedList<Tuple<Integer, Integer>> pconnectionList) {
        this.connectionList = pconnectionList;
    }
    // </editor-fold>
    
    /**
     * Sets up the node- and edge-model for the current workflow.
     * @return boolean
     */
    public boolean setUp() {
        int i;
        if(this.workflow == null) return false;
        // gather nodelist from workflow
        this.connectionList = new LinkedList<>();
        ObservableList<VNode> nodesTemp = this.workflow.getNodes(); 
        if(nodesTemp == null) return false;
        this.nodecount = nodesTemp.size();
        this.nodes = new VNode[this.nodecount];
        
        for(i = 0; i < nodesTemp.size(); i++) {
            this.nodes[i] = nodesTemp.get(i);
        }
        
        // get all edges
        this.conncount = 0;
        ObservableMap<String, Connections> allConnections = 
                this.workflow.getAllConnections();
        Set<String> keys = allConnections.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            String currType = it.next();
            Connections currConns = allConnections.get(currType);
            ObservableList<Connection> connections = currConns.getConnections();
            int currConnCount = connections.size();
            for(i = 0; i < currConnCount; i++) {
                this.conncount++;
                Connection currConn = connections.get(i);
                Integer out = getNodeID(currConn.getSender().getNode());
                Integer in = getNodeID(currConn.getReceiver().getNode());
                this.connectionList.add(new Tuple<>(out, in));
            }
        }
        this.cycle = checkCycles();
        if(this.debug) System.out.println("Setup complete with " + this.nodecount + " nodes and " + this.conncount + " edges.");
        return true;
    }
    
    /**
     * Checks if the graph contains cycles.
     * @return boolean
     */
    private boolean checkCycles() {
        int i;
        boolean[] checked = new boolean[this.nodecount];
        for(i = 0; i < this.nodecount; i++) {
            int j;
            for(j = 0; j < this.nodecount; j++) {
                checked[j] = false;
            }
            LinkedList<Integer> succs = new LinkedList<>();
            for(j = 0; j < this.conncount; j++) {
                if(this.connectionList.get(j).x == i) {
                    succs.add(this.connectionList.get(j).y);
                }
            }
            Iterator<Integer> it = succs.iterator();
            while(it.hasNext()) {
                Integer currNode = it.next();
                if(currNode == i) {
                    if(this.debug) System.out.println("graph contains cycles");
                    return true;
                }
                else {
                    if(!checked[currNode]) {
                        for(j = 0; j < this.conncount; j++) {
                            if(Objects.equals(this.connectionList.get(j).x, currNode)) {
                                succs.add(this.connectionList.get(j).y);
                                it = succs.iterator();
                                checked[currNode] = true;
                            }
                        }
                    }
                }
            }
        }
        if(this.debug) System.out.println("graph contains no cycles.");
        return false;
    }
    
    /**
     * Performs a depth-first search and removes all back edges so that the 
     * resulting graph is cycle free.
     */
    private void removeCycles() {
        int i;
        boolean[] checked = new boolean[this.nodecount];
        for(i = 0; i < this.nodecount; i++) {
            checked[i] = false;
        }
        LinkedList<Integer> path = new LinkedList<>();
        
        for(i = 0; i < this.nodecount; i++) {
            remCycR(i, path, checked);
        }
    }
    
    /**
     * Recursive helper-function to be used by removeCycles().
     */
    private void remCycR(Integer curr, LinkedList<Integer> path, boolean[] checked) {
        if(!checked[curr]) {
            path.addFirst(curr);
            checked[curr] = true;
            int i;
            LinkedList<Integer> succs = new LinkedList<>();
            for(i = 0; i < this.conncount; i++) {
                if(Objects.equals(this.connectionList.get(i).x, curr)) {
                    succs.add(this.connectionList.get(i).y);
                }
            }
            if(!succs.isEmpty()) {
                Iterator<Integer> it = succs.iterator();
                while(it.hasNext()) {
                    Integer currSucc = it.next();
                    if(path.contains(currSucc)) {
                        Iterator<Tuple<Integer, Integer>> its = this.connectionList.iterator();
                        while(its.hasNext()) {
                            Tuple<Integer, Integer> currConn = its.next();
                            if(Objects.equals(currConn.x, curr) && Objects.equals(currConn.y, currSucc)) {
                                this.connectionList.remove(currConn);
                                its = this.connectionList.iterator();
                                this.conncount--;
                            }
                        }
                        if(this.debug) System.out.println("removing edge from "
                                + curr + " to " + currSucc);
                    }
                    else {
                        remCycR(currSucc, path, checked);
                    }
                }
            }
            path.removeFirst();
        }
    }
    
    /**
     * Generates a Layout for the workflow given.
     */
    @Override
    public void generateLayout() {
        if(this.debug) System.out.println("Generating layout.");
        if(this.setUp()) {
            if(this.cycle) {
                if(this.launchRemoveCycles) {
                    removeCycles();
                }
                else {
                    if(this.debug) System.out.println("Cycles in graph. please "
                            + "run with launchRemoveCycles set to true.");
                    return;
                }
            }
            
            if(this.recursive) {
                runSubflows();
            }
            
            if(this.autoscaleNodes) {
                // code
            }
            applyLayout();
        }
    }
    
    /**
     * Applies the layout with the same parameters to each subflow.
     */
    private void runSubflows() {
        // initialize sub generator with the same parameters
        LayoutGeneratorNaive subgen = new LayoutGeneratorNaive(false);
        subgen.setRecursive(this.recursive);
        subgen.setAutoscaleNodes(this.autoscaleNodes);
        subgen.setLaunchRemoveCycles(this.launchRemoveCycles);
        // apply layout to each subflow
        Collection<VFlow> subconts = this.workflow.getSubControllers();
        Iterator<VFlow> it = subconts.iterator();
        while(it.hasNext()) {
            VFlow subflow = it.next();
            subgen.setWorkflow(subflow);
            subgen.generateLayout();
        }
    }
    
    /**
     * Applies the layout to the VFlow given.
     */
    private void applyLayout() {
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
        double[] sizeX = new double[layers.size()];
        double sizeY = 0;
        for(i = 0; i < layers.size(); i++) {
            double tempsizeY = 0;
            for(j = 0; j < layers.get(i).size(); j++) {
                sizeX[i] = 0.;
                VNode currentNode;
                for(k = 0; k < this.nodecount; k++) {
                    if(layers.get(i).get(j).equals((Integer) k)) {
                        currentNode = this.nodes[k];
                        if(sizeX[i] < currentNode.getWidth())
                            sizeX[i] = currentNode.getWidth();
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
            double distX = this.scaling * sizeX[i];
            double distY = this.scaling * sizeY / layers.get(i).size();
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

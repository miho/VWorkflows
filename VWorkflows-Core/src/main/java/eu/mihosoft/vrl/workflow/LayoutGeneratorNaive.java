/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
package eu.mihosoft.vrl.workflow;


import edu.uci.ics.jung.graph.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * This class provides a naive implementation for the LayoutGenerator interface.
 * Nodes will be positioned in a layered arrangement, but there is no process 
 * for the minimization of line-crossings.
 * 
 * @author Tobias Mertz
 */
public class LayoutGeneratorNaive implements LayoutGenerator {
    
    // parameters:
    private VFlow workflow;
    private LinkedList<Pair<Integer>> connectionList;
    //private LinkedList<Tuple<Integer,Integer>> connectionList;
    private boolean recursive;
    private boolean autoscaleNodes;
    private boolean launchRemoveCycles;
    private double scaling;
    private double subflowscale;
    private boolean debug;
    
    // internal fields:
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
     * Constructor with debug-functionality.
     * Debug parameter can be set, all other parameters are initialized with 
     * default values.
     * @param pdebug boolean
     */
    public LayoutGeneratorNaive(boolean pdebug) {
        this.debug = pdebug;
        initialize();
        if(this.debug) System.out.println("Creating layout generator");
    }
    
    /**
     * Initializes the fields of the class needed in future methods.
     */
    private void initialize() {
        // default parameters:
        this.recursive = true;
        this.autoscaleNodes = true;
        this.launchRemoveCycles = true;
        this.scaling = 1.5;
        this.subflowscale = 2.;
    }
    
    // <editor-fold defaultstate="collapsed" desc="getter">
    /**
     * Returns the workflow to be laid out.
     * @return VFlow
     */
    @Override
    public VFlow getWorkflow() {
        return this.workflow;
    }
    
    /**
     * If set to true, the layout is applied to all subflows of the given 
     * workflow recursively.
     * default: true
     * @return boolean
     */
    @Override
    public boolean getRecursive() {
        return this.recursive;
    }
    
    /**
     * If set to true, subflow nodes in the given workflow are automatically 
     * scaled to fit their contents.
     * default: true
     * @return boolean
     */
    @Override
    public boolean getAutoscaleNodes() {
        return this.autoscaleNodes;
    }
    
    /**
     * If set to true a depth-first-search is performed and all back edges are 
     * removed from the model graph. The layout is then applied without these 
     * edges.
     * Is only run if the graph contains cycles.
     * default: true
     * @return boolean
     */
    public boolean getLaunchRemoveCycles() {
        return this.launchRemoveCycles;
    }
    
    /**
     * Get the scaling parameter of the algorithm. 
     * Determines the distance between nodes.
     * default: 1.5
     * @return double
     */
    public double getScaling() {
        return this.scaling;
    }
    
    /**
     * Get the scaling factor that is used to scale subflow-nodes in the 
     * autoscaleNodes procedure.
     * default: 2.0
     * @return double
     */
    public double getSubflowscale() {
        return this.subflowscale;
    }
    
    /**
     * If set to true, debugging output will be printed in the command line and 
     * a second representation of the graph will be shown.
     * default: false
     * @return boolean
     */
    @Override
    public boolean getDebug() {
        return this.debug;
    }
    
    /**
     * Returns the Graph modeled after the workflow.
     * @return LinkedList<Pair<Integer>>
     */
    public LinkedList<Pair<Integer>> getModelGraph() {
        return this.connectionList;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="setter">
    /**
     * Set the workflow to be laid out.
     * @param pworkflow VFlow.
     */
    @Override
    public void setWorkflow(VFlow pworkflow) {
        this.workflow = pworkflow;
    }
    
    /**
     * If set to true, the layout is applied to all subflows of the given 
     * workflow recursively.
     * default: true
     * @param precursive boolean
     */
    @Override
    public void setRecursive(boolean precursive) {
        this.recursive = precursive;
    }
    
    /**
     * If set to true, subflow nodes in the given workflow are automatically 
     * scaled to fit their contents.
     * default: true
     * @param pautoscaleNodes boolean
     */
    @Override
    public void setAutoscaleNodes(boolean pautoscaleNodes) {
        this.autoscaleNodes = pautoscaleNodes;
    }
    
    /**
     * If set to true a depth-first-search is performed and all back edges are 
     * removed from the model graph. The layout is then applied without these 
     * edges.
     * Is only run if the graph contains cycles.
     * default: true 
     * @param plaunchRemoveCycles boolean
     */
    public void setLaunchRemoveCycles(boolean plaunchRemoveCycles) {
        this.launchRemoveCycles = plaunchRemoveCycles;
    }
    
    /**
     * Set the scaling parameter of the algorithm. 
     * Determines the distance between nodes.
     * default: 1.5
     * @param pscaling double
     */
    public void setScaling(double pscaling) {
        this.scaling = pscaling;
    }
    
    /**
     * Set the scaling factor that is used to scale subflow-nodes in the 
     * autoscaleNodes procedure.
     * default: 2.0
     * @param psubflowscale double
     */
    public void setSubflowscale(double psubflowscale) {
        this.subflowscale = psubflowscale;
    }
    
    /**
     * If set to true, debugging output will be printed in the command line and 
     * a second representation of the graph will be shown.
     * default: false
     * @param pdebug boolean
     */
    @Override
    public void setDebug(boolean pdebug) {
        this.debug = pdebug;
    }
    
    /**
     * Set the model graph.
     * @param pconnectionList LinkedList<Tuple<Integer, Integer>>
     */
    public void setModelGraph(LinkedList<Pair<Integer>> pconnectionList) {
        this.connectionList = pconnectionList;
    }
    // </editor-fold>
    
    /**
     * Sets up the model-fields. This includes nodearray, nodecount and the 
     * model graph.
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
        
        // copy nodelist into nodearray for better performance in the future
        for(i = 0; i < nodesTemp.size(); i++) {
            this.nodes[i] = nodesTemp.get(i);
        }
        
        // get all edges
        this.conncount = 0;
        ObservableMap<String, Connections> allConnections = 
                this.workflow.getAllConnections();
        // get all types of connections
        Set<String> keys = allConnections.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            // get all connections for current type
            String currType = it.next();
            Connections currConns = allConnections.get(currType);
            ObservableList<Connection> connections = currConns.getConnections();
            int currConnCount = connections.size();
            for(i = 0; i < currConnCount; i++) {
                this.conncount++;
                Connection currConn = connections.get(i);
                // create a tuple of the sender and receiver of the current
                // connection and add it to the connectionlist
                Integer out = getNodeID(currConn.getSender().getNode());
                Integer in = getNodeID(currConn.getReceiver().getNode());
                this.connectionList.add(new Pair<>(out, in));
            }
        }
        this.cycle = checkCycles();
        if(this.debug) System.out.println("Setup complete with " 
                + this.nodecount + " nodes and " + this.conncount + " edges.");
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
            // initialize checked with all entries set to false
            for(j = 0; j < this.nodecount; j++) {
                checked[j] = false;
            }
            LinkedList<Integer> succs = new LinkedList<>();
            for(j = 0; j < this.conncount; j++) {
                if(this.connectionList.get(j).getFirst() == i) {
                    succs.add(this.connectionList.get(j).getSecond());
                }
            }
            Iterator<Integer> it = succs.iterator();
            // iterate through all successors of start node
            while(it.hasNext()) {
                Integer currNode = it.next();
                // if current node is the start node, the start node can be 
                // reached -> there is a cycle
                if(currNode == i) {
                    if(this.debug) System.out.println("graph contains cycles");
                    return true;
                }
                else {
                    // if current node is not the start node and has not yet 
                    // been checked, add all successors of current node to the 
                    // list, reset the iterator, set checked for current
                    // node true.
                    // -> all nodes that are reacheable from start node will 
                    // be checked once.
                    if(!checked[currNode]) {
                        for(j = 0; j < this.conncount; j++) {
                            if(Objects.equals(this.connectionList.get(j).getFirst(),
                                    currNode)) {
                                succs.add(this.connectionList.get(j).getSecond());
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
        // initialize checked with all entries set to false
        for(i = 0; i < this.nodecount; i++) {
            checked[i] = false;
        }
        LinkedList<Integer> path = new LinkedList<>();
        // iterate through all nodes
        for(i = 0; i < this.nodecount; i++) {
            // start recursive search with current node as startingpoint
            remCycR(i, path, checked);
        }
    }
    
    /**
     * Recursive helper-function to be used by removeCycles().
     * @param VNode curr: current node
     * @param path LinkedList<Integer>: the path from the root-node to the 
     * current node
     * @param checked boolean[]: array of length nodecount showing which nodes 
     * were already checked
     */
    private void remCycR(Integer curr, LinkedList<Integer> path, boolean[] checked) {
        if(!checked[curr]) {
            // add current node to path
            path.addFirst(curr);
            checked[curr] = true;
            int i;
            LinkedList<Integer> succs = new LinkedList<>();
            // get successors to current node
            for(i = 0; i < this.conncount; i++) {
                if(Objects.equals(this.connectionList.get(i).getFirst(), curr)) {
                    succs.add(this.connectionList.get(i).getSecond());
                }
            }
            // perform search on all successors.
            if(!succs.isEmpty()) {
                Iterator<Integer> it = succs.iterator();
                while(it.hasNext()) {
                    Integer currSucc = it.next();
                    // if edge from curr to currSucc is a back edge
                    if(path.contains(currSucc)) {
                        // remove all edges from curr to currSucc
                        Iterator<Pair<Integer>> its = this.connectionList.iterator();
                        while(its.hasNext()) {
                            Pair<Integer> currConn = its.next();
                            if(Objects.equals(currConn.getFirst(), curr) 
                                    && Objects.equals(currConn.getSecond(), 
                                            currSucc)) {
                                this.connectionList.remove(currConn);
                                its = this.connectionList.iterator();
                                this.conncount--;
                            }
                        }
                        if(this.debug) System.out.println("removing edge from "
                                + curr + " to " + currSucc);
                    }
                    // if edge from curr to currSucc is not a back edge
                    else {
                        // run recursive search on currSucc
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
        // setup and check for errors
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
            // apply layout to subflows
            if(this.recursive) {
                runSubflows();
            }
            // scale nodes according to their contents.
            if(this.autoscaleNodes) {
                autoscaleNodes();
            }
            // apply layout
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
     * Scales subflow-nodes according to their contents.
     */
    private void autoscaleNodes() {
        Collection<VFlow> subconts = this.workflow.getSubControllers();
        Iterator<VFlow> it = subconts.iterator();
        // iterate over all subflows
        while(it.hasNext()) {
            VFlow subflow = it.next();
            // get node representation of the current subflow
            VNode flownode = subflow.getModel();
            if(this.debug) System.out.println("Resizing subflow-node "
                    + flownode.getId());
            // get nodes from the subflow
            Collection<VNode> subnodes = subflow.getNodes();
            if(subnodes.isEmpty()) continue;
            Iterator<VNode> nodeit = subnodes.iterator();
            double minx = Double.POSITIVE_INFINITY;
            double maxx = Double.NEGATIVE_INFINITY;
            double miny = Double.POSITIVE_INFINITY;
            double maxy = Double.NEGATIVE_INFINITY;
            // iterate over all subflow nodes to get outermost coordinates of 
            // subflow
            while(nodeit.hasNext()) {
                VNode currNode = nodeit.next();
                double x = currNode.getX();
                double y = currNode.getY();
                if(this.debug) System.out.println(currNode.getId() + " has "
                        + "position (" + x + "|" + y + ")");
                if(minx > x) minx = x;
                if(maxx < (x + currNode.getWidth())) maxx = x 
                        + currNode.getWidth();
                if(miny > y) miny = y;
                if(maxy < (y + currNode.getHeight())) maxy = y
                        + currNode.getHeight();
            }
            if(this.debug) System.out.println("extreme positions of " 
                    + flownode.getId() + " are min: (" + minx + "|" + miny 
                    + ") max: (" + maxx + "|" + maxy + ")");
            // calculate size of subflow
            double width = maxx - minx;
            double height = maxy - miny;
            if(this.debug) System.out.println("Resizing subflow-node "
                    + flownode.getId() + " from size (" + flownode.getWidth()
                    + "|" + flownode.getHeight() + ") to size ("
                    + (width / subnodes.size() * this.subflowscale) + "|" 
                    + (height / subnodes.size() * this.subflowscale) + ")");
            // set size of node representing current subflow
            flownode.setWidth(width / subnodes.size() * this.subflowscale);
            flownode.setHeight(height / subnodes.size() * this.subflowscale);
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
                if(this.connectionList.get(j).getSecond().equals((Integer) i))
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
                    if(this.connectionList.get(k).getFirst().equals(currentNode))
                        currentLayer.add(this.connectionList.get(k).getSecond());
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
        double[] sizeX = new double[layers.size()];
        for(i = 0; i < layers.size(); i++) {
            sizeX[i] = 0.;
            Iterator<Integer> it;
            it = layers.get(i).iterator();
            while(it.hasNext()) {
                int curr = it.next();
                VNode currNode = this.nodes[curr];
                if(sizeX[i] < currNode.getWidth())
                    sizeX[i] = currNode.getWidth();
            }
        }
        // apply layout
        double posX = 0;
        double posY;
        for(i = 0; i < layers.size(); i++) {
            double distX = this.scaling * sizeX[i];
            posY = 0;
            for(j = 0; j < layers.get(i).size(); j++) {
                for(k = 0; k < this.nodecount; k++) {
                    if(layers.get(i).get(j).equals((Integer) k)) {
                        VNode currentNode = this.nodes[k];
                        currentNode.setX(posX);
                        currentNode.setY(posY);
                        if(this.debug) System.out.println(currentNode.getId() 
                                + " | X: " + posX + " Y: " + posY);
                        posY += (currentNode.getHeight() * this.scaling);
                        break;
                    }
                }
            }
            posX += distX;
        }        
    }
    
    /**
     * Returns index of the given node in the nodearray.
     * @param pnode VNode
     * @return Integer
    */
    private Integer getNodeID(VNode pnode) {
        int i;
        for(i = 0; i < this.nodecount; i++) {
            if(this.nodes[i].equals(pnode))
                return i;
        }
        return -1;
    }
}

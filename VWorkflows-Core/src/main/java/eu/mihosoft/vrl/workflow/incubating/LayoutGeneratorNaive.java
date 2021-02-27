/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
package eu.mihosoft.vrl.workflow.incubating;


import edu.uci.ics.jung.graph.util.Pair;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
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
    private VFlowModel workflow;
    private LinkedList<Pair<Integer>> connectionList;
    private boolean recursive;
    private boolean autoscaleNodes;
    private int graphmode;
    private boolean launchRemoveCycles;
    private boolean launchCreateLayering;
    private boolean launchCalculateHorizontalPositions;
    private boolean launchCalculateVerticalPositions;
    private double scaling;
    private double subflowscale;
    private boolean debug;
    
    // internal fields:
    private VNode[] nodes;
    private int nodecount;
    private int conncount;
    private boolean cycle;
    private int[] layering;
    private int layercount;
    
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
        this.graphmode = 0;
        this.launchRemoveCycles = true;
        this.launchCreateLayering = true;
        this.launchCalculateVerticalPositions = true;
        this.launchCalculateHorizontalPositions = true;
        this.scaling = -1.5;
        this.subflowscale = 2.0;
    }
    
    // <editor-fold defaultstate="collapsed" desc="getter">
    /**
     * Returns the workflow to be laid out.
     * @return VFlowModel
     */
    @Override
    public VFlowModel getWorkflow() {
        return this.workflow;
    }
    
    /**
     * Returns a list of the nodes to be laid out.
     * default: the nodelist is gathered from the given workflow.
     * @return {@code Collection<VNode>}
     */
    public Collection<VNode> getNodelist() {
        Collection<VNode> nodelist = new ArrayList<>();
        int i;
        for(i = 0; i < this.nodecount; i++) {
            nodelist.add(this.nodes[i]);
        }
        return nodelist;
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
     * Returns the set input type.
     * 0 - VFlowModel (setWorkflow)
     * 2 - nodelist (setNodelist)
     * The input must be delivered via the corresponding setter method before 
     * the call of generateLayout().
     * default: 0
     * @return int
     */
    public int getGraphmode() {
        return this.graphmode;
    }
    
    /**
     * If set to true, a depth-first-search is performed and all back edges are 
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
     * If set to true, a new layering for the given graph is created.
     * default: true
     * @return boolean
     */
    public boolean getLaunchCreateLayering() {
        return this.launchCreateLayering;
    }
    
    /**
     * If set to true, the vertical position for each node is calculated and 
     * changed.
     * Does not change the order of nodes on each layer.
     * default: true
     * @return boolean
     */
    public boolean getLaunchCalculateVerticalPositions() {
        return this.launchCalculateVerticalPositions;
    }
    
    /**
     * If set to true, the horizontal position for each layer is calculated and 
     * changed.
     * default: true
     * @return boolean
     */
    public boolean getLaunchCalculateHorizontalPositions() {
        return this.launchCalculateHorizontalPositions;
    }
    
    /**
     * Returns the scaling parameter of the algorithm. 
     * Determines the distance between nodes.
     * default: -1.5
     * @return double
     */
    public double getScaling() {
        return this.scaling;
    }
    
    /**
     * Returns the scaling factor that is used to scale subflow-nodes in the 
     * autoscaleNodes procedure.
     * default: 2.0
     * @return double
     */
    public double getSubflowscale() {
        return this.subflowscale;
    }
    
    /**
     * If set to true, debugging output will be printed in the command line.
     * default: false
     * @return boolean
     */
    @Override
    public boolean getDebug() {
        return this.debug;
    }
    
    /**
     * Returns the Graph modeled after the workflow.
     * @return {@code LinkedList<Pair<Integer>>}
     */
    public LinkedList<Pair<Integer>> getModelGraph() {
        return this.connectionList;
    }
    
    /**
     * Returns the layering of the current graph as an array of layer indices.
     * @return int[]
     */
    public int[] getLayering() {
        return this.layering;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="setter">
    /**
     * Sets the workflow to be laid out.
     * @param pworkflow VFlowModel
     */
    @Override
    public void setWorkflow(VFlowModel pworkflow) {
        this.workflow = pworkflow;
    }
    
    /**
     * Sets the list of nodes to be laid out.
     * default: the nodelist is gathered from the given workflow.
     * @param pnodelist {@code Collection<VNode>}
     */
    public void setNodelist(Collection<VNode> pnodelist) {
        this.nodes = new VNode[pnodelist.size()];
        int i = 0;
        Iterator<VNode> it = pnodelist.iterator();
        while(it.hasNext()) {
            this.nodes[i] = it.next();
            i++;
        }
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
     * Sets the input type.
     * 0 - VFlowModel (setWorkflow)
     * 2 - nodelist (setNodelist)
     * The input must be delivered via the corresponding setter method before 
     * the call of generateLayout().
     * default: 0
     * @param pgraphmode int
     */
    public void setGraphmode(int pgraphmode) {
        this.graphmode = pgraphmode;
    }
    
    /**
     * If set to true, a depth-first-search is performed and all back edges are 
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
     * If set to true, a new layering for the given graph is created.
     * default: true
     * @param plaunchCreateLayering boolean
     */
    public void setLaunchCreateLayering(boolean plaunchCreateLayering) {
        this.launchCreateLayering = plaunchCreateLayering;
    }
    
    /**
     * If set to true, the vertical position for each node is calculated and 
     * changed.
     * Does not change the order of nodes on each layer.
     * default: true
     * @param plaunchCalculateVerticalPositions boolean
     */
    public void setLaunchCalculateVerticalPositions(
            boolean plaunchCalculateVerticalPositions) {
        this.launchCalculateVerticalPositions = 
                plaunchCalculateVerticalPositions;
    }
    
    /**
     * If set to true, the horizontal position for each layer is calculated and 
     * changed.
     * default: true
     * @param plaunchCalculateHorizontalPositions boolean
     */
    public void setLaunchCalculateHorizontalPositions(
            boolean plaunchCalculateHorizontalPositions) {
        this.launchCalculateHorizontalPositions = 
                plaunchCalculateHorizontalPositions;
    }
    
    /**
     * Sets the scaling parameter of the algorithm. 
     * Determines the distance between nodes.
     * default: -1.5
     * @param pscaling double
     */
    public void setScaling(double pscaling) {
        this.scaling = pscaling;
    }
    
    /**
     * Sets the scaling factor that is used to scale subflow-nodes in the 
     * autoscaleNodes procedure.
     * default: 2.0
     * @param psubflowscale double
     */
    public void setSubflowscale(double psubflowscale) {
        this.subflowscale = psubflowscale;
    }
    
    /**
     * If set to true, debugging output will be printed in the command line.
     * default: false
     * @param pdebug boolean
     */
    @Override
    public void setDebug(boolean pdebug) {
        this.debug = pdebug;
    }
    
    /**
     * Sets the model graph.
     * @param pconnectionList {@code LinkedList<Tuple<Integer, Integer>>}
     */
    public void setModelGraph(LinkedList<Pair<Integer>> pconnectionList) {
        this.connectionList = pconnectionList;
    }
    
    /**
     * Sets the layering of the current graph as an array of layer indices.
     * @param playering int[]
     */
    public void setLayering(int[] playering) {
        this.layering = playering;
    }
    // </editor-fold>
    
    /**
     * Sets up the model-fields. This includes nodearray, nodecount and the 
     * model graph.
     * Uses either a VFlowModel or a nodelist depending on the graphmode
     * parameter.
     * @return boolean
     */
    public boolean setUp() {
        int i;
        switch(this.graphmode) {
            // VFlowModel:
            case 0:
                if(this.workflow == null) return false;
                // gather nodelist from workflow
                ObservableList<VNode> nodesTemp = this.workflow.getNodes(); 
                if(nodesTemp == null) return false;
                this.nodecount = nodesTemp.size();
                this.nodes = new VNode[this.nodecount];
                // copy nodelist into nodearray for better performance in the future
                for(i = 0; i < nodesTemp.size(); i++) {
                    this.nodes[i] = nodesTemp.get(i);
                }
                break;
            // nodelist:
            case 2:
                if(this.debug) System.out.println("laying out with nodelist");
                if(this.nodes == null) return false;
                this.nodecount = this.nodes.length;
                if(this.nodecount == 0) return false;
                this.workflow = this.nodes[0].getFlow();
                break;
            // default:
            default:
                this.graphmode = 0;
                return setUp();
        }
        // get all edges
        this.connectionList = new LinkedList<>();
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
                Connection currConn = connections.get(i);
                // create a tuple of the sender and receiver of the current
                // connection and add it to the connectionlist
                Integer out = getNodeID(currConn.getSender().getNode());
                Integer in = getNodeID(currConn.getReceiver().getNode());
                if((out != -1) && (in != -1)) {
                    this.connectionList.add(new Pair<>(out, in));
                    this.conncount++;
                }
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
                            if(Objects.equals(this.connectionList.get(j)
                                    .getFirst(), currNode)) {
                                succs.add(this.connectionList.get(j)
                                        .getSecond());
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
    private void remCycR(Integer curr, LinkedList<Integer> path, 
            boolean[] checked) {
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
                        Iterator<Pair<Integer>> its = this.connectionList
                                .iterator();
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
     * Applies all steps of the layout, whose launch-parameters are set to true.
     */
    @Override
    public void generateLayout() {
        if(this.debug) System.out.println("Generating layout.");
        // setup and check for errors
        if(this.setUp()) {
            // get origin point
            double minx = Double.POSITIVE_INFINITY;
            double miny = Double.POSITIVE_INFINITY;
            int i;
            for(i = 0; i < this.nodecount; i++) {
                if(this.nodes[i].getX() < minx) minx = this.nodes[i].getX();
                if(this.nodes[i].getY() < miny) miny = this.nodes[i].getY();
            }
            for(i = 0; i < this.nodecount; i++) {
                this.nodes[i].setX(this.nodes[i].getX() - minx);
                this.nodes[i].setY(this.nodes[i].getY() - miny);
            }
            
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
            if(this.launchCreateLayering) createLayering();
            if(this.launchCalculateVerticalPositions) 
                calculateVerticalPositions();
            if(this.launchCalculateHorizontalPositions) 
                calculateHorizontalPositions();
            // displace by origin point
            for(i = 0; i < this.nodecount; i++) {
                this.nodes[i].setX(this.nodes[i].getX() + minx);
                this.nodes[i].setY(this.nodes[i].getY() + miny);
            }
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
        int i;
        for(i = 0; i < this.nodecount; i++) {
            if(this.nodes[i] instanceof VFlowModel) {
                subgen.setWorkflow((VFlowModel) this.nodes[i]);
                subgen.generateLayout();
            }
        }
    }
    
    /**
     * Scales subflow-nodes according to their contents.
     */
    private void autoscaleNodes() {
        int i;
        for(i = 0; i < this.nodecount; i++) {
            VFlowModel subflow;
            if(!(this.nodes[i] instanceof VFlowModel)) continue;
            subflow = (VFlowModel) this.nodes[i];
            // get node representation of the current subflow
            VNode flownode = this.nodes[i];
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
     * Creates a new layering, assigning each node the lowest layer possible, 
     * while still having all of its preceeding nodes on a lower layer.
     */
    private void createLayering() {
        int i;
        // init layering
        this.layering = new int[this.nodecount];
        int lockable = 0;
        boolean[] locked = new boolean[this.nodecount];
        for(i = 0; i < this.nodecount; i++) {
            this.layering[i] = 0;
            locked[i] = false;
        }
        while(!allLocked(locked)) {
            Iterator<Pair<Integer>> it = this.connectionList.iterator();
            // iterate over all connections
            while(it.hasNext()) {
                Pair<Integer> curr = it.next();
                // set all receivers of the current connection to the layer of 
                // their receiver + 1
                if((!locked[curr.getSecond()]) 
                        && (this.layering[curr.getSecond()] 
                        < (this.layering[curr.getFirst()] + 1))) {
                    this.layering[curr.getSecond()] = 
                            this.layering[curr.getFirst()] + 1;
                }
            }
            for(i = 0; i < this.nodecount; i++) {
                // layers that have been finalized
                if(this.layering[i] == lockable) {
                    locked[i] = true;
                    if(this.debug) System.out.println(this.nodes[i].getId() 
                            + " locked on layer " + lockable);
                }
            }
            // each iteration, the next layer can be locked
            lockable++;
        }
        this.layercount = lockable;
    }
    
    /**
     * Returns true if all nodes are on a locked layer.
     * @param locked boolean[]
     * @return boolean
     */
    private boolean allLocked(boolean[] locked) {
        boolean result = true;
        int i;
        for(i = 0; i < locked.length; i++) {
            if(!locked[i]) result = false;
        }
        return result;
    }
    
    /**
     * Calculates the vertical positions of the nodes in each layer depending on 
     * their height and the scaling parameter.
     * Does not sort nodes to reduce edge crossings.
     */
    private void calculateVerticalPositions() {
        int i;
        int currlayer;
        int[] layer;
        if(this.layering == null) return;
        if(this.layercount == 0) return;
        for(currlayer = 0; currlayer < this.layercount; currlayer++) {
            // create array of the layer
            int nodeson = 0;
            // find number of nodes on current layer
            for(i = 0; i < this.nodecount; i++) {
                if(this.layering[i] == currlayer) nodeson++;
            }
            int layeri = 0;
            layer = new int[nodeson];
            // find nodes on current layer
            for(i = 0; i < this.nodecount; i++) {
                if(this.layering[i] == currlayer) {
                    layer[layeri] = i;
                    layeri++;
                }
            }
            // iterate over all nodes on current layer
            double posy = 0.;
            for(i = 0; i < nodeson; i++) {
                if(this.debug) System.out.println("Vertical position of " 
                        + this.nodes[layer[i]].getId() + " on layer " 
                        + currlayer + " set to " + posy);
                this.nodes[layer[i]].setY(posy);
                // increase position for next node by height of current node 
                // and scaling
                if(this.scaling < 0) {
                    posy = posy + this.nodes[layer[i]].getHeight() 
                            * this.scaling * (-1);
                }
                else {
                    posy = posy + this.nodes[layer[i]].getHeight() 
                            + this.scaling;
                }
            }
        }
    }
    
    /**
     * Calculates horizontal positions of the layers depending on node width.
     */
    private void calculateHorizontalPositions() {
        int i;
        int currlayer;
        int[] layer;
        double posx = 0.;
        if(this.layering == null) return;
        if(this.layercount == 0) return;
        for(currlayer = 0; currlayer < this.layercount; currlayer++) {
            // create array of layer
            int nodeson = 0;
            // find number of nodes on current layer
            for(i = 0; i < this.nodecount; i++) {
                if(this.layering[i] == currlayer) nodeson++;
            }
            int layeri = 0;
            layer = new int[nodeson];
            // find nodes on current layer
            for(i = 0; i < this.nodecount; i++) {
                if(this.layering[i] == currlayer) {
                    layer[layeri] = i;
                    layeri++;
                }
            }
            // iterate over all nodes on current layer
            double maxwidth = Double.NEGATIVE_INFINITY;
            for(i = 0; i < nodeson; i++) {
                if(this.debug) System.out.println("Horizontal position of " 
                        + this.nodes[layer[i]].getId() + " on layer " 
                        + currlayer + " set to " + posx);
                this.nodes[layer[i]].setX(posx);
                // find max width on current layer
                if(this.nodes[layer[i]].getWidth() > maxwidth) {
                    maxwidth = this.nodes[layer[i]].getWidth();
                }
            }
            // increase position for next layer by max width and scaling
            if(this.scaling < 0) {
                posx = posx + maxwidth * this.scaling * (-1);
            }
            else {
                posx = posx + maxwidth + this.scaling;
            }
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

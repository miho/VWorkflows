/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javafx.collections.ObservableList;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;

/** 
 * @author Tobias Mertz
 * Layout generator class using the Jung Graph Drawing Library.
 * steps:
 * 1 - place nodes according to the Kamada & Kawai layout implemented in the Jung library
 * 2 - rotate the graph so the average edge direction is now parallel to the x axis
 * 3 - place all origin nodes (nodes without predecessors) at the left border of the graph
 * 4 - push all nodes to the right, so no children nodes are left of their parents
 * 5 - push all nodes away from each other until no overlaps are left.
 * 
 * ideas:
 * - separate disjuct graphs.
 * - replace getDesiredNodeDist with calculated distance instead of diagonal
 * - separate priorities.
 * - replace KK layout with Fruchterman Reingold Layout or combine the two. (KK layout only finds local minima, FR can circumvent it with its temperature)
 * 
 * - stepOrigin unnecessary once disjunct graphs are implemented.
 * - apply layout to subflows. Bad idea because of poor performance and memory usage. rather apply layout to selected subflows from FXMLController.
 */
public class LayoutGeneratorSmart implements LayoutGenerator {
    
    private boolean debug;
    private VFlow workflow;
    private String[] priority;
    private VNode[] nodes;
    private DirectedGraph<VNode, Connection> jgraph;
    private KKLayout<VNode, Connection> layout;
    private VisualizationViewer<VNode, Connection> vis;
    private int nodecount;
    private int conncount;
    private Point2D graphcenter;
    
    private Pair<Integer>[] origin;
    
    private final double scaling = 1.1;
    
    /**
     * Default contructor.
     * Debugging is disabled and default layout is CircleLayout.
     */
    public LayoutGeneratorSmart() {
        this.debug = false;
        this.priority = new String[3];
        this.priority[0] = "";
        this.priority[1] = "";
        this.priority[2] = "";
        this.jgraph = new DirectedSparseGraph<>();
        this.layout = new KKLayout<>(this.jgraph);
        this.layout.setSize(new Dimension(1000, 1000));
        this.vis = new VisualizationViewer<>(this.layout);
    }
    
    /**
     * Constructor with workflow parameter.
     * @param pworkflow VFlow: workflow to be setup.
     */
    public LayoutGeneratorSmart(VFlow pworkflow) {
        this.workflow = pworkflow;
        this.debug = false;
        this.priority = new String[3];
        this.priority[0] = "";
        this.priority[1] = "";
        this.priority[2] = "";
        this.jgraph = new DirectedSparseGraph<>();
        this.layout = new KKLayout<>(this.jgraph);
        this.layout.setSize(new Dimension(1000, 1000));
        this.vis = new VisualizationViewer<>(this.layout);
    }
    
    /**
     * Constructor with debug-functionality.
     * @param pdebug Boolean: debugging output enable/disable.
     */
    public LayoutGeneratorSmart(boolean pdebug) {
        this.debug = pdebug;
        this.priority = new String[3];
        this.priority[0] = "";
        this.priority[1] = "";
        this.priority[2] = "";
        this.jgraph = new DirectedSparseGraph<>();
        this.layout = new KKLayout<>(this.jgraph);
        this.layout.setSize(new Dimension(1000, 1000));
        this.vis = new VisualizationViewer<>(this.layout);
        if(this.debug) System.out.println("Creating Layout Generator.");
    }
    
    /**
     * Constructor with debug-functionality and workflow parameter.
     * @param pworkflow VFlow: workflow to be setup.
     * @param pdebug Boolean: debugging output enable/disable.
     */
    public LayoutGeneratorSmart(VFlow pworkflow, boolean pdebug) {
        this.workflow = pworkflow;
        this.debug = pdebug;
        this.priority = new String[3];
        this.priority[0] = "";
        this.priority[1] = "";
        this.priority[2] = "";
        this.jgraph = new DirectedSparseGraph<>();
        this.layout = new KKLayout<>(this.jgraph);
        this.layout.setSize(new Dimension(1000, 1000));
        this.vis = new VisualizationViewer<>(this.layout);
        if(this.debug) System.out.println("Creating Layout Generator.");
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
     * Get the workflow to layout.
     * @return VFlow workflow.
     */
    @Override
    public VFlow getWorkflow() {
        return this.workflow;
    }
    
    /**
     * Get an array of model nodes.
     * @return VNode[] nodes.
     */
    //@Override
    public VNode[] getModelNodes() {
        return this.nodes;
    }
    
    /**
     * Get the Graph modeled after the workflow.
     * @return DirectedGraph model graph.
     */
    public DirectedGraph getModelGraph() {
        return this.jgraph;
    }
    
    /**
     * Get the priority order.
     * @return String[] priority.
     */
    public String[] getPriority() {
        return this.priority;
    }
    
    /**
     * Get the layout object applied to the model.
     * @return Layout<VNode, Connection>.
     */
    public Layout<VNode, Connection> getLayout() {
        return this.layout;
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
     * Set the workflow to be layouted.
     * @param pworkflow VFlow.
     */
    @Override
    public void setWorkflow(VFlow pworkflow) {
        this.workflow = pworkflow;
    }
    
    /**
     * Set the array of model nodes.
     * @param pnodes VNode[]
     */
    //@Override
    public void setModelNodes(VNode[] pnodes) {
        this.nodes = pnodes;
    }
    
    /**
     * Set the model graph to be layouted.
     * @param pjgraph DirectedGraph
     */
    public void setModelGraph(DirectedGraph pjgraph) {
        this.jgraph = pjgraph;
    }
    
    /**
     * Set the priority order.
     * @param pr0 String: priority 1
     * @param pr1 String: priority 2
     * @param pr2 String: priority 3
     */
    public void setPriority(String pr0, String pr1, String pr2) {
        this.priority = new String[3];
        this.priority[0] = pr0;
        this.priority[1] = pr1;
        this.priority[2] = pr2;
    }
    
    /**
     * Set the layout object to be applied to the graph.
     * @param playout Layout<VNode, Connection>
     */
    public void setLayout(KKLayout<VNode, Connection> playout) {
        this.layout = playout;
        this.vis = new VisualizationViewer<>(this.layout);
    }
    
    /**
     * creates the model graph from the workflow given at creation.
     */
    private void allNodesSetUp() {
        ObservableList<VNode> nodesTemp = this.workflow.getNodes();
        this.nodecount = nodesTemp.size();
        this.nodes = new VNode[this.nodecount];
        
        int i;
        for(i = 0; i < this.nodecount; i++) {
            this.nodes[i] = nodesTemp.get(i);
            this.jgraph.addVertex(this.nodes[i]);
        }
        
        // Setting up edges
        Connections controlConnections = this.workflow.getConnections("control");
        Connections dataConnections = this.workflow.getConnections("data");
        Connections eventConnections = this.workflow.getConnections("event");
        
        //For seperation of connectiontypes change from here
        ObservableList<Connection> allConnections = controlConnections.getConnections();
        allConnections.addAll(dataConnections.getConnections());
        allConnections.addAll(eventConnections.getConnections());
        this.conncount = allConnections.size();
        
        for(i = 0; i < this.conncount; i++) {
            Connection currConn = allConnections.get(i);
            VNode sender = this.nodes[getNodeID(currConn.getSender().getNode())];
            VNode receiver = this.nodes[getNodeID(currConn.getReceiver().getNode())];
            this.jgraph.addEdge(currConn, sender, receiver);
        }
        
        // get origin nodes
        this.origin = getOrigin();
        // sort origin nodes by successor-count
        this.origin = quickSortDesc(this.origin);
        this.origin = triangularOrigin(this.origin);
    }
    
        /**
     * Gets all origin nodes of the graph created at SetUp.
     * origin nodes are all nodes with an in-degree of 0.
     * @return Pair<Integer>[]: Array of Pairs. Each Pair contains the model-ID 
     * of the node and its successor count.
     */
    private Pair<Integer>[] getOrigin() {
        int i;
        LinkedList<Integer> originL = new LinkedList<>();
        for(i = 0; i < this.nodecount; i++) {
            if(this.jgraph.inDegree(this.nodes[i]) == 0) originL.add(i);
        }
        // get successorcount of origin nodes
        int length = originL.size();
        Pair<Integer>[] origina = new Pair[length];
        for(i = 0; i < length; i++) {
            int curr = originL.removeFirst();
            origina[i] = new Pair<>(curr, this.jgraph.getSuccessorCount(this.nodes[curr]));
            if(this.debug) System.out.println(this.nodes[curr].getId() + " | In-Degree: " + this.jgraph.inDegree(this.nodes[curr]) + " Successors: " + this.jgraph.getSuccessorCount(this.nodes[curr]));
        }
        return origina;
    }
    
    /**
     * Sorts an Array of origin nodes in descending order by their successor 
     * count via Quicksort.
     * @param origin Pair<Integer>[]: Array to be sorted.
     * @return Pair<Integer>[]: Sorted Array.
     */
    private Pair<Integer>[] quickSortDesc(Pair<Integer>[] porigin) {
        // cancellation
        if(porigin.length <= 1) {
            return porigin;
        }
        
        // setup
        int l = 1;
        int r = porigin.length - 1;
        Pair<Integer> pivot = porigin[0];
        Pair<Integer> temp;
        
        // sort
        while(l < r) {
            if((porigin[l].getSecond() <= pivot.getSecond()) && (porigin[r].getSecond() > pivot.getSecond())) {
                temp = porigin[r];
                porigin[r] = porigin[l];
                porigin[l] = temp;
            }
            else {
                if(porigin[l].getSecond() > pivot.getSecond()) l++;
                if(porigin[r].getSecond() <= pivot.getSecond()) r--;
            }
        }
        
        // split array into left and right arrays
        Pair<Integer>[] left;
        Pair<Integer>[] right;
        int leftcount = 0;
        int i;
        for(i = 1; i < porigin.length; i++) {
            if(porigin[i].getSecond() > pivot.getSecond()) leftcount++;
        }
        left = new Pair[leftcount];
        right = new Pair[porigin.length - leftcount - 1];
        for(i = 1; i < porigin.length; i++) {
            if(porigin[i].getSecond() > pivot.getSecond()) left[i-1] = porigin[i];
            else right[i-leftcount-1] = porigin[i];
        }
        
        // sort left and right arrays
        left = quickSortDesc(left);
        right = quickSortDesc(right);
        
        // fuse left and right arrays back together
        for(i = 0; i < left.length; i++) {
            porigin[i] = left[i];
        }
        porigin[i] = pivot;
        i++;
        while(i < porigin.length) {
            porigin[i] = right[i - leftcount-1];
            i++;
        }
        return porigin;
    }
    
    /**
     * Sorts a descending Array into a triangular shape of ascending size up to 
     * the maximum value at the center position, followed by descending values.
     * @param origin Pair<Integer>[]: Array of origin nodes sorted in descending 
     *  order by their successor count.
     * @return Pair<Integer>[]: Sorted Array.
     */
    private Pair<Integer>[] triangularOrigin(Pair<Integer>[] porigin) {
        Pair<Integer>[] originT = new Pair[porigin.length];
        int i;
        for(i = 0; i < porigin.length; i++) {
            originT[(porigin.length / 2) + (((i / 2) + (i % 2)) * powOne(i))] = porigin[i];
        }
        return originT;
    }
    
    /**
     * Returns -1 to the power of x.
     * @param x int: exponent
     * @return int
     */
    private int powOne(int x) {
        if((x % 2) == 0) return 1;
        else return -1;
    }
    
    /**
     * launches only the Kamada & Kawai layout operation.
     */
    public void launchKK() {
        allNodesSetUp();
        stepLayoutApply();
        if(this.debug) testvis("Original Layout");
    }
    
    /**
     * Launches the Kamada & Kawai layout, followed by a rotation of the resulting graph.
     */
    public void launchRotate() {
        allNodesSetUp();
        stepLayoutApply();
        stepRotate();
        if(this.debug) testvis("After Rotate");
    }
    
    /**
     * positions all nodes according to the Kamada & Kawai layout, rotates the resulting graph
     * and moves all nodes without predecessors to the left.
     */
    public void launchOrigin() {
        allNodesSetUp();
        stepLayoutApply();
        stepRotate();
        stepOrigin();
        if(this.debug) testvis("After Origin");
    }
    
    /**
     * positions all nodes according to the Kamada & Kawai layout, rotates the resulting graph,
     * moves all nodes without predecessors to the left and pushes all successors past their
     * predecessors.
     */
    public void launchPushBack() {
        allNodesSetUp();
        stepLayoutApply();
        stepRotate();
        stepOrigin();
        stepPushBack();
        if(this.debug) testvis("After PushBack");
    }
    
    
    /**
     * launches all steps of the algorithm in order.
     * - Kamada & Kawai layout
     * - rotation of the graph
     * - moving of all root-nodes to the left
     * - pushing all children-nodes past their parents
     * - pushing nodes away from each other to remove overlaps
     */
    public void launchForcePushOld() {
        allNodesSetUp();
        stepLayoutApply();
        stepRotate();
        stepOrigin();
        stepPushBack();
        forcePush();
        if(this.debug) testvis("After ForcePush");
    }
    
    /**
     * launches all steps of the algorithm in order.
     * - Kamada & Kawai layout
     * - rotation of the graph
     * - moving of all root-nodes to the left
     * - pushing all children-nodes past their parents
     * - pushing nodes away from each other to remove overlaps
     */
    public void launchForcePushLazy() {
        allNodesSetUp();
        stepLayoutApply();
        stepRotate();
        stepOrigin();
        stepPushBack();
        forcePushLazy();
        if(this.debug) testvis("After ForcePush");
    }
    
    /**
     * launches all steps of the algorithm in order.
     * - Kamada & Kawai layout
     * - rotation of the graph
     * - moving of all root-nodes to the left
     * - pushing all children-nodes past their parents
     * - pushing nodes away from each other to remove overlaps
     */
    public void launchLazyNoOrigin() {
        allNodesSetUp();
        stepLayoutApply();
        stepRotate();
        stepPushBack();
        forcePushLazy();
        if(this.debug) testvis("After ForcePush");
    }
    
    /**
     * Generates a Layout for the workflow given.
     */
    @Override
    public void generateLayout() {
        if(this.debug) System.out.println("Generating layout.");
        launchForcePushLazy();
    }
    
    /**
     * Runs all steps of the layouting algorithm in order.
     */
    private void allSteps() {
        allNodesSetUp();
        stepLayoutApply();
        stepRotate();
        stepOrigin();
        stepPushBack();
        forcePush();
    }
    
    /**
     * applies the Kamada & Kawai Layout implemented in the Jung-Graph-Drawing-Library
     */
    private void stepLayoutApply() {
        if(this.debug) System.out.println("--- applying layout.");

        // find longest path
        int maxpath = (int) Math.round(findMaxPathWidth() * this.scaling);
        int y = (maxpath / 16) * 9;
        
        this.vis = new VisualizationViewer<>(this.layout);
        vis.setPreferredSize(new Dimension(maxpath, y));
        layout.setSize(new Dimension(maxpath, y));
        
        // set layout coordinates
        int i;
        for(i = 0; i < this.nodecount; i++) {
            Point2D coords;
            coords = this.layout.transform(this.nodes[i]);
            this.nodes[i].setX(coords.getX());
            this.nodes[i].setY(coords.getY());
            if(this.debug) System.out.println(this.nodes[i].getId() + " | X: " + coords.getX() + " Y: " + coords.getY());
        }
    }
    
    /**
     * Finds the cumulative width of all nodes on the longest path in the graph.
     * @return double: sum of width of all nodes on the longest path multiplied 
     * by constant scaling factor
     */
    private double findMaxPathWidth() {
        int[] maxPathFollowing = new int[this.nodecount];
        int maxPath = 0;
        int maxPathIndex = 0;
        double maxPathWidth = 0.;
        LinkedList<Integer> fifo = new LinkedList<>();
        Collection<VNode> nodelist;
        Iterator<VNode> it;
        int i;
        for(i = 0; i < this.nodecount; i++) {
            if(this.jgraph.getSuccessorCount(this.nodes[i]) == 0) {
                maxPathFollowing[i] = 0;
                fifo.add(i);
            }
        }
        while(!fifo.isEmpty()) {
            Integer currNode = fifo.removeFirst();
            if(this.jgraph.getSuccessorCount(this.nodes[currNode]) == 0) {
                maxPathFollowing[currNode] = 0;
            }
            else {
                nodelist = this.jgraph.getSuccessors(this.nodes[currNode]);
                it = nodelist.iterator();
                while(it.hasNext()) {
                    Integer currSucc = getNodeID(it.next());
                    int tempFollowing = 1 + maxPathFollowing[currSucc];
                    if(tempFollowing > maxPathFollowing[currNode]) {
                        maxPathFollowing[currNode] = tempFollowing;
                    }
                }
                nodelist = this.jgraph.getPredecessors(this.nodes[currNode]);
                it = nodelist.iterator();
                while(it.hasNext()) {
                    fifo.add(getNodeID(it.next()));
                }
            }
        }
        for(i = 0; i < this.nodecount; i++) {
            if(maxPathFollowing[i] > maxPath) {
                maxPath = maxPathFollowing[i];
                maxPathIndex = i;
            }
        }
        maxPathWidth += this.nodes[maxPathIndex].getWidth();
        nodelist = this.jgraph.getSuccessors(this.nodes[maxPathIndex]);
        maxPath -= 1;
        it = nodelist.iterator();
        while(it.hasNext()) {
            VNode currSucc = it.next();
            if(maxPath == maxPathFollowing[getNodeID(currSucc)]) {
                maxPathWidth += this.nodes[getNodeID(currSucc)].getWidth();
                nodelist = this.jgraph.getSuccessors(currSucc);
                it = nodelist.iterator();
            }
        }
        return maxPathWidth;
    }
    
    /**
     * rotates the entire graph around its center point, so its new average edge-
     * direction is parallel to the horizontal axis from left to right.
     */
    private void stepRotate() {
        if(this.debug) System.out.println("--- starting rotation.");
        int i;
        if(this.conncount == 0) return;

        // get center of graph
        double centerx = 0;
        double centery = 0;
        for(i = 0; i < this.nodecount; i++) {
            Point2D currCoords = this.layout.transform(this.nodes[i]);
            centerx += currCoords.getX();
            centery += currCoords.getY();
        }
        centerx /= this.nodecount;
        centery /= this.nodecount;
        this.graphcenter = new Point2D.Double(centerx, centery);
        if(this.debug) System.out.println("center of rotation: (" + centerx + "|" + centery + ")");
        
        // get average direction of edges
        double avgdirx = 0;
        double avgdiry = 0;
        Collection<Connection> conns = this.jgraph.getEdges();
        Iterator<Connection> it = conns.iterator();
        while(it.hasNext()) {
            Connection currConn = it.next();
            Point2D sender = this.layout.transform(currConn.getSender().getNode());
            Point2D receiver = this.layout.transform(currConn.getReceiver().getNode());
            
            double dirx = receiver.getX() - sender.getX();
            double diry = receiver.getY() - sender.getY();
            avgdirx += dirx;
            avgdiry += diry;
            if(this.debug) System.out.println("Edge from " + currConn.getSender().getNode().getId() + " to " + currConn.getReceiver().getNode().getId() + " has direction: (" + dirx + "|" +diry + ") = " + (diry / dirx));
        }
        avgdirx /= this.conncount;
        avgdiry /= this.conncount;
        double avghyp = Math.sqrt(Math.pow(avgdirx, 2.) + Math.pow(avgdiry, 2.));
        if(this.debug) System.out.println("average horizontal vector: " + avgdirx);
        if(this.debug) System.out.println("original average edge direction: " + ((avgdiry / avgdirx) / this.conncount));
        
        // mirror graph at vertical axis through center point
        // if the horizontal component of the average edge direction is negative.
        if(avgdirx < 0) {
            for(i = 0; i < this.nodecount; i++) {
                Point2D currCoords = this.layout.transform(this.nodes[i]);
                double newx;
                newx = ((-1) * (currCoords.getX() - centerx)) + centerx;
                Point2D newCoords = new Point2D.Double(newx, currCoords.getY());
                this.nodes[i].setX(newx);
                this.layout.setLocation(this.nodes[i], newCoords);
            }
            avgdirx *= -1;
            if(this.debug) System.out.println("mirrored graph");
            stepRotate();
            return;
        }
        
        // rotate graph around center so new average direction is 0 ( -> x-direction)
        for(i = 0; i < this.nodecount; i++) {
            Point2D currCoords = this.layout.transform(this.nodes[i]);
            if(this.debug) System.out.println("Rotated Vertex " + this.nodes[i].getId() + " from (" + currCoords.getX() + "|" + currCoords.getY() + ")");
            double x = currCoords.getX() - centerx;
            double y = currCoords.getY() - centery;
            double newx = (x * avgdirx / avghyp) + (y * avgdiry / avghyp);
            newx += centerx;
            double newy = (y * avgdirx / avghyp) - (x * avgdiry / avghyp);
            newy += centery;
            Point2D newCoords = new Point2D.Double(newx, newy);
            this.nodes[i].setX(newx);
            this.nodes[i].setY(newy);
            this.layout.setLocation(this.nodes[i], newCoords);
            if(this.debug) System.out.println("to (" + newx + "|" + newy + ")");
        }
        if(this.debug) System.out.println("new average edge direction: " + getAvgDir());
    }
    
    /**
     * returns the average direction of all edges in the graph.
     * @return average direction as an angle to the horizontal axis.
     */
    private double getAvgDir() {
        double avgdirx = 0;
        double avgdiry = 0;
        Collection<Connection> conns = this.jgraph.getEdges();
        Iterator<Connection> it = conns.iterator();
        while(it.hasNext()) {
            Connection currConn = it.next();
            Point2D sender = this.layout.transform(currConn.getSender().getNode());
            Point2D receiver = this.layout.transform(currConn.getReceiver().getNode());
            
            double dirx = receiver.getX() - sender.getX();
            double diry = receiver.getY() - sender.getY();
            avgdirx += dirx;
            avgdiry += diry;
            if(this.debug) System.out.println("Edge from " + currConn.getSender().getNode().getId() + " to " + currConn.getReceiver().getNode().getId() + " has direction: (" + dirx + "|" +diry + ") = " + (diry / dirx));
        }
        avgdirx /= this.conncount;
        avgdiry /= this.conncount;
        return Math.atan(avgdiry / avgdirx);
    }
    
    /**
     * places all nodes without predecessors at the leftmost edge of the graph
     */
    private void stepOrigin() {
        if(this.debug) System.out.println("--- starting origin");
        int i;
        
        // get lowest x coordinate in graph
        double minx = this.nodes[0].getX();
        double maxw = 0.;
        double maxh = 0.;
        for(i = 0; i < this.nodecount; i++) {
            if(this.nodes[i].getX() < minx) minx = this.nodes[i].getX();
            if(this.nodes[i].getWidth() > maxw) maxw = this.nodes[i].getWidth();
            if(this.nodes[i].getHeight() > maxh) maxh = this.nodes[i].getHeight();
        }
        
        // place origin nodes on lowest x coordinate
        double lastpos = this.graphcenter.getY() - ((this.origin.length / 2) * maxh * this.scaling);
        for(i = 0; i < this.origin.length; i++) {
            Point2D coords = new Point2D.Double();
            coords.setLocation((minx - (this.scaling * maxw)), lastpos);
            lastpos += this.scaling * (maxh);
            VNode currNode = this.nodes[this.origin[i].getFirst()];
            this.layout.setLocation(currNode, coords);
            currNode.setX(coords.getX());
            currNode.setY(coords.getY());
        }
    }
    
    /**
     * Pushes all nodes to the right, if they were left of one of their predecessors.
     */
    private void stepPushBack() {
        if(this.debug) System.out.println("--- starting push back.");
        LinkedList<Integer> fifo = new LinkedList<>();
        int i;
        // enter all origin nodes into fifo
        for(i = 0; i < this.origin.length; i++) {
            fifo.add(this.origin[i].getFirst());
        }

        while(!fifo.isEmpty()) {
            i = fifo.removeFirst();
            // move node to the right if predecessor has larger or equal x-coordinate
            Collection<VNode> nodelist = this.jgraph.getPredecessors(this.nodes[i]);
            Iterator<VNode> it = nodelist.iterator();
            while(it.hasNext()) {
                VNode pred = it.next();
                double minpos = pred.getX() + (pred.getWidth() * this.scaling);
                if(this.nodes[i].getX() < minpos) {
                    this.nodes[i].setX(minpos);
                    Point2D coords = new Point2D.Double(this.nodes[i].getX(), this.nodes[i].getY());
                    this.layout.setLocation(this.nodes[i], coords);
                }
            }
            // add successors of current node to the fifo
            nodelist = this.jgraph.getSuccessors(this.nodes[i]);
            it = nodelist.iterator();
            while(it.hasNext()) {
                fifo.add(getNodeID(it.next()));
            }
        }
    }
    
    /**
     * Applies force to each node, to push each other away and remove overlaps.
     */
    private void forcePush() {
        if(this.debug) System.out.println("--- starting force push");
        LinkedList<Integer> fifo = new LinkedList<>();
        int i;
        for(i = 0; i < this.origin.length; i++) {
            fifo.add(this.origin[i].getFirst());
        }
        
        while(fifo.size() > 0) {
            i = fifo.removeFirst();
            int j;
            Collection<VNode> succ = this.jgraph.getSuccessors(this.nodes[i]);
            Object[] successors = succ.toArray();
            for(j = 0; j < successors.length; j++) {
                fifo.add(getNodeID((VNode) successors[j]));
            }

            double x1 = this.nodes[i].getX() + (this.nodes[i].getWidth() / 2);
            double y1 = this.nodes[i].getY() + (this.nodes[i].getHeight() / 2);
            for(j = 0; j < this.nodecount; j++) {
                if(j == i) continue;
                double x2 = this.nodes[j].getX() + (this.nodes[j].getWidth() / 2);
                double y2 = this.nodes[j].getY() + (this.nodes[j].getHeight() / 2);
                double vx = x2 - x1;
                double vy = y2 - y1;
                double len = Math.sqrt(Math.pow(vy, 2) + Math.pow(vx, 2));
                double dst = getDesiredNodeDist(this.nodes[i], this.nodes[j]);
                double xf;
                double yf;
                if((len < dst) && ((x1 != x2) || (y1 != y2))) {
                    if(vy >= 0) {
                        yf = y1 + ((dst * vy) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2)));
                    }
                    else {
                        yf = y1 - ((dst * vy) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2)));
                    }
                    xf = ((vx * (yf - y1)) / vy) + x1;
                    Double newx = xf - (this.nodes[j].getWidth() / 2);
                    Double newy = yf - (this.nodes[j].getHeight() / 2);
                    this.nodes[j].setX(newx);
                    this.nodes[j].setY(newy);
                    Point2D coords = new Point2D.Double(newx, newy);
                    this.layout.setLocation(this.nodes[j], coords);
                    if(this.debug) System.out.println(this.nodes[i].getId() + " pushed " + this.nodes[j].getId() + " from (" + x2 + "|" + y2 + ") to (" + xf + "|" + yf + ")");
                }
            }
        }
    }
    
    /**
     * Removes overlaps by scaling coordinates of all nodes with constant factor
     * until all overlaps are gone or maximum number of iterations has been reached.
     */
    private void forcePushLazy() {
        if(this.debug) System.out.println("--- starting force push");
        int maxits = Math.max(1, (100 / this.nodecount));
        int iteration;
        boolean change = true;
        for(iteration = 0; iteration < maxits; iteration ++) {
            if(this.debug) System.out.println("iteration " + (iteration + 1) + " of " + maxits);
            if(!change) break;
            change = false;
            int i;
            for(i = 0; i < (this.nodecount - 1); i++) {
                int j;
                for(j = (i + 1); j < this.nodecount; j++) {
                    double realDist = getRealNodeDist(this.nodes[i], this.nodes[j]);
                    double desDist = getDesiredNodeDist(this.nodes[i], this.nodes[j]);
                    if(this.debug) System.out.println(this.nodes[i].getId() + " and " + this.nodes[j].getId() + " have real distance of: " + realDist + " and desired distance of: " + desDist);
                    if(realDist < desDist) change = true;
                }
            }
            if(change) {
                for(i = 0; i < this.nodecount; i++) {
                    this.nodes[i].setX(this.nodes[i].getX() * this.scaling);
                    this.nodes[i].setY(this.nodes[i].getY() * this.scaling);
                }
            }
        }
    }
    
    /**
     * Returns distance between center points of two nodes.
     * @param node1 VNode
     * @param node2 VNode
     * @return double: distance between node1 and node2
     */
    private double getRealNodeDist(VNode node1, VNode node2) {
        double distx = (node1.getX() + (node1.getWidth() / 2)) - (node2.getX() + (node2.getWidth() / 2));
        double disty = (node1.getY() + (node1.getHeight() / 2)) - (node2.getY() + (node2.getHeight() / 2));
        return Math.sqrt(Math.pow(distx, 2) + Math.pow(disty, 2));
    }
    
    /**
     * Returns the desired distance between center points of two nodes.
     * Currently nodes are simulated as circles with a diameter equal to their 
     * diagonal. Desired distance is the sum of the radii of both 
     * circles multiplied with a constant scaling factor.
     * @param node1 VNode
     * @param node2 VNode
     * @return double: desired distance between node1 and node2
     */
    private double getDesiredNodeDist(VNode node1, VNode node2) {
        double f1;
        double f2;
        f1 = Math.sqrt(Math.pow(node1.getWidth(), 2) + Math.pow(node1.getHeight(), 2));
        f2 = Math.sqrt(Math.pow(node2.getWidth(), 2) + Math.pow(node2.getHeight(), 2));
        return (this.scaling * (f1 + f2) / 2);
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
     * Graph visualization for debugging output.
     */
    private void testvis(String pname) {
        Transformer<VNode, Paint> vertexPaintT = new Transformer<VNode, Paint>() {
            @Override
            public Paint transform(VNode n) {
                return Color.GREEN;
            }
        };
        final Stroke edgeStroke = new BasicStroke();
        Transformer<Connection, Stroke> edgeStrokeT = new Transformer<Connection, Stroke>() {
            @Override
            public Stroke transform(Connection c) {
                return edgeStroke;
            }
        };
        this.vis.getRenderContext().setVertexFillPaintTransformer(vertexPaintT);
        this.vis.getRenderContext().setVertexLabelTransformer(new IDLabeller());
        this.vis.getRenderContext().setEdgeStrokeTransformer(edgeStrokeT);
        this.vis.getRenderContext().setEdgeLabelTransformer(new IDLabeller());
        this.vis.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        this.vis.setGraphMouse(gm);
        JFrame frame = new JFrame(pname);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(this.vis);
        frame.pack();
        frame.setVisible(true);
        System.out.println("--- node positions on show:");
        int i;
        for(i = 0; i < this.nodecount; i++) {
            Point2D coords = this.layout.transform(this.nodes[i]);
            System.out.println(this.nodes[i].getId() + " | X: " + coords.getX() + " Y: " + coords.getY());
        }
    }
    
    /**
     * Labeller that labels VNodes and Connections by their ID.
     * @param <V> VNode or Connection to be transformed.
     */
    class IDLabeller<V extends Object> implements Transformer<V, String> {
        
        @Override
        public String transform(V v) {
            if(v instanceof VNode) {
                VNode node = (VNode) v;
                return node.getId();
            }
            else if(v instanceof Connection) {
                Connection conn = (Connection) v;
                return conn.getId();
            }
            else{
                return v.toString();
            }
        }
    }
    
}

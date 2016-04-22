/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import edu.uci.ics.jung.algorithms.layout.DAGLayout;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import javafx.collections.ObservableList;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;

/**
 * Layout generator class using the Jung Graph Drawing Library. 
 * @author Tobias Mertz
 */
public class LayoutGeneratorSmart implements LayoutGenerator {
    
    private final boolean debug;
    private VFlow workflow;
    private DirectedGraph<VNode, Connection> jgraph;
    private VNode[] nodes;
    private int nodecount;
    private int conncount;
    
    /**
     * Default contructor.
     * Debugging is disabled and default layout is CircleLayout.
     */
    public LayoutGeneratorSmart() {
        this.debug = false;
    }
    
    /**
     * Constructor with debug-functionality.
     * @param pdebug Boolean: debugging output enable/disable.
     */
    public LayoutGeneratorSmart(boolean pdebug) {
        this.debug = pdebug;
        if(this.debug) System.out.println("Creating Layout Generator.");
    }
    
    /**
     * Sets up the node- and edge-model for the current workflow.
     * @param pworkflow VWorfklow: current workflow to be layouted.
     */
    @Override
    public void setUp(VFlow pworkflow) {
        if(this.debug) System.out.println("Setting up workflow for layout generation.");
        this.workflow = pworkflow;
        this.jgraph = new DirectedSparseGraph<>();
        
        // Setting up nodes
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
        if(this.debug) System.out.println("Setup complete with " + this.jgraph.getVertexCount() + " nodes and " + this.jgraph.getEdgeCount() + " edges.");
    }
    
    /**
     * Generates a Layout for the workflow given at SetUp.
     */
    @Override
    public void generateLayout() {
        if(this.debug) System.out.println("Generating layout.");
        int i;
        Layout<VNode, Connection> layout = new KKLayout<>(this.jgraph);
        layout.setSize(new Dimension(1000, 1000));
        
        // get origin nodes
        Pair<Integer>[] origin = getOrigin();
        // sort origin nodes by successor-count
        origin = quickSortDesc(origin);
        origin = triangularOrigin(origin);
        
        // place origin nodes on x=0
        double lastpos = 0.;
        for(i = 0; i < origin.length; i++) {
            Point2D coords = new Point2D.Double();
            coords.setLocation(0., lastpos);
            lastpos += this.nodes[origin[i].getFirst()].getHeight();
            layout.setLocation(this.nodes[origin[i].getFirst()], coords);
        }
        
        if(this.debug) testvis(layout);
        
        // set layout coordinates
        for(i = 0; i < this.nodecount; i++) {
            Point2D coords;
            coords = layout.transform(this.nodes[i]);
            this.nodes[i].setX(coords.getX());
            this.nodes[i].setY(coords.getY());
            if(this.debug) System.out.println(this.nodes[i].getId() + " | X: " + coords.getX() + " Y: " + coords.getY());
        }
        // force push
        forcePush(origin);
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
        Pair<Integer>[] origin = new Pair[length];
        for(i = 0; i < length; i++) {
            int curr = originL.removeFirst();
            origin[i] = new Pair<>(curr, this.jgraph.getSuccessorCount(this.nodes[curr]));
            if(this.debug) System.out.println(this.nodes[curr].getId() + " | In-Degree: " + this.jgraph.inDegree(this.nodes[curr]) + " Successors: " + this.jgraph.getSuccessorCount(this.nodes[curr]));
        }
        return origin;
    }
    
    /**
     * Sorts an Array of origin nodes in descending order by their successor 
     * count via Quicksort.
     * @param origin Pair<Integer>[]: Array to be sorted.
     * @return Pair<Integer>[]: Sorted Array.
     */
    private Pair<Integer>[] quickSortDesc(Pair<Integer>[] origin) {
        // cancellation
        if(origin.length <= 1) {
            return origin;
        }
        
        // setup
        int l = 1;
        int r = origin.length - 1;
        Pair<Integer> pivot = origin[0];
        Pair<Integer> temp;
        
        // sort
        while(l < r) {
            if((origin[l].getSecond() <= pivot.getSecond()) && (origin[r].getSecond() > pivot.getSecond())) {
                temp = origin[r];
                origin[r] = origin[l];
                origin[l] = temp;
            }
            else {
                if(origin[l].getSecond() > pivot.getSecond()) l++;
                if(origin[r].getSecond() <= pivot.getSecond()) r--;
            }
        }
        
        // split array into left and right arrays
        Pair<Integer>[] left;
        Pair<Integer>[] right;
        int leftcount = 0;
        int i;
        for(i = 1; i < origin.length; i++) {
            if(origin[i].getSecond() > pivot.getSecond()) leftcount++;
        }
        left = new Pair[leftcount];
        right = new Pair[origin.length - leftcount - 1];
        for(i = 1; i < origin.length; i++) {
            if(origin[i].getSecond() > pivot.getSecond()) left[i-1] = origin[i];
            else right[i-leftcount-1] = origin[i];
        }
        
        // sort left and right arrays
        left = quickSortDesc(left);
        right = quickSortDesc(right);
        
        // fuse left and right arrays back together
        for(i = 0; i < left.length; i++) {
            origin[i] = left[i];
        }
        origin[i] = pivot;
        i++;
        while(i < origin.length) {
            origin[i] = right[i - leftcount-1];
            i++;
        }
        return origin;
    }
    
    /**
     * Sorts a descending Array into a triangular shape of ascending size up to 
     * the maximum value at the center position, followed by descending values.
     * @param origin Pair<Integer>[]: Array of origin nodes sorted in descending 
     *  order by their successor count.
     * @return Pair<Integer>[]: Sorted Array.
     */
    private Pair<Integer>[] triangularOrigin(Pair<Integer>[] origin) {
        Pair<Integer>[] originT = new Pair[origin.length];
        int i;
        for(i = 0; i < origin.length; i++) {
            originT[(origin.length / 2) + (((i / 2) + (i % 2)) * powOne(i))] = origin[i];
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
     * Applies force to each node, to push each other away and remove overlaps.
     * @param origin Pair<Integer>[]: List of origin nodes.
     */
    private void forcePush(Pair<Integer>[] origin) {
        LinkedList<Integer> fifo = new LinkedList<>();
        int i;
        for(i = 0; i < origin.length; i++) {
            fifo.add(origin[i].getFirst());
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
                double f1;
                double f2;
                if(this.nodes[i].getWidth() > this.nodes[i].getHeight()) f1 = this.nodes[i].getWidth() / 2;
                else f1 = this.nodes[i].getHeight() / 2;
                if(this.nodes[j].getWidth() > this.nodes[j].getHeight()) f2 = this.nodes[j].getWidth() / 2;
                else f2 = this.nodes[j].getHeight() / 2;
                double dst = 1.2 * (f1 + f2);
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
                    this.nodes[j].setX(xf - (this.nodes[j].getWidth() / 2));
                    this.nodes[j].setY(yf - (this.nodes[j].getHeight() / 2));
                    if(this.debug) System.out.println(this.nodes[i].getId() + " pushed " + this.nodes[j].getId() + " from (" + x2 + "|" + y2 + ") to (" + xf + "|" + yf + ")");
                }
            }
        }
    }
    
    /**
     * Graph visualization for debugging output.
     * @param layout Layout<VNode, Connection>: layout to visualize.
     */
    private void testvis(Layout<VNode, Connection> layout) {
        VisualizationViewer<VNode, Connection> vis = new VisualizationViewer<>(layout);
        vis.setPreferredSize(new Dimension(1280, 720));
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
        vis.getRenderContext().setVertexFillPaintTransformer(vertexPaintT);
        vis.getRenderContext().setVertexLabelTransformer(new IDLabeller());
        vis.getRenderContext().setEdgeStrokeTransformer(edgeStrokeT);
        vis.getRenderContext().setEdgeLabelTransformer(new IDLabeller());
        vis.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vis.setGraphMouse(gm);
        JFrame frame = new JFrame("Jung Display");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(vis);
        frame.pack();
        frame.setVisible(true);
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

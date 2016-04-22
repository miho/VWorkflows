/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import javafx.collections.ObservableList;

// layouts
import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;

// testvis
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;

/**
 * Layout generator class using the Jung Graph Drawing Library.
 * @author Tobias Mertz
 */
public class LayoutGeneratorJung implements LayoutGenerator {
    
    private final boolean debug;
    private final int layoutselector;
    private VFlow workflow;
    private DirectedGraph<VNode, Connection> jgraph;
    private VNode[] nodes;
    private int nodecount;
    private int conncount;
    
    /**
     * Default contructor.
     * Debugging is disabled and default layout is CircleLayout.
     */
    public LayoutGeneratorJung() {
        this.debug = false;
        this.layoutselector = 1;
        
    }
    
    /**
     * Constructor with debug-functionality.
     * Default layout is CircleLayout.
     * @param pdebug Boolean: debugging output enable/disable.
     */
    public LayoutGeneratorJung(boolean pdebug) {
        this.debug = pdebug;
        this.layoutselector = 1;
        if(this.debug) System.out.println("Creating layout generator");
    }
    
    /**
     * Constructor with debug- and chosen layout-functionality.
     * @param pdebug Boolean: debugging output enable/disable.
     * @param playout int: layout to be used. (1: CircleLayout, 2: DAGLayout, 
     * 3: FRLayout, 4: FRLayout2, 5: ISOMLayout, 6: KKLayout, 8: SpringLayout, 
     * 9: SpringLayout2, 10: StaticLayout)
     */
    public LayoutGeneratorJung(boolean pdebug, int playout) {
        this.debug = pdebug;
        this.layoutselector = playout;
        if(this.debug) System.out.println("Creating layout generator.");
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
        
        Layout<VNode, Connection> layout;
        switch (this.layoutselector) {
            /* Only works for Forests not generic directed graphs
            case 0:
                layout = new BalloonLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running Circle layout.");
                break;
            */
            case 1:
                layout = new CircleLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running Circle layout.");
                break;
            case 2:
                layout = new DAGLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running DAG layout.");
                break;
            case 3:
                layout = new FRLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running FR layout.");
                break;
            case 4:
                layout = new FRLayout2<>(this.jgraph);
                if(this.debug) System.out.println("Runnung FR2 layout.");
                break;
            case 5:
                layout = new ISOMLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running ISOM layout.");
                break;
            case 6:
                layout = new KKLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running KK layout.");
                break;
            /* Only works for forests not generic directed graphs
            case 7:
                layout = new RadialTreeLayout<VNode, Connection>(this.jgraph);
                if(this.debug) System.out.println("Running Radial Tree layout.");
                break;
            */
            case 8:
                layout = new SpringLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running Spring layout.");
                break;
            case 9:
                layout = new SpringLayout2<>(this.jgraph);
                if(this.debug) System.out.println("Running Spring2 layout.");
                break;
            case 10:
                layout = new StaticLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running Static layout.");
                break;
            /* Only works for forests not generic directed graphs
            case 11:
                layout = new TreeLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running Tree layout.");
                break;
            */
            default:
                layout = new CircleLayout<>(this.jgraph);
                if(this.debug) System.out.println("Running default (Circle) layout.");
                break;
        }
        
        int i;
        double maxheight = 0;
        double maxwidth = 0;
        for(i = 0; i < this.nodecount; i++) {
            double currheight = this.nodes[i].getHeight();
            double currwidth = this.nodes[i].getWidth();
            if(currheight > maxheight) {
                maxheight = currheight;
            }
            if(currwidth > maxwidth) {
                maxwidth = currwidth;
            }
        }
        //layout.setSize(new Dimension((int) Math.round(maxwidth * this.nodecount), (int) Math.round(maxheight * this.nodecount)));
        layout.setSize(new Dimension(1280, 720));
        
        if(this.debug) testvis(layout);
        
        for(i = 0; i < this.nodecount; i++) {
            Point2D coords = layout.transform(this.nodes[i]);
            this.nodes[i].setX(coords.getX());
            this.nodes[i].setY(coords.getY());
            if(this.debug) System.out.println(this.nodes[i].getId() + " | X: " + coords.getX() + " Y: " + coords.getY());
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
        vis.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
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

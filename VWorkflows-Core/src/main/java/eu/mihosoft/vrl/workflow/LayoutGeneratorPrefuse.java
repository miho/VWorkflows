/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Objects;
import javafx.collections.ObservableList;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.tuple.TupleSet;
import prefuse.Display;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.Visualization;

// layouts
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.CollapsedStackLayout;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.BalloonTreeLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.FruchtermanReingoldLayout; // testvis makes a difference for some reason
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
import prefuse.action.layout.GridLayout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.StackedAreaChart;

// testvis
import javax.swing.JFrame;
import prefuse.action.assignment.ColorAction;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

/**
 *
 * @author Tobias Mertz
 */
public class LayoutGeneratorPrefuse implements LayoutGenerator {
    
    private final boolean debug;
    private final int layoutselector;
    private VFlow workflow;
    private GenTuple<VNode, Integer>[] nodes;
    private int nodecount;
    private int conncount;
    private Graph pgraph;
    
    public LayoutGeneratorPrefuse() {
        this.debug = false;
        this.layoutselector = 2;
    }
    
    public LayoutGeneratorPrefuse(boolean pdebug) {
        this.debug = pdebug;
        this.layoutselector = 2;
        if(this.debug) {
            System.out.println("Creating layout generator");
        }
    }
    
    public LayoutGeneratorPrefuse(boolean pdebug, int playout) {
        this.debug = pdebug;
        this.layoutselector = playout;
        if(this.debug) {
            System.out.println("Creating layout generator");
        }
    }
    
    /**
     * Sets up the model for the current workflow.
     * @param pworkflow current workflow to be layouted.
     */
    @Override
    public void setUp(VFlow pworkflow) {
        if(this.debug) {
            System.out.println("Setting up workflow for layout generation.");
        }
        this.workflow = pworkflow;
        this.pgraph = new Graph();
        
        // Setting up nodes
        ObservableList<VNode> nodesTemp = this.workflow.getNodes();
        this.nodecount = nodesTemp.size();
        this.nodes = new GenTuple[this.nodecount];
        
        int i;
        for(i = 0; i < this.nodecount; i++) {
            int temp;
            temp = this.pgraph.addNodeRow();
            this.nodes[i] = new GenTuple<>(nodesTemp.get(i), (Integer) temp);
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
            int sender = getPNode(currConn.getSender().getNode());
            int receiver = getPNode(currConn.getReceiver().getNode());
            pgraph.addEdge(sender, receiver);
        }
        if(this.debug) {
            System.out.println("Setup complete with " + this.pgraph.getNodeCount() + " nodes and " + this.pgraph.getEdgeCount() + " edges.");
        }
    }
    
    /**
     * Generates a Layout for the workflow 
     * as well as the nodes and connections given at SetUp.
     */
    @Override
    public void generateLayout() {
        if(this.debug) {
            System.out.println("Generating layout.");
        }

        ActionList layout = new ActionList();
        switch (this.layoutselector) {
            /* requires additional setup
            case 0:
                layout.add(new AxisLabelLayout("pgraph"));
                if(this.debug) System.out.println("Running Axis Label layout.");
                break;
            */
            /* requires additional setup
            case 1:
                layout.add(new AxisLayout("pgraph"));
                if(this.debug) System.out.println("Running Axis layout");
                break;
            */
            case 2:
                layout.add(new CircleLayout("pgraph"));
                if(this.debug) System.out.println("Running Circle layout.");
                break;
            /* Can not be used on graphs
            case 3:
                layout.add(new CollapsedStackLayout("pgraph"));
                if(this.debug) System.out.println("Running Collapsed Stack layout.");
                break;
            */
            case 4:
                layout.add(new CollapsedSubtreeLayout("pgraph"));
                if(this.debug) System.out.println("Running Collapsed Subtree layout.");
                break;
            case 5: 
                layout.add(new BalloonTreeLayout("pgraph"));
                if(this.debug) System.out.println("Running Balloon Tree layout.");
                break;
            case 6:
                layout.add(new ForceDirectedLayout("pgraph"));
                if(this.debug) System.out.println("Running Force Directed layout.");
                break;
            case 7: 
                layout.add(new FruchtermanReingoldLayout("pgraph"));
                if(this.debug) System.out.println("Running Fruchterman Reingold layout.");
                break;
            case 8:
                layout.add(new NodeLinkTreeLayout("pgraph"));
                if(this.debug) System.out.println("Running Node Link Tree layout.");
                break;
            case 9: 
                layout.add(new RadialTreeLayout("pgraph"));
                if(this.debug) System.out.println("Running Radial Tree layout.");
                break;
            case 10:
                layout.add(new SquarifiedTreeMapLayout("pgraph"));
                if(this.debug) System.out.println("Running Squarified Tree Map layout");
                break;
            /* Can not be used on graphs
            case 11:
                layout.add(new GridLayout("pgraph"));
                if(this.debug) System.out.println("Running Grid layout.");
                break;
            */
            case 12: 
                layout.add(new RandomLayout("pgraph"));
                if(this.debug) System.out.println("Running Random layout.");
                break;
            /* requires additional setup
            case 13:
                layout.add(new StackedAreaChart("pgraph"));
                if(this.debug) System.out.println("Running Stacked Area Chart layout.");
                break;
            */
            default: 
                layout.add(new CircleLayout("pgraph"));
                if(this.debug) System.out.println("Running default (Circle) layout.");
                break;
        }
        layout.add(new RepaintAction());
        
        Visualization vis = new Visualization();
        vis.add("pgraph", pgraph);
        vis.putAction("layout", layout);

        Display d = new Display(vis);
        int i;
        double maxheight = 0;
        double maxwidth = 0;
        for(i = 0; i < this.nodecount; i++) {
            double currheight = this.nodes[i].x.getHeight();
            double currwidth = this.nodes[i].x.getWidth();
            if(currheight > maxheight) {
                maxheight = currheight;
            }
            if(currwidth > maxwidth) {
                maxwidth = currwidth;
            }
        }
        //d.setSize((int) Math.round(maxwidth * this.nodecount), (int) Math.round(maxheight * this.nodecount));
        d.setSize(1280, 720);
        
        vis.run("layout");
        if(this.debug) {
            testvis(vis, d);
        }
        
        TupleSet temp = vis.getVisualGroup("pgraph");
        VisualGraph vgraph;
        if(temp instanceof VisualGraph) {
            vgraph = (VisualGraph) temp;

            for(i = 0; i < this.nodecount; i++) {
                VNode pnode = this.nodes[i].x;
                Node tempnode;
                tempnode = vgraph.getNode((int) this.nodes[i].y);
                NodeItem vnode;
                if(tempnode instanceof NodeItem) {
                    vnode = (NodeItem) tempnode;
                    pnode.setX(vnode.getX());
                    pnode.setY(vnode.getY());
                    if(this.debug) {
                        System.out.println(this.nodes[i].x.getId() + " | X: " + vnode.getX() + " Y: " + vnode.getY());
                    }
                }
                else {
                    // vnode is not of type NodeItem
                    System.out.println("Type error! Layout could not be generated!");
                    return;
                }
            }
        }
        else {
            // vgraph is not of type VisualGraph
            System.out.println("Type error! Layout could not be generated!");
        }
    }
    
    private void testvis(Visualization vis, Display d) {
        ColorAction fill = new ColorAction("pgraph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(0, 200, 0));
        ColorAction edges = new ColorAction("pgraph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
        ActionList color = new ActionList();
        color.add(fill);
        color.add(edges);
        vis.putAction("color", color);
        ShapeRenderer r = new ShapeRenderer();
        vis.setRendererFactory(new DefaultRendererFactory(r));
        d.addControlListener(new DragControl());
        d.addControlListener(new PanControl());
        d.addControlListener(new ZoomControl());
        JFrame frame = new JFrame("Prefuse Display");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(d);
        frame.pack();
        frame.setVisible(true);
        vis.run("color");
    }
    
    /**
     * Searches linearly for the ID of the given Node
     */
    private int getPNode(VNode pnode) {
        int i;
        for(i = 0; i < this.nodecount; i++) {
            if(this.nodes[i].x.equals(pnode))
                return (int) this.nodes[i].y;
        }
        return -1;
    }
    
    
    class GenTuple<X, Y> {
        public final X x;
        public final Y y;
        
        public GenTuple(X px, Y py) {
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
            if(!(other instanceof GenTuple)) {
                return false;
            }
            GenTuple<Object, Object> this_ = (GenTuple<Object, Object>) this;
            GenTuple<Object, Object> other_ = (GenTuple<Object, Object>) other;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Objects;
import javafx.collections.ObservableList;
import javax.swing.JFrame;
import prefuse.action.ActionList;
import prefuse.action.assignment.ColorAction; // Testvis
import prefuse.action.layout.RandomLayout;
import prefuse.action.RepaintAction;
import prefuse.controls.DragControl; // Testvis
import prefuse.controls.PanControl; // Testvis
import prefuse.controls.ZoomControl; // Testvis
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.Display; // Testvis
import prefuse.render.DefaultRendererFactory; // Testvis
import prefuse.render.ShapeRenderer; // Testvis
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.Visualization;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

/**
 *
 * @author tobi
 */
public class LayoutGeneratorPrefuse implements LayoutGenerator {
    
    private VFlow workflow;
    private GenTuple<VNode, Integer>[] nodes;
    private int nodecount;
    private int conncount;
    private Graph pgraph;
    
    public LayoutGeneratorPrefuse() {
        //System.out.println("Creating layout generator");
    }
    
    /**
     * Sets up the model for the current workflow.
     * @param pworkflow current workflow to be layouted.
     */
    @Override
    public void setUp(VFlow pworkflow) {
        //System.out.println("Setting up workflow for layout generation.");
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
        //System.out.println("Setup complete with " + this.pgraph.getNodeCount() + " nodes and " + this.pgraph.getEdgeCount() + " edges.");
    }
    
    /**
     * Generates a Layout for the workflow 
     * as well as the nodes and connections given at SetUp.
     */
    @Override
    public void generateLayout() {
        //System.out.println("Generating layout.");
        ActionList layout = new ActionList();
        layout.add(new RandomLayout());
        layout.add(new RepaintAction());
        
        Visualization vis = new Visualization();
        vis.add("pgraph", pgraph);
        vis.putAction("layout", layout);
        
        Display d = new Display(vis);
        d.setSize(720, 500);
        
        vis.run("layout");
        TupleSet temp = vis.getVisualGroup("pgraph");
        VisualGraph vgraph;
        if(temp instanceof VisualGraph) {
            vgraph = (VisualGraph) temp;
            int i;
            for(i = 0; i < this.nodecount; i++) {
                VNode pnode = this.nodes[i].x;
                Node tempnode;
                tempnode = vgraph.getNode((int) this.nodes[i].y);
                NodeItem vnode;
                if(tempnode instanceof NodeItem) {
                    vnode = (NodeItem) tempnode;
                    pnode.setX(vnode.getX());
                    pnode.setY(vnode.getY());
                    //System.out.println("Node: " + i + " X: " + vnode.getX() + " Y: " + vnode.getY());
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
            if(!(other instanceof Tuple)) {
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

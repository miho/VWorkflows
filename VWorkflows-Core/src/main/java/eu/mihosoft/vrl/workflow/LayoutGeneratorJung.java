/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import javafx.collections.ObservableList;
import javax.swing.JFrame;
import org.apache.commons.collections4.Transformer;

/**
 *
 * @author tobi
 */
public class LayoutGeneratorJung implements LayoutGenerator {
    
    private VFlow workflow;
    private DirectedGraph<VNode, Connection> jgraph;
    private VNode[] nodes;
    private int nodecount;
    private int conncount;
    private Layout<VNode, Connection> layout;
    
    public LayoutGeneratorJung() {
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
            Integer sender = getNodeID(currConn.getSender().getNode());
            Integer receiver = getNodeID(currConn.getReceiver().getNode());
            this.jgraph.addEdge(currConn, this.nodes[sender], this.nodes[receiver]);
        }
        //System.out.println("Setup complete with " + this.jgraph.getVertexCount() + " nodes and " + this.jgraph.getEdgeCount() + " edges.");
    }
    
    /**
     * Generates a Layout for the workflow 
     * as well as the nodes and connections given at SetUp.
     */
    @Override
    public void generateLayout() {
        //System.out.println("Generating layout.");
        this.layout = new CircleLayout<>(this.jgraph);
        
        // Test
        System.out.println("testvis");
        this.layout.setSize(new Dimension(300,300));
        BasicVisualizationServer<VNode, Connection> vv = new BasicVisualizationServer<>(layout);
        vv.setPreferredSize(new Dimension(350, 350));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        System.out.println("Server setup complete");
        
        JFrame frame = new JFrame("View");
        System.out.println("adding server to frame");
        frame.getContentPane().add(vv);
        System.out.println("server added");
        frame.pack();
        System.out.println("frame packed");
        frame.setVisible(true);
        System.out.println("frame visible");
    }
    
    // Searches linearly for the ID of the given Node
    private Integer getNodeID(VNode pnode) {
        int i;
        for(i = 0; i < this.nodecount; i++) {
            if(this.nodes[i].equals(pnode))
                return i;
        }
        return -1;
    }
    
}

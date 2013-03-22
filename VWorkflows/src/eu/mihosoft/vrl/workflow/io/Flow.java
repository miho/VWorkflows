/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.io;

import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Flow extends Node{
    private List<Connection> connections;
    private List<Node> nodes;
    private List<String> connectionTypes;
    private Flow parent;
    private boolean visible;

    public Flow() {
    }

    public Flow(List<Connection> connections, List<Node> nodes) {
        this.connections = connections;
        this.nodes = nodes;
    }

    public Flow(Flow parent,String id, List<String> connectionTypes, List<Connection> connections, List<Node> nodes, String title, double x, double y, double width, double height, ValueObject valueObject, boolean visible, VisualizationRequest vReq) {
        super(id, title, x, y, width, height, valueObject, vReq);
        this.connectionTypes = connectionTypes;
        this.connections = connections;
        this.nodes = nodes;
        this.parent = parent;
        this.visible = visible;
    }

    /**
     * @return the connections
     */
    public List<Connection> getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    /**
     * @return the nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the connectionTypes
     */
    public List<String> getConnectionTypes() {
        return connectionTypes;
    }

    /**
     * @param connectionTypes the connectionTypes to set
     */
    public void setConnectionTypes(List<String> connectionTypes) {
        this.connectionTypes = connectionTypes;
    }

    /**
     * @return the parent
     */
    public Flow getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Flow parent) {
        this.parent = parent;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    
}

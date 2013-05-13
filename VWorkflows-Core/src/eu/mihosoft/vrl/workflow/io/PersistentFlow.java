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
public class PersistentFlow extends PersistentNode {

    private List<PersistentConnection> connections;
    private List<PersistentNode> nodes;
    private List<String> connectionTypes;
    private PersistentFlow parent;
    private boolean visible;

    public PersistentFlow() {
    }

    public PersistentFlow(List<PersistentConnection> connections, List<PersistentNode> nodes) {
        this.connections = connections;
        this.nodes = nodes;
    }

    public PersistentFlow(PersistentFlow parent, String id, List<String> connectionTypes,
            List<PersistentConnection> connections, List<PersistentNode> nodes, String title,
            double x, double y, double width, double height,
            ValueObject valueObject, boolean visible, VisualizationRequest vReq,
            List<String> inputTypes, List<String> outputTypes) {

        super(id, title, x, y, width, height, valueObject, vReq,
                inputTypes, outputTypes);
        this.connectionTypes = connectionTypes;
        this.connections = connections;
        this.nodes = nodes;
        this.parent = parent;
        this.visible = visible;
    }

    /**
     * @return the connections
     */
    public List<PersistentConnection> getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(List<PersistentConnection> connections) {
        this.connections = connections;
    }

    /**
     * @return the nodes
     */
    public List<PersistentNode> getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(List<PersistentNode> nodes) {
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
    public PersistentFlow getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(PersistentFlow parent) {
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

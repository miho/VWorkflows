/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
import javafx.scene.Node;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
class SelectedConnector {
    private Node node;
    private Connector connector;

    public SelectedConnector(Node n, Connector connector) {
        this.node = n;
        this.connector = connector;
    }

    public SelectedConnector() {
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public void setNode(Node n) {
        this.node = n;
    }

    public Node getNode() {
        return node;
    }
    
    
}

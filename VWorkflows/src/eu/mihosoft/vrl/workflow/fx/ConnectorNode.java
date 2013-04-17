/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Circle;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ConnectorNode extends Circle {

    private ObjectProperty<FXFlowNodeSkin> nodeSkinProperty = new SimpleObjectProperty<>();
    private String connectorId;

    public ConnectorNode(String connectorId) {
        this.connectorId = connectorId;
    }

    /**
     * @return the nodeSkinProperty
     */
    public ObjectProperty<FXFlowNodeSkin> nodeSkinProperty() {
        return nodeSkinProperty;
    }

    public void setNodeSkin(FXFlowNodeSkin skin) {
        this.nodeSkinProperty.set(skin);
    }

    public FXFlowNodeSkin getNodeSkin() {
        return this.nodeSkinProperty.get();
    }

    public String getConnectorId() {
        return connectorId;
    }

    public Connector getConnector() {
        
        // TODO split into input/output 
        Connector connector = getNodeSkin().getModel().getInputById(connectorId);

        if (connector == null) {
            connector = getNodeSkin().getModel().getOutputById(connectorId);
        }

        return connector;
    }
}

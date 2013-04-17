/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ConnectorNode extends Circle {

    private ObjectProperty<FXFlowNodeSkin> nodeSkinProperty = new SimpleObjectProperty<>();
    private String connectorId;

    public ConnectorNode() {
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
}

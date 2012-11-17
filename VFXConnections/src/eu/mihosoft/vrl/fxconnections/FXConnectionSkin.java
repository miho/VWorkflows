/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Path;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXConnectionSkin implements ConnectionSkin<Connection> {
    
    private ObjectProperty<FlowNode> nodeProperty = new SimpleObjectProperty<>();
    private Path connectionPath;

    public FXConnectionSkin() {
        init();
    }
    
    private void init() {
//        connectionPath.layoutXProperty().bind(this);
    }

    @Override
    public FlowNode getNode() {
        return nodeProperty.get();
    }

    @Override
    public void setNode(FlowNode n) {
        nodeProperty.set(n);
    }

    @Override
    public ObjectProperty<FlowNode> nodeProperty() {
        return nodeProperty;
    }
    
}

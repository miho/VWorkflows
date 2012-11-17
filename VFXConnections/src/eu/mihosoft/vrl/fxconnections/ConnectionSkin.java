/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ConnectionSkin<T extends Connection> extends Skin<Connection> {
    public FlowNode getNode();
    public void setNode(FlowNode n);
    public ObjectProperty<FlowNode> nodeProperty();
}

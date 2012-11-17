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
public interface FlowNodeSkin<T extends FlowNode, V extends Flow<T> > extends Skin<T> {
    public void setFlow(V flow);
    public V getFlow();
    public ObjectProperty<V> flowProperty();
}

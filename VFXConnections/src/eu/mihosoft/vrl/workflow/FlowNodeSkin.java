/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FlowNodeSkin<T extends FlowNode> extends Skin<T> {
//    public void setFlow(V flow);
//    public V getFlow();
//    public ObjectProperty<V> flowProperty();

    public ObservableList<FlowNodeSkin<T>> getChildren();

    public ObjectProperty<FlowNodeSkin<T>> parentProperty();
}

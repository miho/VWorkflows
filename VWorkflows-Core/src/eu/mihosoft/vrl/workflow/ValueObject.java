/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface ValueObject {
    public VNode getParent();
    public void setParent(VNode p);
    public Object getValue();
    public void setValue(Object o);
    public ObjectProperty<Object> valueProperty();
    // assumes we are receiver
    public CompatibilityResult compatible(ValueObject sender, String flowTpe);
    public VisualizationRequest getVisualizationRequest();
}

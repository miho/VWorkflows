/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ValueObject {
    public FlowNode getParent();
    public Object getValue();
    public void setValue(Object o);
    public ObjectProperty<Object> valueProperty();
    public CompatibilityResult compatible(ValueObject other, String flowTpe);
    public VisualizationRequest getVisualizationRequest();
}

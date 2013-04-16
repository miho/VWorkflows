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
public interface ConnectorValueObject {
    public Connector getParent();
    public Object getValue();
    public void setValue(Object o);
    public ObjectProperty<Object> valueProperty();
    public CompatibilityResult compatible(ConnectorValueObject other);
    public String getConnectionType();
//    public VisualizationRequest getVisualizationRequest();
}
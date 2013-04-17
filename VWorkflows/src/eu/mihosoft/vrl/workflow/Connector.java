/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connector extends Model {

    public void setValueObject(ConnectorValueObject obj);
    public ConnectorValueObject getValueObject();
    public ObjectProperty<ConnectorValueObject> valueObjectProperty();
    
    public void setId(String id);
    public String getId();
    public StringProperty idProperty();
    public String getGlobalId();
    
    public void setParent(FlowNode parent);
    public FlowNode getParent();
    public ObjectProperty<FlowNode> parentProperty();
    
}

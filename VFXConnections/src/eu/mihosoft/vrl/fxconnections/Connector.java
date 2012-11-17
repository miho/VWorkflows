/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connector<T extends FlowNode> extends Model{
    
    public StringProperty idProperty();
    public void setId(String id);
    public String getID();
    
    public ObjectProperty<T> parentProperty();
    public void setParent(T p);
    public T getParent();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FlowNode<T> {
    
    public StringProperty titleProperty();
    
    public void setTitle(String title);
    public String getTitle();
    
    public StringProperty idProperty();
    
    public void setId(String id);
    public String getId();

    public DoubleProperty xProperty();
    public DoubleProperty yProperty();
    
    public void setX(double x);
    public void setY(double x);
    public double getX();
    public double getY();
    
    public ObservableList<FlowNode<T>> getChildren();
    
    public T getNode();
    
    public ObservableList<Connector<T>> getInputs();
    public ObservableList<Connector<T>> getOutputs();
}

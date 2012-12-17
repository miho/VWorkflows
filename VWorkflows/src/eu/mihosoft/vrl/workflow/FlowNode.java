/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FlowNode extends Model {
    
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
    
    public DoubleProperty widthProperty();
    public DoubleProperty heightProperty();
    
    public void setWidth(double w);
    public void setHeight(double h);
    
    public double getWidth();
    public double getHeight();
    
    public ObservableList<FlowNode> getChildren();
    
//    public ObservableList<Connector<FlowNode>> getInputs();
//    public ObservableList<Connector<FlowNode>> getOutputs();
    
    public void setValueObject(ValueObject obj);
    public ValueObject getValueObject();
    public ObjectProperty<ValueObject> valueObjectProperty();
    
    public Flow getFlow();

    /**
     * @return the inputProperty
     */
    BooleanProperty inputProperty();

    boolean isInput();

    boolean isOutput();

    /**
     * @return the outputProperty
     */
    BooleanProperty outputProperty();

    void setInput(boolean state);

    void setOutput(boolean state);
    
}

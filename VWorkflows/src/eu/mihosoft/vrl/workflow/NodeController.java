/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface NodeController {

    public void setModel(FlowNode node);

    public FlowNode getModel();

    public ObjectProperty<FlowNode> modelProperty();

    public void setSkin(FlowNodeSkin skin);

    public FlowNodeSkin getSkin();

    public ObjectProperty<FlowNodeSkin> skinProperty();

    public StringProperty titleProperty();

    public void setTitle(String title);

    public String getTitle();

    public DoubleProperty xProperty();

    public DoubleProperty yProperty();

    public void setX(double x);

    public void setY(double y);

    public double getX();

    public double getY();

    public DoubleProperty widthProperty();

    public DoubleProperty heightProperty();

    public void setWidth(double w);

    public void setHeight(double h);

    public double getWidth();

    public double getHeight();

    public Connector newInput(String connectionType);

    public Connector newInput(String myId, String connectionType);

    public Connector newOutput(String connectionType);

    public Connector newOutput(String myId, String connectionType);

    public Connector getInputById(String id);

    public Connector getOutputById(String id);

    public void setConnectorIdGenerator(IdGenerator generator);

    public IdGenerator getConnectorIdGenerator();

    public ObjectProperty<IdGenerator> connectorIdGeneratorProperty();
}

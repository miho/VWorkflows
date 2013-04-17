/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class NodeControllerImpl implements NodeController{
    
    private ObjectProperty<FlowNode> modepProperty = new SimpleObjectProperty<>();
    private ObjectProperty<FlowNodeSkin> skinProperty = new SimpleObjectProperty<>();

    @Override
    public void setModel(FlowNode node) {
        this.modepProperty.set(node);
    }

    @Override
    public FlowNode getModel() {
        return this.modepProperty.get();
    }

    @Override
    public ObjectProperty<FlowNode> modelProperty() {
        return this.modepProperty;
    }

    @Override
    public void setSkin(FlowNodeSkin skin) {
        this.skinProperty.set(skin);
    }

    @Override
    public FlowNodeSkin getSkin() {
        return this.skinProperty.get();
    }

    @Override
    public ObjectProperty<FlowNodeSkin> skinProperty() {
        return this.skinProperty;
    }

    @Override
    public StringProperty titleProperty() {
        return getModel().titleProperty();
    }

    @Override
    public void setTitle(String title) {
        this.getModel().setTitle(title);
    }

    @Override
    public String getTitle() {
        return this.getModel().getTitle();
    }

    @Override
    public DoubleProperty xProperty() {
        return this.getModel().xProperty();
    }

    @Override
    public DoubleProperty yProperty() {
        return this.getModel().yProperty();
    }

    @Override
    public void setX(double x) {
        this.getModel().setX(x);
    }

    @Override
    public void setY(double y) {
        this.getModel().setY(y);
    }

    @Override
    public double getX() {
        return this.getModel().getX();
    }

    @Override
    public double getY() {
        return this.getModel().getY();
    }

    @Override
    public DoubleProperty widthProperty() {
        return this.getModel().widthProperty();
    }

    @Override
    public DoubleProperty heightProperty() {
        return this.getModel().heightProperty();
    }

    @Override
    public void setWidth(double w) {
        this.getModel().setWidth(w);
    }

    @Override
    public void setHeight(double h) {
        this.getModel().setHeight(h);
    }

    @Override
    public double getWidth() {
        return this.getModel().getWidth();
    }

    @Override
    public double getHeight() {
        return this.getModel().getHeight();
    }

    @Override
    public Connector newInput(String connectionType) {
        return this.getModel().newInput(connectionType);
    }

    @Override
    public Connector newInput(String myId, String connectionType) {
        return this.getModel().newInput(myId, connectionType);
    }

    @Override
    public Connector newOutput(String connectionType) {
        return this.getModel().newOutput(connectionType);
    }

    @Override
    public Connector newOutput(String myId, String connectionType) {
        return this.getModel().newOutput(myId, connectionType);
    }

    @Override
    public Connector getInputById(String id) {
        return this.getModel().getInputById(id);
    }

    @Override
    public Connector getOutputById(String id) {
        return this.getModel().getOutputById(id);
    }

    @Override
    public void setConnectorIdGenerator(IdGenerator generator) {
        this.getModel().setConnectorIdGenerator(generator);
    }

    @Override
    public IdGenerator getConnectorIdGenerator() {
        return this.getModel().getConnectorIdGenerator();
    }

    @Override
    public ObjectProperty<IdGenerator> connectorIdGeneratorProperty() {
        return this.getModel().connectorIdGeneratorProperty();
    }
    
}

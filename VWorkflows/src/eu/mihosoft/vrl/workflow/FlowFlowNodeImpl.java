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
import javafx.collections.ObservableMap;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FlowFlowNodeImpl implements FlowFlowNode {

    private FlowNode node;
    private FlowModel flow;

    public FlowFlowNodeImpl(FlowModel parentFlow) {
        node = new FlowNodeBase(parentFlow);
        flow = new FlowModelImpl();
    }

    @Override
    public ConnectionResult tryConnect(FlowNode s, FlowNode r, String flowType) {
        return flow.tryConnect(s, r, flowType);
    }

    @Override
    public ConnectionResult connect(FlowNode s, FlowNode r, String flowType) {
        return flow.connect(s, r, flowType);
    }

    @Override
    public FlowNode remove(FlowNode n) {
        return flow.remove(n);
    }

    @Override
    public ObservableList<FlowNode> getNodes() {
        return flow.getNodes();
    }

    @Override
    public FlowNode getSender(Connection c) {
        return flow.getSender(c);
    }

    @Override
    public FlowNode getReceiver(Connection c) {
        return flow.getReceiver(c);
    }

    @Override
    public void addConnections(Connections connections, String flowType) {
        flow.addConnections(connections, flowType);
    }

    @Override
    public Connections getConnections(String flowType) {
        return flow.getConnections(flowType);
    }

    @Override
    public ObservableMap<String, Connections> getAllConnections() {
        return flow.getAllConnections();
    }

    @Override
    public void setFlowNodeClass(Class<? extends FlowNode> cls) {
        flow.setFlowNodeClass(cls);
    }

    @Override
    public Class<? extends FlowNode> getFlowNodeClass() {
        return flow.getFlowNodeClass();
    }

    @Override
    public FlowNode newNode(ValueObject obj) {
        return flow.newNode(obj);
    }

    @Override
    public FlowNode newNode() {
        return flow.newNode();
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StringProperty titleProperty() {
        return node.titleProperty();
    }

    @Override
    public void setTitle(String title) {
        node.setTitle(title);
    }

    @Override
    public String getTitle() {
        return node.getTitle();
    }

    @Override
    public StringProperty idProperty() {
        return node.idProperty();
    }

    @Override
    public void setId(String id) {
        node.setId(id);
    }

    @Override
    public String getId() {
        return node.getId();
    }

    @Override
    public DoubleProperty xProperty() {
        return node.xProperty();
    }

    @Override
    public DoubleProperty yProperty() {
        return node.yProperty();
    }

    @Override
    public void setX(double x) {
        node.setX(x);
    }

    @Override
    public void setY(double x) {
        node.setY(x);
    }

    @Override
    public double getX() {
        return node.getX();
    }

    @Override
    public double getY() {
        return node.getY();
    }

    @Override
    public DoubleProperty widthProperty() {
        return node.widthProperty();
    }

    @Override
    public DoubleProperty heightProperty() {
        return node.heightProperty();
    }

    @Override
    public void setWidth(double w) {
        node.setWidth(w);
    }

    @Override
    public void setHeight(double h) {
        node.setHeight(h);
    }

    @Override
    public double getWidth() {
        return node.getWidth();
    }

    @Override
    public double getHeight() {
        return node.getHeight();
    }

    @Override
    public ObservableList<FlowNode> getChildren() {
        return node.getChildren();
    }

    @Override
    public void setValueObject(ValueObject obj) {
        node.setValueObject(obj);
    }

    @Override
    public ValueObject getValueObject() {
        return node.getValueObject();
    }

    @Override
    public ObjectProperty<ValueObject> valueObjectProperty() {
        return node.valueObjectProperty();
    }

    @Override
    public FlowModel getFlow() {
        return node.getFlow();
    }

    @Override
    public BooleanProperty inputProperty() {
        return node.inputProperty();
    }

    @Override
    public boolean isInput() {
        return node.isInput();
    }

    @Override
    public boolean isOutput() {
        return node.isOutput();
    }

    @Override
    public BooleanProperty outputProperty() {
        return node.outputProperty();
    }

    @Override
    public void setInput(boolean state) {
        node.setInput(state);
    }

    @Override
    public void setOutput(boolean state) {
        node.setOutput(state);
    }
}

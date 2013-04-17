/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import com.sun.javafx.collections.UnmodifiableObservableMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FlowFlowNodeImpl implements FlowFlowNode {

    private final FlowNode node;
    private final FlowModelImpl flow;

    @Override
    public BooleanProperty visibleProperty() {
        return flow.visibleProperty();
    }

    @Override
    public void setVisible(boolean b) {
        flow.setVisible(b);
    }

    @Override
    public boolean isVisible() {
        return flow.isVisible();
    }

    public FlowFlowNodeImpl(FlowModel parentFlow) {

        flow = new FlowModelImpl();
        
        FlowFlowNode pFlow = null;

        if (parentFlow != null) {
            if (!(parentFlow instanceof FlowFlowNode)) {
                throw new IllegalArgumentException("Only " + FlowFlowNode.class.getName() + " objects are supported. Given type: " + parentFlow.getClass());
            } else {
                pFlow = (FlowFlowNode) parentFlow;
            }
            if (parentFlow.getIdGenerator()==null) {
                throw new IllegalStateException("Please define an id generator before creating subflows!");
            }
            
            setIdGenerator(parentFlow.getIdGenerator().newChild());
        }

        node = new FlowNodeBase(pFlow);
        
    }

    @Override
    public ConnectionResult tryConnect(Connector s, Connector r) {
        return flow.tryConnect(s, r);
    }

    @Override
    public ConnectionResult connect(Connector s, Connector r) {
        return flow.connect(s, r);
    }

    @Override
    public FlowNode remove(FlowNode n) {
        return flow.remove(n);
    }

    @Override
    public void clear() {
        flow.clear();
    }

    @Override
    public ObservableList<FlowNode> getNodes() {
        return flow.getNodes();
    }

    @Override
    public Connector getSender(Connection c) {
        return flow.getSender(c);
    }

    @Override
    public Connector getReceiver(Connection c) {
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
    public UnmodifiableObservableMap<String, Connections> getAllConnections() {
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
    public VisualizationRequest getVisualizationRequest() {
        return node.getVisualizationRequest();
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        node.setVisualizationRequest(vReq);
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
    public void setValueObject(NodeValueObject obj) {
        node.setValueObject(obj);
    }

    @Override
    public NodeValueObject getValueObject() {
        return node.getValueObject();
    }

    @Override
    public ObjectProperty<NodeValueObject> valueObjectProperty() {
        return node.valueObjectProperty();
    }

    @Override
    public FlowFlowNode getFlow() {
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

    @Override
    public FlowFlowNode newFlowNode(NodeValueObject obj) {
        FlowFlowNode flowNode = new FlowFlowNodeImpl(this);

        return (FlowFlowNode) flow.newNode(flowNode, obj);
    }

    @Override
    public FlowFlowNode newFlowNode() {
        FlowFlowNode flowNode = new FlowFlowNodeImpl(this);

        return (FlowFlowNode) flow.newNode(flowNode, new EmptyValueObject()); // end newNode()

    }
    
    @Override
    public FlowNode newNode(NodeValueObject obj) {

        FlowNode result = null;

        try {
            Constructor constructor = getFlowNodeClass().getConstructor(FlowFlowNode.class);
            try {
                result = (FlowNode) constructor.newInstance(this);
                result.setValueObject(obj);

                result = flow.newNode(result, obj);

            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ConnectionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
    
    @Override
    public FlowNode newNode() {
        return newNode(new EmptyValueObject());
    }

//    @Override
//    public String getGlobalId() {
//        return node.getGlobalId();
//    }

    @Override
    public void setIdGenerator(IdGenerator generator) {
        flow.setIdGenerator(generator);
    }

    @Override
    public IdGenerator getIdGenerator() {
        return flow.getIdGenerator();
    }

    @Override
    public void setNodeLookup(NodeLookup nodeLookup) {
        flow.setNodeLookup(nodeLookup);
    }

    @Override
    public NodeLookup getNodeLookup() {
        return flow.getNodeLookup();
    }

    @Override
    public Connector newInput(String connectionType) {
       return node.newInput(connectionType);
    }

    @Override
    public Connector newInput(String myId, String connectionType) {
        return node.newInput(myId, connectionType);
    }

    @Override
    public Connector newOutput(String connectionType) {
        return node.newOutput(connectionType);
    }

    @Override
    public Connector newOutput(String myId, String connectionType) {
        return node.newOutput(myId, connectionType);
    }

    @Override
    public ObjectProperty<VisualizationRequest> visualizationRequestProperty() {
        return node.visualizationRequestProperty();
    }

    @Override
    public void setConnectorIdGenerator(IdGenerator generator) {
        this.node.setConnectorIdGenerator(generator);
    }

    @Override
    public IdGenerator getConnectorIdGenerator() {
        return this.node.getConnectorIdGenerator();
    }

    @Override
    public ObjectProperty<IdGenerator> connectorIdGeneratorProperty() {
        return this.node.connectorIdGeneratorProperty();
    }

    @Override
    public Connector getInputById(String id) {
        return node.getInputById(id);
    }

    @Override
    public Connector getOutputById(String id) {
        return node.getOutputById(id);
    }

    @Override
    public ObservableList<Connector> getInputs() {
        return this.node.getInputs();
    }

    @Override
    public ObservableList<Connector> getOutputs() {
        return this.node.getOutputs();
    }
}


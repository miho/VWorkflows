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
class VFlowModelImpl implements VFlowModel {

    private final VNode node;
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

    public VFlowModelImpl(FlowModel parentFlow) {

        flow = new FlowModelImpl();

        VFlowModel pFlow = null;

        if (parentFlow != null) {
            if (!(parentFlow instanceof VFlowModel)) {
                throw new IllegalArgumentException("Only " + VFlowModel.class.getName() + " objects are supported. Given type: " + parentFlow.getClass());
            } else {
                pFlow = (VFlowModel) parentFlow;
            }
            if (parentFlow.getIdGenerator()==null) {
                throw new IllegalStateException("Please define an id generator before creating subflows!");
            }
            
            setIdGenerator(parentFlow.getIdGenerator().newChild());
        }

        node = new VNodeImpl(pFlow);
        setTitle("Node");
        
    }

    @Override
    public ConnectionResult tryConnect(VNode s, VNode r, String flowType) {
        return flow.tryConnect(s, r, flowType);
    }

    @Override
    public ConnectionResult connect(VNode s, VNode r, String flowType) {
        return flow.connect(s, r, flowType);
    }
    
     @Override
    public ConnectionResult tryConnect(Connector s, Connector r ){
        return flow.tryConnect(s, r);
    }

    @Override
    public ConnectionResult connect(Connector s, Connector r ){
        return flow.connect(s, r);
    }

    @Override
    public VNode remove(VNode n) {
        return flow.remove(n);
    }

    @Override
    public void clear() {
        flow.clear();
    }

    @Override
    public ObservableList<VNode> getNodes() {
        return flow.getNodes();
    }

    @Override
    public VNode getSender(Connection c) {
        return flow.getSender(c);
    }

    @Override
    public VNode getReceiver(Connection c) {
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
    public void setFlowNodeClass(Class<? extends VNode> cls) {
        flow.setFlowNodeClass(cls);
    }

    @Override
    public Class<? extends VNode> getFlowNodeClass() {
        return flow.getFlowNodeClass();
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return null;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StringProperty titleProperty() {
        return node.titleProperty();
    }

    @Override
    public final void setTitle(String title) {
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
    public Connector getConnector(String localId) {
        return node.getConnector(localId);
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
    public VFlowModel getFlow() {
        return node.getFlow();
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
    public VFlowModel newFlowNode(ValueObject obj) {
        VFlowModel flowNode = new VFlowModelImpl(this);

        return (VFlowModel) flow.newNode(flowNode, obj);
    }

    @Override
    public VFlowModel newFlowNode() {
        VFlowModel flowNode = new VFlowModelImpl(this);
        
        DefaultValueObject valObj = new DefaultValueObject();

        VFlowModel result = (VFlowModel) flow.newNode(flowNode, valObj); // end newNode()
        
        valObj.setParent(result);
        
        return result;

    }
    
    @Override
    public VNode newNode(ValueObject obj) {

        VNode result = null;

        try {
            Constructor constructor = getFlowNodeClass().getConstructor(VFlowModel.class);
            try {
                result = (VNode) constructor.newInstance(this);
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
    public VNode newNode() {
        DefaultValueObject valObj = new DefaultValueObject();
        VNode result = newNode(valObj);
        valObj.setParent(result);
        return result;
    }

//    @Override
//    public String getGlobalId() {
//        return node.getGlobalId();
//    }

    @Override
    public final void setIdGenerator(IdGenerator generator) {
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
    public ObservableList<String> getInputTypes() {
        return node.getInputTypes();
    }

    @Override
    public ObservableList<String> getOutputTypes() {
        return node.getOutputTypes();
    }

    @Override
    public boolean isInputOfType(String type) {
        return node.isInputOfType(type);
    }

    @Override
    public boolean isOutputOfType(String type) {
        return node.isOutputOfType(type);
    }

    @Override
    public Connector getMainInput(String type) {
        return this.node.getMainInput(type);
    }

    @Override
    public Connector getMainOutput(String type) {
        return this.node.getMainOutput(type);
    }

    @Override
    public Connector addInput(String type) {
        return this.node.addInput(type);
    }

    @Override
    public Connector addOutput(String type) {
       return this.node.addOutput(type);
    }

    @Override
    public Connector addConnector(Connector c) {
        return this.node.addConnector(c);
    }

    @Override
    public ObservableList<Connector> getConnectors() {
        return this.node.getConnectors();
    }
}


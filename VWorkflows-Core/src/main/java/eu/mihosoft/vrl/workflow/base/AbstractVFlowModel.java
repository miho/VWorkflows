/*
 * Copyright 2012-2017 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
package eu.mihosoft.vrl.workflow.base;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowModel;
import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.NodeLookup;
import eu.mihosoft.vrl.workflow.ThruConnector;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.impl.DefaultValueObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Collection;

/**
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public abstract class AbstractVFlowModel implements VFlowModel {
    protected final VNode node;
    protected final FlowModel flow;

    protected final ObservableList<ThruConnector> thruInputs = FXCollections.observableArrayList();
    protected final ObservableList<ThruConnector> thruOutputs = FXCollections.observableArrayList();
    private final ObservableList<ThruConnector> unmodifiableThruInputs = FXCollections.unmodifiableObservableList(thruInputs);
    private final ObservableList<ThruConnector> unmodifiableThruOutputs = FXCollections.unmodifiableObservableList(thruOutputs);

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

    public AbstractVFlowModel(FlowModel parentFlow) {
        flow = instantiateFlowModel();

        VFlowModel pFlow = null;

        if (parentFlow != null) {
            if (!(parentFlow instanceof VFlowModel)) {
                throw new IllegalArgumentException("Only " + VFlowModel.class.getName() + " objects are supported. Given type: " + parentFlow.getClass());
            } else {
                pFlow = (VFlowModel) parentFlow;
            }
            if (parentFlow.getIdGenerator() == null) {
                throw new IllegalStateException("Please define an id generator before creating subflows!");
            }

            setIdGenerator(parentFlow.getIdGenerator().newChild());
        }

        node = instantiateVNode(pFlow);
        setTitle("Node");

        node.getConnectors().addListener(
            (ListChangeListener.Change<? extends Connector> c) -> {
                while (c.next()) {
                    for (Connector connector : c.getRemoved()) {
                        if (connector instanceof ThruConnector) {
                            ThruConnector tC = (ThruConnector) connector;

                            if (tC.isInput()) {
                                thruInputs.remove(tC);
                            } else if (tC.isOutput()) {
                                thruOutputs.remove(tC);
                            }

                            flow.remove(tC.getInnerNode());
                        }
                    }
                }
            });
    }

    protected abstract FlowModel instantiateFlowModel();

    protected abstract VNode instantiateVNode(VFlowModel parentFlow);

    @Override
    public ConnectionResult tryConnect(VNode s, VNode r, String flowType) {
        return flow.tryConnect(s, r, flowType);
    }

    @Override
    public ConnectionResult connect(VNode s, VNode r, String flowType) {
        return flow.connect(s, r, flowType);
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
    public ObservableMap<String, Connections> getAllConnections() {
        return flow.getAllConnections();
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
    public VFlowModel newFlowNode(ValueObject obj) {
        VFlowModel flowNode = instantiateVFlowModel(this);
        return (VFlowModel) flow.registerNode(flowNode, obj, getId());
    }

    @Override
    public VFlowModel newFlowNode() {
        VFlowModel flowNode = instantiateVFlowModel(this);

        flowNode.setNodeLookup(getNodeLookup());

        ValueObject valObj = createValueObject();

        VFlowModel result = (VFlowModel) flow.registerNode(flowNode, valObj, getId());

        valObj.setParent(result);

        return result;
    }

    protected abstract VFlowModel instantiateVFlowModel(FlowModel parent);

    protected DefaultValueObject createValueObject() {
        return new DefaultValueObject();
    }

    @Override
    public VNode newNode(ValueObject obj) {
        VNode result = instantiateVNode(this);
        result.setValueObject(obj);
        result = flow.registerNode(result, obj, getId());
        return result;
    }

    @Override
    public VNode newNode() {
        ValueObject valObj = createValueObject();
        VNode result = newNode(valObj);
        valObj.setParent(result);
        return result;
    }

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
    public Connector getMainInput(String type) {
        return this.node.getMainInput(type);
    }

    @Override
    public Connector getMainOutput(String type) {
        return this.node.getMainOutput(type);
    }

    @Override
    public Connector addInput(String type) {
        return this.node.addInput(this, type);
    }

    @Override
    public Connector addOutput(String type) {
        return this.node.addOutput(this, type);
    }

    @Override
    public Connector addConnector(Connector c) {
        return this.node.addConnector(this, c);
    }

    @Override
    public ObservableList<Connector> getConnectors() {
        return this.node.getConnectors();
    }

    @Override
    public ObservableList<Connector> getInputs() {
        return this.node.getInputs();
    }

    @Override
    public ObservableList<Connector> getOutputs() {
        return this.node.getOutputs();
    }

    @Override
    public Connector setMainInput(Connector connector) {
        this.node.setMainInput(connector);
        return connector;
    }

    @Override
    public Connector setMainOutput(Connector connector) {
        this.node.setMainOutput(connector);
        return connector;
    }

    @Override
    public Collection<String> getMainInputTypes() {
        return this.node.getMainInputTypes();
    }

    @Override
    public Collection<String> getMainOutputTypes() {
        return this.node.getMainOutputTypes();
    }

    @Override
    public boolean isSelected() {
        return node.isSelected();
    }

    @Override
    public boolean requestSelection(boolean b) {
        return node.requestSelection(b);
    }

    @Override
    public ReadOnlyBooleanProperty selectedProperty() {
        return node.selectedProperty();
    }

    @Override
    public BooleanProperty selectableProperty() {
        return node.selectableProperty();
    }

    @Override
    public boolean isSelectable() {
        return node.isSelectable();
    }

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        return node.visualizationRequestProperty();
    }

    @Override
    public boolean isVisualizationRequestInitialized() {
        return node.isVisualizationRequestInitialized();
    }

    @Override
    public ThruConnector addThruInput(String type) {
        VNode innerNode = newNode();

        innerNode.getVisualizationRequest().
            set(VisualizationRequest.KEY_NODE_NOT_REMOVABLE, true);

        Connector innerConnector = innerNode.
            setMainOutput(innerNode.addOutput(type));

        ThruConnector tC = node.addThruInput(
            node, type, innerNode, innerConnector);

        thruInputs.add(tC);

        return tC;
    }

    @Override
    public ThruConnector addThruOutput(String type) {
        VNode innerNode = newNode();

        innerNode.getVisualizationRequest().
            set(VisualizationRequest.KEY_NODE_NOT_REMOVABLE, true);

        Connector innerConnector = innerNode.
            setMainInput(innerNode.addInput(type));

        ThruConnector tC = node.addThruOutput(
            node, type, innerNode, innerConnector);

        thruOutputs.add(tC);

        return tC;
    }

    @Override
    public ObservableList<ThruConnector> getThruInputs() {
        return this.unmodifiableThruInputs;
    }

    @Override
    public ObservableList<ThruConnector> getThruOutputs() {
        return this.unmodifiableThruOutputs;
    }

    @Override
    public boolean removeConnector(Connector c) {
        return this.node.removeConnector(c);
    }

    @Override
    public int getDepth() {
        return this.node.getDepth();
    }

    @Override
    public FlowModel getRoot() {
        FlowModel root = this.node.getRoot();

        if (root == null) {
            root = this;
        }

        return root;
    }

    @Override
    public Connector addInput(VNode node, String type) {
        return this.node.addInput(node, type);
    }

    @Override
    public Connector addOutput(VNode node, String type) {
        return this.node.addOutput(node, type);
    }

    @Override
    public ThruConnector addThruInput(VNode node, String type, VNode innerNode, Connector innerConnector) {
        return this.node.addThruInput(node, type, innerNode, innerConnector);
    }

    @Override
    public ThruConnector addThruOutput(VNode node, String type, VNode innerNode, Connector innerConnector) {
        return this.node.addThruOutput(node, type, innerNode, innerConnector);
    }

    @Override
    public Connector addConnector(VNode node, Connector c) {
        return this.node.addConnector(node, c);
    }

    @Override
    public VNode registerNode(VNode result, ValueObject obj, String parentId) {
        return this.flow.registerNode(result, obj, parentId);
    }
}
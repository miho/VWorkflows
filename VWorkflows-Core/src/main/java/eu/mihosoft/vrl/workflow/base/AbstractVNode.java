/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowModel;
import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.ThruConnector;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public abstract class AbstractVNode implements VNode {
    protected final ObservableList<Connector> inputs = FXCollections.observableArrayList();
    protected final ObservableList<Connector> outputs = FXCollections.observableArrayList();
    protected final ObservableList<Connector> connectors = FXCollections.observableArrayList();
    protected final ObservableList<Connector> unmodifiableOutputs = FXCollections.unmodifiableObservableList(outputs);
    protected final ObservableList<Connector> unmodifiableInputs = FXCollections.unmodifiableObservableList(inputs);
    protected final StringProperty idProperty = new SimpleStringProperty();

    protected final StringProperty titleProperty = new SimpleStringProperty(this, "title");
    protected final DoubleProperty xProperty = new SimpleDoubleProperty(this, "x");
    protected final DoubleProperty yProperty = new SimpleDoubleProperty(this, "y");
    protected final DoubleProperty widthProperty = new SimpleDoubleProperty(this, "width");
    protected final DoubleProperty heightProperty = new SimpleDoubleProperty(this, "height");

    protected final BooleanProperty selectedProperty = new SimpleBooleanProperty(false, "selected");
    protected final BooleanProperty selectableProperty = new SimpleBooleanProperty(true, "selectable");

    protected final ObjectProperty<ValueObject> valueObjectProperty = new SimpleObjectProperty<>(this, "valueObject");
    protected final Map<String, Connector> mainInputs = new HashMap<>();
    protected final Map<String, Connector> mainOutputs = new HashMap<>();

    protected VFlowModel flow;
    protected IdGenerator connectorIdGenerator;
    protected ObjectProperty<VisualizationRequest> visualizationRequestProperty;

    public AbstractVNode(VFlowModel flow) {
        this.flow = flow;
        connectorIdGenerator = instantiateIdGenerator();

        setWidth(200);
        setHeight(150);

        setTitle("Node");

        setValueObject(instantiateValueObject());

        valueObjectProperty.addListener((value, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.setParent(AbstractVNode.this);
            }
        });

        connectors.addListener(createConnectorsListener());
    }

    protected abstract IdGenerator instantiateIdGenerator();

    protected abstract ValueObject instantiateValueObject();

    protected ListChangeListener<Connector> createConnectorsListener() {
        return change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    for (Connector connector : change.getRemoved()) {
                        if (connector.isInput()) {
                            inputs.remove(connector);

                        }

                        if (connector.isOutput()) {
                            outputs.remove(connector);
                        }

                        Connections connections = getFlow().getConnections(connector.getType());
                        connections.getConnections().removeAll(connections.getAllWith(connector));

                        connectorIdGenerator.getIds().remove(connector.getId());
                    }
                } else if (change.wasAdded()) {
                    for (Connector connector : change.getAddedSubList()) {
                        if (connector.isInput()) {
                            inputs.add(connector);
                        }

                        if (connector.isOutput()) {
                            outputs.add(connector);
                        }

                        if (connector instanceof ThruConnector) {
                            connector.setLocalId(connectorIdGenerator.newId("thru"));
                        } else {
                            connector.setLocalId(connectorIdGenerator.newId());
                        }
                    }
                }
            }
        };
    }

    @Override
    public StringProperty titleProperty() {
        return titleProperty;
    }

    @Override
    public final void setTitle(String title) {
        titleProperty.set(title);
    }

    @Override
    public String getTitle() {
        return titleProperty.get();
    }

    @Override
    public StringProperty idProperty() {
        return idProperty;
    }

    @Override
    public void setId(String id) {
        idProperty.set(id);
    }

    @Override
    public String getId() {
        return idProperty.get();
    }

    @Override
    public DoubleProperty xProperty() {
        return xProperty;
    }

    @Override
    public DoubleProperty yProperty() {
        return yProperty;
    }

    @Override
    public void setX(double x) {
        xProperty.set(x);
    }

    @Override
    public void setY(double y) {
        yProperty.set(y);
    }

    @Override
    public double getX() {
        return xProperty.get();
    }

    @Override
    public double getY() {
        return yProperty.get();
    }

    @Override
    public DoubleProperty widthProperty() {
        return widthProperty;
    }

    @Override
    public DoubleProperty heightProperty() {
        return heightProperty;
    }

    @Override
    public final void setWidth(double w) {
        widthProperty.set(w);
    }

    @Override
    public final void setHeight(double h) {
        heightProperty.set(h);
    }

    @Override
    public double getWidth() {
        return widthProperty.get();
    }

    @Override
    public double getHeight() {
        return heightProperty.get();
    }

    @Override
    public ValueObject getValueObject() {
        return valueObjectProperty.get();
    }

    @Override
    public final void setValueObject(ValueObject o) {
        valueObjectProperty.set(o);
    }

    @Override
    public ObjectProperty<ValueObject> valueObjectProperty() {
        return valueObjectProperty;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return visualizationRequestProperty().getValue();
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        writableVisualizationRequestProperty().set(vReq);
    }

    protected ObjectProperty<VisualizationRequest> writableVisualizationRequestProperty() {
        if (visualizationRequestProperty == null) {
            visualizationRequestProperty = new SimpleObjectProperty<>(this, "visualizationRequest");
            visualizationRequestProperty.set(instantiateVisualizationRequest());
        }

        if (visualizationRequestProperty.get() == null) {
            visualizationRequestProperty.set(instantiateVisualizationRequest());
        }

        return visualizationRequestProperty;
    }

    protected abstract VisualizationRequest instantiateVisualizationRequest();

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        return writableVisualizationRequestProperty();
    }

    @Override
    public VFlowModel getFlow() {
        return flow;
    }

    @Override
    public Connector addInput(String type) {
        return addInput(this, type);
    }

    @Override
    public Connector addOutput(String type) {
        return this.addOutput(this, type);
    }

    @Override
    public Connector addConnector(Connector c) {
        return addConnector(this, c);
    }

    public Connector addInput(VNode node, String type) {
        Connector c = instantiateConnector(node, type, null, true);
        connectors.add(c);
        return c;
    }

    public Connector addOutput(VNode node, String type) {
        Connector c = instantiateConnector(node, type, null, false);
        connectors.add(c);
        return c;
    }

    protected abstract Connector instantiateConnector(VNode node, String type, String localId, boolean input);

    public ThruConnector addThruInput(VNode node, String type, VNode innerNode, Connector innerConnector) {
        ThruConnector c = instantiateThruConnector(node, type, null, true, innerNode, innerConnector);
        connectors.add(c);
        return c;
    }

    public ThruConnector addThruOutput(VNode node, String type, VNode innerNode, Connector innerConnector) {
        ThruConnector c = instantiateThruConnector(node, type, null, false, innerNode, innerConnector);
        connectors.add(c);
        return c;
    }

    protected abstract ThruConnector instantiateThruConnector(VNode node, String type, String localId, boolean input, VNode innerNode, Connector innerConnector);

    public Connector addConnector(VNode node, Connector c) {
        String localId = c.getLocalId();

        if (connectorIdGenerator.getIds().contains(localId)) {
            throw new IllegalArgumentException(
                "Cannot add connector: id \"" + localId + "\" already in use");
        }

        Connector result = instantiateConnector(node, c);
        connectors.add(result);

        connectorIdGenerator.addId(localId);

        return result;
    }

    protected abstract Connector instantiateConnector(VNode node, Connector connector);

    @Override
    public Connector getMainInput(String type) {
        return mainInputs.get(type);
    }

    @Override
    public Connector getMainOutput(String type) {
        return mainOutputs.get(type);
    }

    @Override
    public Collection<String> getMainInputTypes() {
        return mainInputs.keySet();
    }

    @Override
    public Collection<String> getMainOutputTypes() {
        return mainOutputs.keySet();
    }

    @Override
    public Connector setMainInput(Connector connector) {
        mainInputs.put(connector.getType(), connector);
        return connector;
    }

    @Override
    public Connector setMainOutput(Connector connector) {
        mainOutputs.put(connector.getType(), connector);
        return connector;
    }

    @Override
    public Connector getConnector(String localId) {
        for (Connector c : connectors) {
            if (c.getLocalId().equals(localId)) {
                return c;
            }
        }

        return null;
    }

    @Override
    public ObservableList<Connector> getConnectors() {
        return this.connectors;
    }

    @Override
    public ObservableList<Connector> getInputs() {
        return this.unmodifiableInputs;
    }

    @Override
    public ObservableList<Connector> getOutputs() {
        return this.unmodifiableOutputs;
    }

    @Override
    public final boolean isSelected() {
        return selectedProperty().get();
    }

    @Override
    public final boolean requestSelection(boolean select) {

        if (!select) {
            selectedProperty.set(false);
        }

        if (isSelectable()) {
            selectedProperty.set(select);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final ReadOnlyBooleanProperty selectedProperty() {
        return this.selectedProperty;
    }

    @Override
    public final BooleanProperty selectableProperty() {
        return selectableProperty;
    }

    @Override
    public final boolean isSelectable() {
        return selectableProperty().get();
    }

    public final void setSelectable(boolean b) {
        selectableProperty().set(b);
    }

    @Override
    public boolean isVisualizationRequestInitialized() {
        return visualizationRequestProperty != null;
    }

    @Override
    public boolean removeConnector(Connector c) {
        return this.connectors.remove(c);
    }

    @Override
    public int getDepth() {
        VFlowModel parent = this.getFlow();

        int depth = 0;

        while (parent != null) {
            parent = parent.getFlow();
            depth++;
        }

        return depth;
    }

    @Override
    public FlowModel getRoot() {

        VFlowModel root = null;
        VFlowModel parent = this.getFlow();

        while (parent != null) {
            root = parent;
            parent = parent.getFlow();
        }

        return root;
    }
}

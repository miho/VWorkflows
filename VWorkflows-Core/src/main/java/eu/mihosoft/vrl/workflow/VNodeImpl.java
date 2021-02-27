/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
package eu.mihosoft.vrl.workflow;

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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class VNodeImpl implements VNode {

    private final ObservableList<Connector> connectors
            = FXCollections.observableArrayList();
    private ObservableList<Connector> inputs
            = FXCollections.observableArrayList();
    private ObservableList<Connector> outputs
            = FXCollections.observableArrayList();
    private final ObservableList<Connector> unmodifiableInputs
            = FXCollections.unmodifiableObservableList(inputs);
    private final ObservableList<Connector> unmodifiableOutputs
            = FXCollections.unmodifiableObservableList(outputs);
    private final StringProperty idProperty = new SimpleStringProperty();
    private final StringProperty titleProperty = new SimpleStringProperty();
    private final DoubleProperty xProperty = new SimpleDoubleProperty();
    private final DoubleProperty yProperty = new SimpleDoubleProperty();
    private final DoubleProperty widthProperty = new SimpleDoubleProperty();
    private final DoubleProperty heightProperty = new SimpleDoubleProperty();

    private final BooleanProperty selectedProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty selectableProperty = new SimpleBooleanProperty(true);

    private final ObjectProperty<ValueObject> valueObjectProperty
            = new SimpleObjectProperty<>();
    private VFlowModel flow;

    private IdGenerator connectorIdGenerator = new IdGeneratorImpl();
    private final Map<String, Connector> mainInputs = new HashMap<>();
    private final Map<String, Connector> mainOutputs = new HashMap<>();
    private ObjectProperty<VisualizationRequest> vReqProperty;

    public VNodeImpl(VFlowModel flow) {

        this.flow = flow;

        setWidth(200);
        setHeight(150);

        setTitle("Node");

        setValueObject(new DefaultValueObject(this));

        valueObjectProperty.addListener(new ChangeListener<ValueObject>() {
            @Override
            public void changed(ObservableValue<? extends ValueObject> ov, ValueObject t, ValueObject t1) {
                if (t1 != null) {
                    t1.setParent(VNodeImpl.this);
                }
            }
        });

        connectors.addListener(new ListChangeListener<Connector>() {
            @Override
            public void onChanged(Change<? extends Connector> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                            String action = "permutate"; // TODO: implement
                        }
                    }
//                    else if (change.wasUpdated()) {
//                        //TODO: update item
//
//                    } 
                    else if (change.wasRemoved()) {
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
//                                    System.out.println("added input:" + unmodifiableInputs.size());
                            }

                            if (connector.isOutput()) {
                                outputs.add(connector);
//                                    System.out.println("added output:" + unmodifiableOutputs.size());
                            }

                            if (connector instanceof ThruConnector) {
                                connector.setLocalId(connectorIdGenerator.newId("thru"));
                            } else {
                                connector.setLocalId(connectorIdGenerator.newId());
                            }
                        }
                    }
                }
            }
        });

//
//        outputs.addListener(new ListChangeListener<Connector<FlowNode>>() {
//            @Override
//            public void onChanged(Change<? extends Connector<FlowNode>> change) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        });
    }

//    @Override
//    public ObservableList<Connector<FlowNode>> getInputs() {
//        return inputs;
//    }
//
//    @Override
//    public ObservableList<Connector<FlowNode>> getOutputs() {
//        return outputs;
//    }
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

//    @Override
//    public ObservableList<VNode> getChildren() {
//        return children;
//    }
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

    /**
     * @return the vReq
     */
    @Override
    public VisualizationRequest getVisualizationRequest() {
        return visualizationRequestProperty().getValue();
    }

    /**
     * @param vReq the vReq to set
     */
    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        _visualizationRequestProperty().set(vReq);
    }

    private ObjectProperty<VisualizationRequest> _visualizationRequestProperty() {
        if (vReqProperty == null) {
            vReqProperty = new SimpleObjectProperty<>();
            vReqProperty.set(new VisualizationRequestImpl());
        }

        if (vReqProperty.get() == null) {
            vReqProperty.set(new VisualizationRequestImpl());
        }

        return vReqProperty;
    }

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        return _visualizationRequestProperty();
    }

//    @Override
//    public void setSkin(Skin<?> skin) {
//        skinProperty.set(skin);
//    }
//
//    @Override
//    public Skin<?> getSkin() {
//        return skinProperty.get();
//    }
//
//    @Override
//    public ObjectProperty<?> skinProperty() {
//        return skinProperty;
//    }
    /**
     * @return the flow
     */
    @Override
    public VFlowModel getFlow() {
        return flow;
    }

//    @Override
//    public void setOutput(boolean state, String type) {
//        if (state && !outputTypes.contains(type)) {
//            outputTypes.add(type);
//        } else if (!state) {
//            outputTypes.remove(type);
//        }
//    }
//
//    @Override
//    public void setInput(boolean state, String type) {
//        if (state && !inputTypes.contains(type)) {
//            inputTypes.add(type);
//        } else if (!state) {
//            inputTypes.remove(type);
//        }
//    }
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

    Connector addInput(VNode node, String type) {
        Connector c = new ConnectorImpl(
                node, type, null, true);
        connectors.add(c);
        return c;
    }

    Connector addOutput(VNode node, String type) {
        Connector c = new ConnectorImpl(
                node, type, null, false);
        connectors.add(c);
        return c;
    }

    ThruConnector addThruInput(VNode node, String type, VNode innerNode, Connector innerConnector) {
        ThruConnector c = new ThruConnectorImpl(
                node, type, null, true, innerNode, innerConnector);
        connectors.add(c);
        return c;
    }

    ThruConnector addThruOutput(VNode node, String type, VNode innerNode, Connector innerConnector) {
        ThruConnector c = new ThruConnectorImpl(
                node, type, null, false, innerNode, innerConnector);
        connectors.add(c);
        return c;
    }

    Connector addConnector(VNode node, Connector c) {
        String localId = c.getLocalId();

        if (connectorIdGenerator.getIds().contains(localId)) {
            throw new IllegalArgumentException(
                    "Cannot add connector: id \"" + localId + "\" already in use");
        }

        Connector result = new ConnectorImpl(node, c);
        connectors.add(result);

        connectorIdGenerator.addId(localId);

        return result;
    }

//    @Override
//    public boolean isInputOfType(String type) {
//        return inputTypes.contains(type);
//    }
//
//    @Override
//    public boolean isOutputOfType(String type) {
//        return outputTypes.contains(type);
//    }
//
//    @Override
//    public boolean isOutput() {
//        return !outputTypes.isEmpty();
//    }
//
//    @Override
//    public boolean isInput() {
//        return !inputTypes.isEmpty();
//    }
//    @Override
//    public String getGlobalId() {
//       String id = getId();
//       
//       if (getFlow() ==null) {
//           return id;
//       }
//       
//       FlowNode parent = getFlow();
//       
//       while (parent.getFlow()!=null) {
//           id = parent.getGlobalId() + ":" + id;
//           parent = parent.getFlow();
//       }
//       
//       return id;
//    }
//    @Override
//    public ObservableList<String> getInputTypes() {
//        return inputTypes;
//    }
//
//    @Override
//    public ObservableList<String> getOutputTypes() {
//        return outputTypes;
//    }
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
        return vReqProperty != null;
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

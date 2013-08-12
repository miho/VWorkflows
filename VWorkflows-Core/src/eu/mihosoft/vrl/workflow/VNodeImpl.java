/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
class VNodeImpl implements VNode {

    private ObservableList<Connector> connectors =
            FXCollections.observableArrayList();
    private ObservableList<Connector> inputs =
            FXCollections.observableArrayList();
    private ObservableList<Connector> outputs =
            FXCollections.observableArrayList();
    private ObservableList<Connector> unmodifiableInputs =
            FXCollections.unmodifiableObservableList(inputs);
    private ObservableList<Connector> unmodifiableOutputs =
            FXCollections.unmodifiableObservableList(outputs);
    private StringProperty idProperty = new SimpleStringProperty();
    private StringProperty titleProperty = new SimpleStringProperty();
    private DoubleProperty xProperty = new SimpleDoubleProperty();
    private DoubleProperty yProperty = new SimpleDoubleProperty();
    private DoubleProperty widthProperty = new SimpleDoubleProperty();
    private DoubleProperty heightProperty = new SimpleDoubleProperty();
    private ObjectProperty<ValueObject> valueObjectProperty =
            new SimpleObjectProperty<>();
    private VisualizationRequest vReq;
    private VFlowModel flow;
//    private ObservableList<String> inputTypes = FXCollections.observableArrayList();
//    private ObservableList<String> outputTypes = FXCollections.observableArrayList();
    private IdGenerator connectorIdGenerator = new IdGeneratorImpl();
    private Map<String, Connector> mainInputs = new HashMap<>();
    private Map<String, Connector> mainOutputs = new HashMap<>();

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
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else {
                        if (change.wasRemoved()) {
                            for (Connector connector : change.getRemoved()) {
                                if (connector.isInput()) {
                                    inputs.remove(connector);
                                }

                                if (connector.isOutput()) {
                                    outputs.remove(connector);
                                }
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

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return vReq;
    }

    /**
     * @param vReq the vReq to set
     */
    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vReq = vReq;
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
       return this.addOutput(this,type);
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

    Connector addConnector(VNode node, Connector c) {
         String localId = c.getLocalId();

        if (connectorIdGenerator.getIds().contains(localId)) {
            throw new IllegalArgumentException(
                    "Cannot add connector: id \"" + localId + "\" already in use");
        }

        Connector result = new ConnectorImpl(node,c);
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
    public void setMainInput(Connector connector) {
        mainInputs.put(connector.getType(), connector);
    }

    @Override
    public void setMainOutput(Connector connector) {
        mainOutputs.put(connector.getType(), connector);
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

    
}

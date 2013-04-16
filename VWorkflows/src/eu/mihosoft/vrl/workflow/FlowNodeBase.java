/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FlowNodeBase implements FlowNode {

//    private ObservableList<Connector<FlowNode>> inputs =
//            FXCollections.observableArrayList();
//    private ObservableList<Connector<FlowNode>> outputs =
//            FXCollections.observableArrayList();
    private ObservableList<FlowNode> children =
            FXCollections.observableArrayList();
    private StringProperty idProperty = new SimpleStringProperty();
    private StringProperty titleProperty = new SimpleStringProperty();
    private DoubleProperty xProperty = new SimpleDoubleProperty();
    private DoubleProperty yProperty = new SimpleDoubleProperty();
    private DoubleProperty widthProperty = new SimpleDoubleProperty();
    private DoubleProperty heightProperty = new SimpleDoubleProperty();
    private ObjectProperty<NodeValueObject> valueObjectProperty =
            new SimpleObjectProperty<>();
    private ObjectProperty<VisualizationRequest> vReq = new SimpleObjectProperty<>();
    private FlowFlowNode flow;
    private BooleanProperty outputProperty = new SimpleBooleanProperty(true);
    private BooleanProperty inputProperty = new SimpleBooleanProperty();
    
    private ObservableList<Connector> inputs = FXCollections.observableArrayList();
    private ObservableList<Connector> outputs = FXCollections.observableArrayList();
    
    
    public FlowNodeBase(FlowFlowNode flow) {

        this.flow = flow;

        setValueObject(new EmptyValueObject());

//        inputs.addListener(new ListChangeListener<Connector<FlowNode>>() {
//            @Override
//            public void onChanged(Change<? extends Connector<FlowNode>> change) {
//                while (change.next()) {
//                    if (change.wasPermutated()) {
//                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
//                            //permutate
//                        }
//                    } else if (change.wasUpdated()) {
//                        //update item
//                    } else {
//                        if (change.wasRemoved()) {
//                            for (Connector<FlowNode> connector : change.getRemoved()) {
//                                //
//                            }
//                        } else if (change.wasAdded()) {
//                            for (Connector<FlowNode> connector : change.getAddedSubList()) {
//                            }
//                        }
//                    }
//                }
//            }
//        });
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
    public void setTitle(String title) {
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
    public void setWidth(double w) {
        widthProperty.set(w);
    }

    @Override
    public void setHeight(double h) {
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
    public ObservableList<FlowNode> getChildren() {
        return children;
    }

    @Override
    public NodeValueObject getValueObject() {
        return valueObjectProperty.get();
    }

    @Override
    public final void setValueObject(NodeValueObject o) {
        valueObjectProperty.set(o);
    }

    @Override
    public ObjectProperty<NodeValueObject> valueObjectProperty() {
        return valueObjectProperty;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return this.vReq.get();
    }

    /**
     * @param vReq the vReq to set
     */
    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vReq.set(vReq);
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
    public FlowFlowNode getFlow() {
        return flow;
    }

    /**
     * @return the outputProperty
     */
    @Override
    public BooleanProperty outputProperty() {
        return outputProperty;
    }

    /**
     * @return the inputProperty
     */
    @Override
    public BooleanProperty inputProperty() {
        return inputProperty;
    }

    @Override
    public void setOutput(boolean state) {
        outputProperty.set(state);
    }

    @Override
    public void setInput(boolean state) {
        inputProperty.set(state);
    }

    @Override
    public boolean isOutput() {
        return outputProperty.get();
    }

    @Override
    public boolean isInput() {
        return inputProperty.get();
    }

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

    @Override
    public Connector newInput(String event) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO implement
    }

    @Override
    public Connector newInput(String myId, String control) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO implement
    }

    @Override
    public Connector newOutput(String control) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO implement
    }

    @Override
    public Connector newOutput(String myId, String control) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO implement
    }

    @Override
    public ObjectProperty<VisualizationRequest> visualizationRequestProperty() {
        return this.vReq;
    }


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowNodeBase implements FlowNode {

    private ObservableList<Connector<FlowNode>> inputs =
            FXCollections.observableArrayList();
    private ObservableList<Connector<FlowNode>> outputs =
            FXCollections.observableArrayList();
    private ObservableList<FlowNode> children =
            FXCollections.observableArrayList();
    private StringProperty idProperty = new SimpleStringProperty();
    private StringProperty titleProperty = new SimpleStringProperty();
    private DoubleProperty xProperty = new SimpleDoubleProperty();
    private DoubleProperty yProperty = new SimpleDoubleProperty();
    private DoubleProperty widthProperty = new SimpleDoubleProperty();
    private DoubleProperty heightProperty = new SimpleDoubleProperty();
    private ObjectProperty<ValueObject> valueObjectProperty =
            new SimpleObjectProperty<ValueObject>();

    public FlowNodeBase() {

        inputs.addListener(new ListChangeListener<Connector<FlowNode>>() {
            @Override
            public void onChanged(Change<? extends Connector<FlowNode>> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else {
                        if (change.wasRemoved()) {
                            for (Connector<FlowNode> connector : change.getRemoved()) {
                                //
                            }
                        } else if (change.wasAdded()) {
                            for (Connector<FlowNode> connector : change.getAddedSubList()) {
                            }
                        }
                    }
                }
            }
        });

        outputs.addListener(new ListChangeListener<Connector<FlowNode>>() {
            @Override
            public void onChanged(Change<? extends Connector<FlowNode>> change) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

    }

    @Override
    public ObservableList<Connector<FlowNode>> getInputs() {
        return inputs;
    }

    @Override
    public ObservableList<Connector<FlowNode>> getOutputs() {
        return outputs;
    }

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
    public ValueObject getValueObject() {
        return valueObjectProperty.get();
    }

    protected void setValueObject(ValueObject o) {
        valueObjectProperty.set(o);
    }
    
    protected ObjectProperty<ValueObject> valueObjectProperty() {
        return valueObjectProperty;
    }
}

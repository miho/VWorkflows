/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

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
abstract class FlowNodeBase<T> implements FlowNode<T> {

    private ObservableList<Connector<T>> inputs =
            FXCollections.observableArrayList();
    private ObservableList<Connector<T>> outputs =
            FXCollections.observableArrayList();

    public FlowNodeBase() {

        inputs.addListener(new ListChangeListener<Connector<T>>() {
            @Override
            public void onChanged(Change<? extends Connector<T>> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else {
                        if (change.wasRemoved()) {
                            for (Connector<T> connector : change.getRemoved()) {
                                //
                            }
                        } else if (change.wasAdded()) {
                            for (Connector<T> connector : change.getAddedSubList()) {
                                
                            }
                        }
                    }
                }
            }
        });

        outputs.addListener(new ListChangeListener<Connector<T>>() {
            @Override
            public void onChanged(Change<? extends Connector<T>> change) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

    }

    @Override
    public ObservableList<Connector<T>> getInputs() {
        return inputs;
    }

    @Override
    public ObservableList<Connector<T>> getOutputs() {
        return outputs;
    }
}

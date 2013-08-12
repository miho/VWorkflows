/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.FlowModelImpl;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXFlowNodeSkin;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Custom flownode skin. In addition to the basic node visualization from
 * VWorkflows this skin adds custom visualization of value objects.
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class FXFlowNodeSkinBase extends FXFlowNodeSkin {

    private ChangeListener<Object> valueChangeListener;

    /**
     * Constructor.
     *
     * @param skinFactory skin factory that created this skin
     * @param model node model that shall be visualized
     * @param controller parent flow controller
     */
    public FXFlowNodeSkinBase(FXSkinFactory skinFactory,
            VNode model, VFlow controller) {
        super(skinFactory, skinFactory.getFxParent(), model, controller);

        init();
    }

    /**
     * Initializes this node skin.
     */
    private void init() {

        // update the view (uses value object of the model)
        updateView();

        valueChangeListener = new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> ov, Object t, Object t1) {
                updateView();
            }
        };

        // registers listener to update view if new value object has been defined
        getModel().getValueObject().valueProperty().addListener(valueChangeListener);
    }

    public void updateView() {
        // no default implementation
    }
    
    protected void updateViewSample() {
        // we don't create custom view for flows
        if (getModel() instanceof VFlowModel) {
            return;
        }

        // we don't create a custom view if no value has been defined
        if (getModel().getValueObject().getValue() == null) {
            return;
        }

        StackPane nodePane = new StackPane();

        nodePane.getChildren().add(new Button(getModel().getValueObject().getValue().toString()));

        getNode().setContentPane(nodePane);
    }

    @Override
    public void remove() {
        
        // we remove the listener since we are going to be removed from the scene graph
        getModel().getValueObject().valueProperty().removeListener(valueChangeListener);
        
        super.remove();
    }
}

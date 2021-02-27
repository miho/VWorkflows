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
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Custom flownode skin. In addition to the basic node visualization from
 * VWorkflows this skin adds custom visualization of value objects.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
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
//        updateView();
        valueChangeListener = (
                ObservableValue<? extends Object> ov, Object t, Object t1) -> {
                    updateView();
                };

        // registers listener to update view if new value object has been defined
        getModel().getValueObject().valueProperty().
                addListener(valueChangeListener);
    }

    @Override
    public void add() {
        super.add();
        updateView();
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

        nodePane.getChildren().add(new Button(getModel().getValueObject().
                getValue().toString()));

        getNode().setContentPane(nodePane);
    }

    @Override
    public void remove() {

        // we remove the listener since we are going to be removed from the scene graph
        getModel().getValueObject().valueProperty().removeListener(valueChangeListener);

        if (getNode() instanceof FlowNodeWindow) {
            FlowNodeWindow window = (FlowNodeWindow) getNode();
            window.onRemovedFromSceneGraph();
        }

        super.remove();
    }
}

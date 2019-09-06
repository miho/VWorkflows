package com.gluonhq.vworkflows.views.helper;

import com.gluonhq.charm.glisten.control.DropdownButton;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;

public class FunctionFlowNodeSkin extends CustomFXFlowNodeSkin {

    public FunctionFlowNodeSkin(FXSkinFactory skinFactory, VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    protected Node createView() {
        DropdownButton button = new DropdownButton();
        for (FunctionValue.FUNCTION op : FunctionValue.FUNCTION.values()) {
            MenuItem item = new MenuItem(op.getFunction());
            item.getProperties().put("FUNCTION", op);
            button.getItems().add(item);
        }
        button.selectedItemProperty().addListener((obs, ov, nv) ->
                ((FunctionValue) getModel().getValueObject().getValue())
                        .setValue((FunctionValue.FUNCTION) nv.getProperties().get("FUNCTION")));
        return button;
    }
}
package com.gluonhq.vworkflows.views.helper;

import com.gluonhq.charm.glisten.control.DropdownButton;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;

public class OperatorFlowNodeSkin extends CustomFXFlowNodeSkin {

    public OperatorFlowNodeSkin(FXSkinFactory skinFactory, VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    protected Node createView() {
        DropdownButton button = new DropdownButton();
        for (OperatorValue.OPERATOR op : OperatorValue.OPERATOR.values()) {
            MenuItem item = new MenuItem(op.getOperation());
            item.getProperties().put("OPERATOR", op);
            button.getItems().add(item);
        }
        button.selectedItemProperty().addListener((obs, ov, nv) ->
                ((OperatorValue) getModel().getValueObject().getValue())
                        .setValue((OperatorValue.OPERATOR) nv.getProperties().get("OPERATOR")));
        return button;
    }
}
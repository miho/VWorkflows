package com.gluonhq.vworkflows.views.helper;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.scene.Node;
import javafx.scene.control.Slider;

public class DoubleFlowNodeSkin extends CustomFXFlowNodeSkin {

    public DoubleFlowNodeSkin(FXSkinFactory skinFactory, VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    protected Node createView() {
        Slider slider = new Slider(0, 100, 50);
        ((DoubleValue) getModel().getValueObject().getValue()).valueProperty().bind(slider.valueProperty());
        return slider;
    }

}
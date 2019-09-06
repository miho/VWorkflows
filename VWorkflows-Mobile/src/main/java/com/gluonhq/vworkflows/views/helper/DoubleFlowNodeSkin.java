package com.gluonhq.vworkflows.views.helper;

import java.text.DecimalFormat;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import javafx.beans.binding.Bindings;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import javafx.scene.control.TextFormatter;

public class DoubleFlowNodeSkin extends CustomFXFlowNodeSkin {

    public DoubleFlowNodeSkin(FXSkinFactory skinFactory, VNode model, VFlow controller) {
        super(skinFactory, model, controller);
    }

    @Override
    protected Node createView() {
        TextField tf = new TextField("1.0");
        tf.setStyle("-fx-background-color: rgba(30,100,180,0.3);-fx-text-fill: white;");

        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();

            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
            
        };
        
        StringConverter<Number> converter = new StringConverter<Number>() {
        
            @Override
            public Number fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0 ;
                } else {
                    return Double.valueOf(s);
                }
            }
        
            @Override
            public String toString(Number d) {
                return d.toString();
            }
        };
        
        TextFormatter<Number> textFormatter = new TextFormatter<Number>(converter, 0.0, filter);
        tf.setTextFormatter(textFormatter);
        
        Bindings.bindBidirectional(tf.textProperty(), ((DoubleValue) getModel().getValueObject().getValue()).valueProperty(), converter);

        return tf;
    }

}
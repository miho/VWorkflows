/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.fxwindows.VFXNodeUtils;
import eu.mihosoft.vrl.fxwindows.Window;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXFlowNodeSkin
implements FXSkin<FlowNode, Window>, FlowNodeSkin<FlowNode> {
    
    private FlowNode model;
//    private ObjectProperty<V> flowProperty = new SimpleObjectProperty<>();
    private Window node;
    
    public FXFlowNodeSkin(FlowNode model) {
        this.model = model;
//        setFlow(flow);
        init();
    }
    
    private void init() {
        node = new Window();
        
        node.setTitle(model.getTitle());
        node.setLayoutX(model.getX());
        node.setLayoutY(model.getY());
        node.setPrefWidth(model.getWidth());
        node.setPrefHeight(model.getHeight());
        
        model.titleProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
                node.setTitle(newVal);
            }
        });
        
        model.xProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setLayoutX((double)newVal);
            }
        });
        
        model.yProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setLayoutY((double)newVal);
            }
        });
        
        model.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setPrefWidth((double)newVal);
            }
        });
        
        model.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setPrefHeight((double)newVal);
            }
        });
        
        node.layoutXProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                model.setX((double)newVal);
            }
        });
        
        node.layoutYProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                model.setY((double)newVal);
            }
        });
        
        node.prefWidthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                model.setWidth((double)newVal);
            }
        });
        
        node.prefHeightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                model.setHeight((double)newVal);
            }
        });
        
//        node.titleProperty().bind(model.titleProperty());
        
//        node.layoutXProperty().bind(model.xProperty());
//        node.layoutYProperty().bind(model.yProperty());
//        
//        node.prefWidthProperty().bind(model.widthProperty());
//        node.prefHeightProperty().bind(model.heightProperty());
    }

    @Override
    public Window getNode() {
        return node;
    }

//    @Override
//    public final void setFlow(V flow) {
//        flowProperty.set(flow);
//    }
//
//    @Override
//    public final V getFlow() {
//        return flowProperty.get();
//    }
//    
//    @Override
//    public ObjectProperty<V> flowProperty() {
//        return flowProperty;
//    }

    @Override
    public void remove() {
        VFXNodeUtils.removeFromParent(node);
    }
}

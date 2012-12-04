/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.fxwindows.VFXNodeUtils;
import eu.mihosoft.vrl.fxwindows.Window;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Flow;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeSkin;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXFlowNodeSkin
        implements FXSkin<FlowNode, Window>, FlowNodeSkin<FlowNode> {

    private ObjectProperty<FlowNode> modelProperty = new SimpleObjectProperty<>();
//    private ObjectProperty<Flow> flowProperty = new SimpleObjectProperty<>();
    private Window node;
    private ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>();
    private ChangeListener<String> modelTitleListener;
    private ChangeListener<Number> modelXListener;
    private ChangeListener<Number> modelYListener;
    private ChangeListener<Number> modelWidthListener;
    private ChangeListener<Number> modelHeightListener;
    private ChangeListener<Number> nodeXListener;
    private ChangeListener<Number> nodeYListener;
    private ChangeListener<Number> nodeWidthListener;
    private ChangeListener<Number> nodeHeightListener;
    private Node output;

    public FXFlowNodeSkin(Parent parent, FlowNode model) {

        setParent(parent);
        setModel(model);

        init();
    }

    private void init() {
        node = new FlowNodeWindow(this);

        node.setTitle(getModel().getTitle());
        node.setLayoutX(getModel().getX());
        node.setLayoutY(getModel().getY());
        node.setPrefWidth(getModel().getWidth());
        node.setPrefHeight(getModel().getHeight());

        registerListeners(getModel());

        modelProperty.addListener(new ChangeListener<FlowNode>() {
            @Override
            public void changed(ObservableValue<? extends FlowNode> ov, FlowNode oldVal, FlowNode newVal) {

                removeListeners(oldVal);
                registerListeners(newVal);
            }
        });

        if (getModel().isOutput()) {
            addOutputConnector();
        }

        getModel().outputProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
                if (newVal) {
                    addOutputConnector();
                } else {
                    removeOutputConnector();
                }
            }
        });

    }

    private void addOutputConnector() {
        DoubleBinding startXBinding = new DoubleBinding() {
            {
                super.bind(node.layoutXProperty(), node.widthProperty());
            }

            @Override
            protected double computeValue() {
                return node.getLayoutX() + node.getWidth();
            }
        };

        DoubleBinding startYBinding = new DoubleBinding() {
            {
                super.bind(node.layoutYProperty(), node.heightProperty());
            }

            @Override
            protected double computeValue() {
                return node.getLayoutY() + node.getHeight() / 2;
            }
        };

        Circle circle = new Circle(20);

        output = circle;

        output.layoutXProperty().bind(startXBinding);
        output.layoutYProperty().bind(startYBinding);

        VFXNodeUtils.addToParent(getParent(), output);

        output.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                if (getModel().getFlow().getConnections("control").
                        isOutputConnected(getModel().getId())) {
                    return;
                }

                FXNewConnectionSkin newConnectionSkin =
                        new FXNewConnectionSkin(
                        getParent(), getModel(),
                        getModel().getFlow(), "control");

                newConnectionSkin.add();
            }
        });
    }

    private void removeOutputConnector() {
        if (output != null) {
            VFXNodeUtils.removeFromParent(output);
        }
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
        getModel().getFlow().remove(getModel());
    }

    @Override
    public final void setModel(FlowNode model) {
        modelProperty.set(model);
    }

    @Override
    public final FlowNode getModel() {
        return modelProperty.get();
    }

    @Override
    public final ObjectProperty<FlowNode> modelProperty() {
        return modelProperty;
    }

    final void setParent(Parent parent) {
        parentProperty.set(parent);
    }

    Parent getParent() {
        return parentProperty.get();
    }

    ObjectProperty<Parent> parentProperty() {
        return parentProperty;
    }

    @Override
    public void add() {
        VFXNodeUtils.addToParent(getParent(), node);
    }

    private void removeListeners(FlowNode flowNode) {
        flowNode.titleProperty().removeListener(modelTitleListener);
        flowNode.xProperty().removeListener(modelXListener);
        flowNode.yProperty().removeListener(modelYListener);
        flowNode.widthProperty().removeListener(modelWidthListener);
        flowNode.heightProperty().removeListener(modelHeightListener);

        node.layoutXProperty().removeListener(nodeXListener);
        node.layoutYProperty().removeListener(nodeXListener);
        node.prefWidthProperty().removeListener(nodeWidthListener);
        node.prefHeightProperty().removeListener(nodeHeightListener);
    }

    private void initListeners() {
        modelTitleListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
                node.setTitle(newVal);
            }
        };

        modelXListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setLayoutX((double) newVal);
            }
        };

        modelYListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setLayoutY((double) newVal);
            }
        };

        modelWidthListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setPrefWidth((double) newVal);
            }
        };

        modelHeightListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                node.setPrefHeight((double) newVal);
            }
        };

        nodeXListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                getModel().setX((double) newVal);
            }
        };

        nodeYListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                getModel().setY((double) newVal);
            }
        };

        nodeWidthListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                getModel().setWidth((double) newVal);
            }
        };

        nodeHeightListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                getModel().setHeight((double) newVal);
            }
        };
    }

    private void registerListeners(FlowNode flowNode) {

        initListeners();

        flowNode.titleProperty().addListener(modelTitleListener);
        flowNode.xProperty().addListener(modelXListener);
        flowNode.yProperty().addListener(modelYListener);
        flowNode.widthProperty().addListener(modelWidthListener);
        flowNode.heightProperty().addListener(modelHeightListener);

        node.layoutXProperty().addListener(nodeXListener);
        node.layoutYProperty().addListener(nodeYListener);
        node.prefWidthProperty().addListener(nodeWidthListener);
        node.prefHeightProperty().addListener(nodeHeightListener);

    }
}

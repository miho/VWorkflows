/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowNode;
import eu.mihosoft.vrl.workflow.FlowNodeSkin;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import jfxtras.labs.scene.control.window.Window;
import jfxtras.labs.util.NodeUtil;

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
    private FXNewConnectionSkin newConnectionSkin;

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
        
        circle.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.2));
        circle.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
        circle.setStrokeWidth(3);

        output = circle;

        output.layoutXProperty().bind(startXBinding);
        output.layoutYProperty().bind(startYBinding);

        NodeUtil.addToParent(getParent(), output);

        output.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                output.toFront();
            }
        });

        output.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

//                if (getModel().getFlow().getConnections("control").
//                        isOutputConnected(getModel().getId())) {
//                    return;
//                }

                newConnectionSkin =
                        new FXNewConnectionSkin(
                        getParent(), getModel(),
                        getModel().getFlow(), "control");

                newConnectionSkin.add();

                t.consume();
                MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);
            }
        });

        output.onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                t.consume();
                MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);

            }
        });

        output.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                t.consume();
                MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);
                output.toBack();
            }
        });

        output.onMouseExitedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                output.toBack();
            }
        });
    }

//    private void delegateEventsTo(Node src, final Node target) {
//        // link: https://forums.oracle.com/forums/thread.jspa?threadID=2425879
//        EventHandler<MouseEvent> interceptHandler = new EventHandler<MouseEvent>() {
//            //is the fixed copy fired already - controls the recursion
//            boolean isEventCopyFired = false;
//
//            @Override
//            public void handle(MouseEvent e) {
//                try {
//                    if (!isEventCopyFired) {
//                        isEventCopyFired = true; //prevents further recursion
//                        //copy the original event but set the appropriate isShiftDown and isControlDown flags
//                        MouseEvent copy = copyEvent(e);
//
//                        MouseEvent.fireEvent(target, copy); //this causes a recursion 
//
//                        e.consume(); //consume the original event, because we fired a copy
//
//                        //turn off the flag for the next event
//                        isEventCopyFired = false;
//                    }
//                } catch (Exception ex) {//impl_ methods can be removed in the future, ensure we don't break event handling 
//                    ex.printStackTrace(System.err);
//                }
//            }
//        };
//        //mouse pressed events deal with selection of items
//        src.addEventFilter(MouseEvent.MOUSE_PRESSED, interceptHandler);
//        src.addEventFilter(MouseEvent.MOUSE_DRAGGED, interceptHandler);
//    }

//    private MouseEvent copyEvent(MouseEvent e) {
//        MouseEvent copy = MouseEvent.impl_mouseEvent(
//                e.getSceneX(), e.getSceneY(),
//                e.getScreenX(), e.getScreenY(),
//                e.getButton(), e.getClickCount(),
//                e.isShiftDown(), e.isControlDown(),
//                e.isAltDown(), e.isMetaDown(),
//                MouseEvent.impl_getPopupTrigger(e),
//                e.isPrimaryButtonDown(),
//                e.isMiddleButtonDown(),
//                e.isSecondaryButtonDown(),
//                e.isSynthesized(),
//                (EventType<MouseEvent>) e.getEventType());
//        return copy;
//    }

    private void removeOutputConnector() {
        if (output != null) {
            NodeUtil.removeFromParent(output);
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
        removeOutputConnector();
        node.close();
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
        NodeUtil.addToParent(getParent(), node);
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

        node.onCloseActionProperty().set(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                modelProperty().get().getFlow().remove(modelProperty().get());
            }
        });
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

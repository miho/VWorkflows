/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VNodeSkin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
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
        implements FXSkin<VNode, Window>, VNodeSkin<VNode> {

    private ObjectProperty<VNode> modelProperty = new SimpleObjectProperty<>();
//    private ObjectProperty<Flow> flowProperty = new SimpleObjectProperty<>();
    private FlowNodeWindow node;
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
//    private Node output;
    private FXNewConnectionSkin newConnectionSkin;
    private boolean removeSkinOnly = false;
    private VFlow controller;
    private Map<String, Node> connectors = new HashMap<>();
    private List<Node> inputList = new ArrayList<>();
    private List<Node> outputList = new ArrayList<>();
    private FXSkinFactory skinFactory;

    public FXFlowNodeSkin(FXSkinFactory skinFactory, Parent parent, VNode model, VFlow controller) {
        this.skinFactory = skinFactory;
        setParent(parent);
        setModel(model);

        this.controller = controller;

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

        modelProperty.addListener(new ChangeListener<VNode>() {
            @Override
            public void changed(ObservableValue<? extends VNode> ov, VNode oldVal, VNode newVal) {

                removeListeners(oldVal);
                registerListeners(newVal);
            }
        });

        for (Connector connector : getModel().getConnectors()) {
            addConnector(connector);
        }

        getModel().getConnectors().addListener(new ListChangeListener<Connector>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Connector> change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        for (int i = change.getFrom(); i < change.getTo(); ++i) {
                            //permutate
                        }
                    } else if (change.wasUpdated()) {
                        //update item
                    } else if (change.wasRemoved()) {
                        // removed
                        for (Connector connector : change.getRemoved()) {
                            removeConnector(connector);
                        }
                    } else if (change.wasAdded()) {
                        // added
                        for (Connector connector : change.getAddedSubList()) {
                            addConnector(connector);
                        }
                    }

                } // end while change.next()
            }
        });

    }

    private void addConnector(final Connector connector) {

        Circle circle = new Circle(20);


        switch (connector.getType()) {
            case "control":
                circle.setFill(new Color(1.0, 1.0, 0.0, 0.75));
                circle.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
                break;
            case "data":
                circle.setFill(new Color(0.1, 0.1, 0.1, 0.5));
                circle.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
                break;
            case "event":
                circle.setFill(new Color(255.0 / 255.0, 100.0 / 255.0, 1, 0.5));
                circle.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
                break;
        }

        circle.setStrokeWidth(3);

        final Circle connectorNode = circle;

        connectors.put(connector.getId(), connectorNode);

        if (connector.isInput()) {
            inputList.add(connectorNode);
        }
        if (connector.isOutput()) {
            outputList.add(connectorNode);
        }

        DoubleBinding startXBinding = new DoubleBinding() {
            {
                super.bind(node.layoutXProperty(), node.widthProperty());
            }

            @Override
            protected double computeValue() {
                double posX = node.getLayoutX();

                if (connector.isOutput()) {
                    posX += node.getWidth();
                }

                return posX;
            }
        };

        DoubleBinding startYBinding = new DoubleBinding() {
            {
                super.bind(node.layoutYProperty(), node.heightProperty());
            }

            @Override
            protected double computeValue() {

                double connectorHeight = connectorNode.getRadius() * 2;
                double gap = 5;

                double numConnectors = inputList.size();
                int connectorIndex = inputList.indexOf(connectorNode);


                if (connector.isOutput()) {
                    numConnectors = outputList.size();
                    connectorIndex = outputList.indexOf(connectorNode);
                }

                double totalHeight = numConnectors * connectorHeight + (numConnectors - 1) * gap;

                double midPointOfNode = node.getLayoutY() + node.getHeight() / 2;

                double startY = midPointOfNode - totalHeight / 2;

                double y = startY + (connectorHeight + gap) * connectorIndex + (connectorHeight + gap) / 2;

                return y;
            }
        };

        connectorNode.layoutXProperty().bind(startXBinding);
        connectorNode.layoutYProperty().bind(startYBinding);

        node.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                adjustConnectorSize(connector, connectorNode, newValue);
            }
        });

        NodeUtil.addToParent(getParent(), connectorNode);

        connectorNode.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                connectorNode.toFront();
            }
        });

        connectorNode.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

//                if (getModel().getFlow().getConnections("control").
//                        isOutputConnected(getModel().getId())) {
//                    return;
//                }

                if (connector.isOutput()) {

                    newConnectionSkin =
                            new FXNewConnectionSkin(getSkinFactory(),
                            getParent(), connector, getController(), connector.getType());

                    newConnectionSkin.add();

                    t.consume();
                    MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);
                }
            }
        });

        connectorNode.onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                t.consume();
                MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);

            }
        });

        connectorNode.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                t.consume();
                try {
                    MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);
                } catch (Exception ex) {
                    // TODO exception is not critical here (node already removed)
                }
                connectorNode.toBack();
            }
        });

        connectorNode.onMouseExitedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                connectorNode.toBack();
            }
        });



    }

    private void adjustConnectorSize(Connector conector, Circle connectorNode, Bounds newValue) {
        connectorNode.setRadius(computeConnectorHeight(conector, connectorNode, newValue) / 2);
    }

    private double computeConnectorHeight(Connector connector, Circle connectorNode, Bounds newValue) {
        double connectorHeight = connectorNode.getRadius() * 2;
        double gap = 5;

        int numConnectors = inputList.size();


        if (connector.isOutput()) {
            numConnectors = outputList.size();
        }

        double totalHeight = numConnectors * connectorHeight + (numConnectors - 1) * gap;

        connectorHeight = Math.min(totalHeight, newValue.getHeight() - 80) / (numConnectors);
        connectorHeight = Math.min(connectorHeight, 20 * 2);

        return connectorHeight;
    }

    private void removeConnector(Connector connector) {
        Node connectorNode = connectors.remove(connector.getId());

        if (connectorNode != null && connectorNode.getParent() != null) {
            if (connector.isInput()) {
                inputList.remove(connectorNode);
            } else if (connector.isOutput()) {
                outputList.remove(connectorNode);
            }
            NodeUtil.removeFromParent(connectorNode);
        }
    }

    @Override
    public Window getNode() {
        return node;
    }

    @Override
    public Parent getContentNode() {
        return node.getWorkflowContentPane();
    }

    @Override
    public void remove() {
        removeSkinOnly = true;

        Set<String> keySet = new HashSet<>(connectors.keySet());

        for (String id : keySet) {
            removeConnector(getModel().getFlow().getNodeLookup().getConnectorById(id));
        }
        if (node != null && node.getParent() != null) {
            NodeUtil.removeFromParent(node);
        }
    }

    @Override
    public final void setModel(VNode model) {
        modelProperty.set(model);
    }

    @Override
    public final VNode getModel() {
        return modelProperty.get();
    }

    @Override
    public final ObjectProperty<VNode> modelProperty() {
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

    private void removeListeners(VNode flowNode) {
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
                if (!removeSkinOnly) {
                    modelProperty().get().getFlow().remove(modelProperty().get());
                }
            }
        });
    }

    private void registerListeners(VNode flowNode) {

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

    /**
     * @return the controller
     */
    @Override
    public VFlow getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    @Override
    public void setController(VFlow controller) {
        this.controller = controller;
    }

    /**
     * @return the skinFactory
     */
    @Override
    public FXSkinFactory getSkinFactory() {
        return this.skinFactory;
    }

    public Node getConnectorById(String id) {
        return connectors.get(id);
    }
}

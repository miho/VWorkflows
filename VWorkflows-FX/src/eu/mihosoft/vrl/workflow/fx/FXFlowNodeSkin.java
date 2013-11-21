/*
 * FXFlowNodeSkin.java
 * 
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VNodeSkin;
import java.util.ArrayList;
import java.util.Collection;
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
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import jfxtras.labs.scene.control.window.Window;
import jfxtras.labs.util.NodeUtil;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FXFlowNodeSkin
        implements FXSkin<VNode, Window>, VNodeSkin<VNode> {

    private ObjectProperty<VNode> modelProperty = new SimpleObjectProperty<>();
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
    VFlow controller;
    Map<Connector, Shape> connectors = new HashMap<>();
    List<Circle> inputList = new ArrayList<>();
    List<Circle> outputList = new ArrayList<>();
    private FXSkinFactory skinFactory;
    private double inputConnectorSize;
    private double outputConnectorSize;
    private List<Connector> connectorList = new ArrayList<>();

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


//        skinFactory.connectionFillColorTypes().addListener(new MapChangeListener<String, Color>() {
//            @Override
//            public void onChanged(MapChangeListener.Change<? extends String, ? extends Color> change) {
//                if (change.wasAdded()) {
//                    for (Connector connector : getModel().getConnectors()) {
//
//                        if (connector.getType().equals(change.getKey())) {
//                            Shape cN = connectors.get(connector.getId());
//                            cN.setFill(change.getValueAdded());
//                        }
//                    }
//                }
//            }
//        });
//
//        skinFactory.connectionStrokeColorTypes().addListener(new MapChangeListener<String, Color>() {
//            @Override
//            public void onChanged(MapChangeListener.Change<? extends String, ? extends Color> change) {
//                if (change.wasAdded()) {
//                    for (Connector connector : getModel().getConnectors()) {
//
//                        if (connector.getType().equals(change.getKey())) {
//                            Shape cN = connectors.get(connector.getId());
//                            cN.setStroke(change.getValueAdded());
//                        }
//                    }
//                }
//            }
//        });
    }

    private void addConnector(final Connector connector) {
        connectorList.add(connector);
        ConnectorCircle circle = new ConnectorCircle(controller, getSkinFactory(), connector, 20);

//        Color fillColor = skinFactory.getConnectionFillColor(connector.getType());
//        Color strokeColor = skinFactory.getConnectionStrokeColor(connector.getType());
//
//        if (fillColor == null) {
//            fillColor = new Color(0.1, 0.1, 0.1, 0.5);
//        }
//
//        if (strokeColor == null) {
//            strokeColor = new Color(120 / 255.0, 140 / 255.0, 1, 0.42);
//        }
//
//        
//        circle.setFill(fillColor);
//        circle.setStroke(strokeColor);
//
//        circle.setStrokeWidth(3);


        final Circle connectorNode = circle;

        connectors.put(connector, connectorNode);

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
                computeInputConnectorSize();
                computeOutputConnectorSize();
                adjustConnectorSize();
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

                // we are already connected and manipulate the existing connection
                // rather than creating a new one
                if (controller.getConnections(connector.getType()).
                        isInputConnected(connector)) {
                    return;
                }

                newConnectionSkin =
                        new FXNewConnectionSkin(getSkinFactory(),
                        getParent(), connector, getController(), connector.getType());

                newConnectionSkin.add();

                t.consume();
                MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);

            }
        });

        connectorNode.onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                // we are already connected and manipulate the existing connection
                // rather than creating a new one
                if (controller.getConnections(connector.getType()).
                        isInputConnected(connector)) {
                    return;
                }

                t.consume();
                MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);

            }
        });

        connectorNode.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                // we are already connected and manipulate the existing connection
                // rather than creating a new one
                if (controller.getConnections(connector.getType()).
                        isInputConnected(connector)) {
                    return;
                }

                t.consume();
                try {
                    MouseEvent.fireEvent(newConnectionSkin.getReceiverConnector(), t);
                } catch (Exception ex) {
                    // TODO exception is not critical here (node already removed)
                }
            }
        });
    }

    private void computeInputConnectorSize() {

        double inset = 120;
        double minInset = 60;
        double minSize = 8;

        double connectorHeight = computeConnectorSize(inset, inputList.size());

        if (connectorHeight < minSize) {
            double diff = minSize - connectorHeight;
            inset = Math.max(inset - diff * inputList.size(), minInset);
            connectorHeight = computeConnectorSize(inset, inputList.size());
        }

        inputConnectorSize = connectorHeight;
    }

    private void computeOutputConnectorSize() {
        double inset = 120;
        double minInset = 60;
        double minSize = 8;

        double connectorHeight = computeConnectorSize(inset, outputList.size());

        if (connectorHeight < minSize) {
            double diff = minSize - connectorHeight;
            inset = Math.max(inset - diff * outputList.size(), minInset);
            connectorHeight = computeConnectorSize(inset, outputList.size());
        }

        outputConnectorSize = connectorHeight;
    }

    private double computeConnectorSize(double inset, int numConnectors) {

        double maxSize = 10;

        double connectorHeight = maxSize * 2;
        double originalConnectorHeight = connectorHeight;
        double gap = 5;

        double totalHeight = numConnectors * connectorHeight + (numConnectors - 1) * gap;

        connectorHeight = Math.min(totalHeight, node.getPrefHeight() - inset) / (numConnectors);
        connectorHeight = Math.min(connectorHeight, maxSize * 2);

        if (numConnectors == 1) {
            connectorHeight = originalConnectorHeight;
        }

        return connectorHeight;
    }

    private void adjustConnectorSize() {

        if (!inputList.isEmpty() && !outputList.isEmpty()) {
            inputConnectorSize = Math.min(inputConnectorSize, outputConnectorSize);
            outputConnectorSize = inputConnectorSize;
        }

        for (Circle connector : inputList) {
            connector.setRadius(inputConnectorSize / 2.0);
        }

        for (Circle connector : outputList) {
            connector.setRadius(outputConnectorSize / 2.0);
        }
    }

    private void removeConnector(Connector connector) {
        connectorList.remove(connector);
        Node connectorNode = connectors.remove(connector.getId());

        if (connectorNode != null && connectorNode.getParent() != null) {
            
            // TODO: remove connectors&connections?

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

        List<Connector> delList = new ArrayList<>(connectorList);

        for (Connector c : delList) {
            removeConnector(c);
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

    public Node getConnectorNodeByReference(Connector c) {
        return connectors.get(c);
    }
}

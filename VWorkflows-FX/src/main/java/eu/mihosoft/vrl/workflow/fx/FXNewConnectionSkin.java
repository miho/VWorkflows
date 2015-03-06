/*
 * FXNewConnectionSkin.java
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
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;
import java.util.List;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import jfxtras.labs.util.event.MouseControlUtil;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FXNewConnectionSkin implements ConnectionSkin<Connection>, FXSkin<Connection, Path> {

    private ObjectProperty<Connector> senderProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Connector> receiverProperty = new SimpleObjectProperty<>();
    private Path connectionPath;
    private LineTo lineTo;
    private MoveTo moveTo;
    private CubicCurveTo curveTo = new CubicCurveTo();
//    private Shape startConnector;
    private Circle receiverConnectorUI;
    private Circle senderConnectorUI;
    private VFlowModel flow;
    private VFlow flowController;
    private ObjectProperty<Connection> modelProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>();
    private String type;
    private Node lastNode;
    private FXSkinFactory skinFactory;
    private ConnectionListener connectionListener;

    public FXNewConnectionSkin(FXSkinFactory skinFactory, Parent parent, Connector sender, VFlow controller, String type) {
        this.skinFactory = skinFactory;
        setParent(parent);
        setSender(sender);

        this.flowController = controller;
        this.flow = controller.getModel();
        this.type = type;

//        startConnector = new Circle(20);
        receiverConnectorUI = new Circle(10);

        moveTo = new MoveTo();
        lineTo = new LineTo();
        connectionPath = new Path(moveTo, curveTo);

        init();
    }

    private void init() {

        connectionPath.getStyleClass().setAll("vnode-new-connection", "vnode-new-connection-" + getSender().getType());
        receiverConnectorUI.getStyleClass().setAll("vnode-new-connection-receiver", "vnode-new-connection-receiver-" + getSender().getType());

//        connectionPath.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.2));
//        connectionPath.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//        connectionPath.setStrokeWidth(5);
//        connectionPath.setStrokeLineCap(StrokeLineCap.ROUND);
//        receiverConnector.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.2));
//        receiverConnector.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//        if (type.equals("control")) {
//            receiverConnectorUI.setFill(new Color(1.0, 1.0, 0.0, 0.75));
//            receiverConnectorUI.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//        } else if (type.equals("data")) {
//            receiverConnectorUI.setFill(new Color(0.1, 0.1, 0.1, 0.5));
//            receiverConnectorUI.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//        } else if (type.equals("event")) {
//            receiverConnectorUI.setFill(new Color(255.0 / 255.0, 100.0 / 255.0, 1, 0.5));
//            receiverConnectorUI.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
//        }
        receiverConnectorUI.setStrokeWidth(3);

//        connectionPath.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");
//        receiverConnector.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");
//    
        final VNode sender = getSender().getNode();
        final FXFlowNodeSkin senderSkin = (FXFlowNodeSkin) getController().getNodeSkinLookup().getById(skinFactory, sender.getId());
        
        final Node senderNode = senderSkin.getConnectorNodeByReference(getSender());

        senderConnectorUI = (Circle) senderNode;

        DoubleBinding startXBinding = new DoubleBinding() {
            {
                super.bind(senderNode.boundsInLocalProperty(), senderNode.layoutXProperty());
            }

            @Override
            protected double computeValue() {

                return senderNode.getLayoutX();

            }
        };

        DoubleBinding startYBinding = new DoubleBinding() {
            {
                super.bind(senderNode.boundsInLocalProperty(), senderNode.layoutYProperty());
            }

            @Override
            protected double computeValue() {
                return senderNode.getLayoutY();
            }
        };

        DoubleBinding controlX1Binding = new DoubleBinding() {
            {
                super.bind(senderNode.boundsInLocalProperty(), senderNode.layoutXProperty(), receiverConnectorUI.layoutXProperty());
            }

            @Override
            protected double computeValue() {

                return senderNode.getLayoutX() + (receiverConnectorUI.getLayoutX() - senderNode.getLayoutX()) / 2;

            }
        };

        DoubleBinding controlY1Binding = new DoubleBinding() {
            {
                super.bind(senderNode.boundsInLocalProperty(), senderNode.layoutYProperty());
            }

            @Override
            protected double computeValue() {

                return senderNode.getLayoutY();

            }
        };

        DoubleBinding controlX2Binding = new DoubleBinding() {
            {
                super.bind(senderNode.boundsInLocalProperty(), senderNode.layoutXProperty(), receiverConnectorUI.layoutXProperty());
            }

            @Override
            protected double computeValue() {

                return receiverConnectorUI.getLayoutX() - (receiverConnectorUI.getLayoutX() - senderNode.getLayoutX()) / 2;

            }
        };

        DoubleBinding controlY2Binding = new DoubleBinding() {
            {
                super.bind(receiverConnectorUI.boundsInLocalProperty(), receiverConnectorUI.layoutYProperty());
            }

            @Override
            protected double computeValue() {

                return receiverConnectorUI.getLayoutY();

            }
        };

        moveTo.xProperty().bind(startXBinding);
        moveTo.yProperty().bind(startYBinding);

        curveTo.controlX1Property().bind(controlX1Binding);
        curveTo.controlY1Property().bind(controlY1Binding);
        curveTo.controlX2Property().bind(controlX2Binding);
        curveTo.controlY2Property().bind(controlY2Binding);
        curveTo.xProperty().bind(receiverConnectorUI.layoutXProperty());
        curveTo.yProperty().bind(receiverConnectorUI.layoutYProperty());

        makeDraggable();

        receiverConnectorUI.setLayoutX(senderNode.getLayoutX());
        receiverConnectorUI.setLayoutY(senderNode.getLayoutY());

        connectionListener
                = new ConnectionListenerImpl(
                        skinFactory, flowController, receiverConnectorUI);

    }

    private void makeDraggable() {

        connectionPath.toFront();
        receiverConnectorUI.toFront();

        MouseControlUtil.makeDraggable(receiverConnectorUI, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                if (lastNode != null) {
//                    lastNode.setEffect(null);
                    lastNode = null;
                }

                SelectedConnector selConnector = null;

                if (getSender().isOutput()) {
                    selConnector = FXConnectorUtil.getSelectedInputConnector(
                            getSender().getNode(), getParent(), type, t);
                } else {
                    selConnector = FXConnectorUtil.getSelectedOutputConnector(
                            getSender().getNode(), getParent(), type, t);
                }

                // reject connection if no main input defined for current node
                if (selConnector != null
                        && selConnector.getNode() != null
                        && selConnector.getConnector() == null) {
//                    DropShadow shadow = new DropShadow(20, Color.RED);
//                    Glow effect = new Glow(0.8);
//                    effect.setInput(shadow);
//                    selConnector.getNode().setEffect(effect);

                    //onConnectionIncompatible();
                    connectionListener.onNoConnection(selConnector.getNode());

                    lastNode = selConnector.getNode();
                }

                if (selConnector != null
                        && selConnector.getNode() != null
                        && selConnector.getConnector() != null) {

                    Connector receiverConnectorModel = selConnector.getConnector();
                    Node n = selConnector.getNode();
                    n.toFront();

                    VNode model = selConnector.getConnector().getNode();

//                    // we cannot create a connection from us to us
//                    if (model == getSender()) {
//                        return;
//                    }
                    ConnectionResult connResult = null;

                    if (getSender().isInput() && receiverConnectorModel.isOutput()) {

                        connResult = flow.tryConnect(
                                receiverConnectorModel, getSender());
                    } else {
                        connResult = flow.tryConnect(
                                getSender(), receiverConnectorModel);
                    }

                    if (connResult.getStatus().isCompatible()) {

                        if (lastNode != n) {

                            connectionListener.onConnectionCompatible(n);
                        }

                    } else {

//                        DropShadow shadow = new DropShadow(20, Color.RED);
//                        Glow effect = new Glow(0.8);
//                        effect.setInput(shadow);
//                        n.setEffect(effect);
                        connectionListener.onConnectionIncompatible();
                    }

                    receiverConnectorUI.toFront();

                    lastNode = n;
                } else {
                    if (lastNode == null) {
                        connectionListener.onNoConnection(receiverConnectorUI);
                    }
                }
            }
        }, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiverConnectorUI.layoutXProperty().unbind();
                receiverConnectorUI.layoutYProperty().unbind();
            }
        });

        receiverConnectorUI.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                receiverConnectorUI.toBack();
                connectionPath.toBack();

                if (lastNode != null) {
//                    lastNode.setEffect(null);
                    lastNode = null;
                }

//                Node n = NodeUtil.getDeepestNode(
//                        getParent(),
//                        t.getSceneX(), t.getSceneY(), FlowNodeWindow.class, ConnectorCircle.class);
                SelectedConnector selConnector = null;

                if (getSender().isOutput()) {
                    selConnector = FXConnectorUtil.getSelectedInputConnector(getSender().getNode(), getParent(), type, t);
                } else {
                    selConnector = FXConnectorUtil.getSelectedOutputConnector(getSender().getNode(), getParent(), type, t);
                }

                if (selConnector != null
                        && selConnector.getNode() != null
                        && selConnector.getConnector() != null) {

                    Node n = selConnector.getNode();

                    n.toFront();

                    Connector receiverConnector = selConnector.getConnector();

                    ConnectionResult connResult = null;

                    if (getSender().isInput() && receiverConnector.isOutput()) {
                        connResult = flow.connect(receiverConnector, getSender());
                        connectionListener.onCreateNewConnectionReverseReleased(connResult);

                    } else {
                        connResult = flow.connect(getSender(), receiverConnector);
                        connectionListener.onCreateNewConnectionReleased(connResult);
                    }

                    if (connResult.getStatus().isCompatible()) {
//                        System.out.println("FX-CONNECT: " + connResult.getConnection());
                    } else {
                        connectionListener.onConnectionIncompatibleReleased(n);
                    }

                }

                remove();
            }
        });

    }

    public Node getReceiverConnector() {
        return receiverConnectorUI;
    }

    @Override
    public Connector getSender() {
        return senderProperty.get();
    }

    @Override
    public final void setSender(Connector n) {
        
        if (n==null) {
            throw new IllegalArgumentException("Sender 'null' not supported.");
        }
        
        senderProperty.set(n);
    }

    @Override
    public ObjectProperty<Connector> senderProperty() {
        return senderProperty;
    }

    @Override
    public Connector getReceiver() {
        return receiverProperty.get();
    }

    @Override
    public void setReceiver(Connector n) {
        receiverProperty.set(n);
    }

    @Override
    public ObjectProperty<Connector> receiverProperty() {
        return receiverProperty;
    }

    @Override
    public Path getNode() {
        return connectionPath;
    }

    @Override
    public Parent getContentNode() {
        return getParent();
    }

    @Override
    public void setModel(Connection model) {
        modelProperty.set(model);
    }

    @Override
    public Connection getModel() {
        return modelProperty.get();
    }

    @Override
    public ObjectProperty<Connection> modelProperty() {
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
        NodeUtil.addToParent(getParent(), connectionPath);
//        VFXNodeUtils.addToParent(getParent(), startConnector);
        NodeUtil.addToParent(getParent(), receiverConnectorUI);

//        startConnector.toBack();
        receiverConnectorUI.toFront();
        connectionPath.toFront();
    }

    @Override
    public void remove() {
        NodeUtil.removeFromParent(connectionPath);
        NodeUtil.removeFromParent(receiverConnectorUI);
    }

    @Override
    public VFlow getController() {
        return flowController;
    }

    @Override
    public void setController(VFlow flow) {
        this.flowController = flow;
        this.flow = flow.getModel();
    }

    /**
     * @return the skinFactory
     */
    @Override
    public FXSkinFactory getSkinFactory() {
        return skinFactory;
    }
}

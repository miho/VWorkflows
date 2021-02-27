/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import jfxtras.labs.util.event.MouseControlUtil;

import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultFXConnectionSkin extends AbstractFXConnectionSkin {

    private final Connection connection;
    private Node lastNode;
    private boolean receiverDraggingStarted = false;
    private MapChangeListener<String, Object> vReqLister;

    public DefaultFXConnectionSkin(FXSkinFactory skinFactory,
            Parent parent, Connection connection, VFlow flow, String type) {
        super(skinFactory, parent, flow, type);
        this.connection = connection;
        this.setModel(connection);
    }

    @Override
    protected void postInitialize() {
        initVReqListeners();
        initMouseEventHandler();
    }

    protected void initStyle() {
        connectionPath.getStyleClass().setAll(
                "vnode-connection", "vnode-connection-" + type);
        receiverConnectorUI.getStyleClass().setAll(
                "vnode-connection-receiver",
                "vnode-connection-receiver-" + type);

        getReceiverUI().setFill(new Color(0, 1.0, 0, 0.0));
        getReceiverUI().setStroke(new Color(0, 1.0, 0, 0.0));
        getReceiverUI().setStrokeWidth(3);
    }

    protected void initSenderAndReceiver() {
        receiverConnectorUI = new Circle(15);

        // find the sender skin via lookup
        // TODO: replace lookup by direct reference?
        final FXFlowNodeSkin senderSkin = (FXFlowNodeSkin) getController().
                getNodeSkinLookup().getById(skinFactory,
                        connection.getSender().getId());

        // retrieve the sender node from its skin
        senderShape = senderSkin.getConnectorShape(connection.getSender());

        // find the receiver skin via lookup
        // TODO: replace lookup by direct reference?
        FXFlowNodeSkin receiverSkin = (FXFlowNodeSkin) getController().
                getNodeSkinLookup().getById(skinFactory,
                        connection.getReceiver().getId());

        // retrieve the receiver node from its skin
        receiverShape = receiverSkin.getConnectorShape(connection.getReceiver());

        // if we establish a connection between different flows
        // we have to create intermediate connections
        if (receiverShape.getNode().getParent() != senderShape.getNode().getParent()) {
            createIntermediateConnection(senderShape, receiverShape, connection);
        }

        setSender(getController().getNodeLookup().getConnectorById(
                connection.getSender().getId()));
        setReceiver(getController().getNodeLookup().getConnectorById(
                connection.getReceiver().getId()));
    }

    protected void initMouseEventHandler() {
        EventHandler<MouseEvent> contextMenuHandler = createContextMenuHandler(createContextMenu());
        connectionPath.addEventHandler(MouseEvent.MOUSE_CLICKED, contextMenuHandler);
        getReceiverUI().addEventHandler(MouseEvent.MOUSE_CLICKED, contextMenuHandler);
    } // end init

    protected EventHandler<MouseEvent> createContextMenuHandler(final ContextMenu contextMenu) {
        return (MouseEvent event) -> {
            // TODO: is this check for MouseButton.SECONDARY really necessary?
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(connectionPath,
                        event.getScreenX(), event.getScreenY());
            }
        };
    }

    protected ContextMenu createContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem removeITem = new MenuItem("Remove Connection");
        contextMenu.getItems().addAll(removeITem);
        removeITem.setOnAction((ActionEvent event) -> {
            controller.getConnections(type).remove(connection);
        });
        return contextMenu;
    }

    private void initVReqListeners() {
        configureEditCapability(false);

        vReqLister = (MapChangeListener.Change<? extends String, ? extends Object> change) -> {
            configureEditCapability(false);
        };

        getModel().getVisualizationRequest().addListener(vReqLister);
    }

    @Override
    protected void makeDraggable() {
        final DoubleBinding receiveXBinding = new DoubleBinding() {
            {
                super.bind(getReceiverShape().getNode().layoutXProperty(),
                        getReceiverShape().getNode().translateXProperty(),
                        getReceiverShape().radiusProperty());
            }

            @Override
            protected double computeValue() {

                return getReceiverShape().getNode().layoutXProperty().get()
                        + getReceiverShape().getNode().getTranslateX()
                        + getReceiverShape().getRadius();
            }
        };

        final DoubleBinding receiveYBinding = new DoubleBinding() {
            {
                super.bind(getReceiverShape().getNode().layoutYProperty(),
                        getReceiverShape().getNode().translateYProperty(),
                        getReceiverShape().radiusProperty());
            }

            @Override
            protected double computeValue() {

                return getReceiverShape().getNode().layoutYProperty().get()
                        + getReceiverShape().getNode().getTranslateY()
                        + getReceiverShape().getRadius();
            }
        };

        connectionPath.toFront();
        getReceiverUI().toFront();

        MouseControlUtil.makeDraggable(getReceiverUI(), (MouseEvent t) -> {
            receiverDraggingStarted = true;

            if (lastNode != null) {
//                    lastNode.setEffect(null);
                lastNode = null;
            }

            SelectedConnector selConnector
                    = FXConnectorUtil.getSelectedInputConnector(
                            getSender().getNode(),
                            getParent(), type, t);

            // reject connection if no main input defined for current node
            if (selConnector != null
                    && selConnector.getNode() != null
                    && selConnector.getConnector() == null) {
//                    DropShadow shadow = new DropShadow(20, Color.RED);
//                    Glow effect = new Glow(0.8);
//                    effect.setInput(shadow);
//                    selConnector.getNode().setEffect(effect);
                connectionListener.onNoConnection(selConnector.getNode());
                lastNode = selConnector.getNode();
            }

            if (selConnector != null
                    && selConnector.getNode() != null
                    && selConnector.getConnector() != null) {

                Node n = selConnector.getNode();
                n.toFront();
                Connector receiver = selConnector.getConnector();

                ConnectionResult connResult
                        = getSender().getNode().getFlow().tryConnect(
                                getSender(), receiver);

                Connector receiverConnector = selConnector.getConnector();
                boolean isSameConnection = receiverConnector.equals(getReceiver());

                if (connResult.getStatus().isCompatible() || isSameConnection) {

//                        DropShadow shadow = new DropShadow(20, Color.WHITE);
//                        Glow effect = new Glow(0.5);
//                        shadow.setInput(effect);
//                        n.setEffect(shadow);
                    getReceiverUI().toFront();

                    if (lastNode != n) {
                        receiverConnectorUI.radiusProperty().unbind();
                        connectionListener.onConnectionCompatible(n);
                    }

                } else {

//                        DropShadow shadow = new DropShadow(20, Color.RED);
//                        Glow effect = new Glow(0.8);
//                        effect.setInput(shadow);
//                        n.setEffect(effect);
                    connectionListener.onConnectionIncompatible();
                }

                getReceiverUI().toFront();

                lastNode = n;

            } else if (lastNode == null) {
                receiverConnectorUI.radiusProperty().unbind();
                connectionListener.onNoConnection(receiverConnectorUI);
            }
        }, (MouseEvent event) -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                getReceiverUI().layoutXProperty().unbind();
                getReceiverUI().layoutYProperty().unbind();
                receiverConnectorUI.radiusProperty().unbind();
            }
            connection.getReceiver().click(NodeUtil.mouseBtnFromEvent(event), event);
            receiverDraggingStarted = false;
        });

        getReceiverUI().layoutXProperty().bind(receiveXBinding);
        getReceiverUI().layoutYProperty().bind(receiveYBinding);

        getReceiverUI().onMouseReleasedProperty().set(
                (EventHandler<MouseEvent>) (MouseEvent t) -> {
                    if (!receiverDraggingStarted) {
                        return;
                    }

                    if (lastNode != null) {
//                    lastNode.setEffect(null);
                        lastNode = null;
                    }

                    getReceiverUI().toFront();
                    connectionPath.toBack();

                    getReceiverUI().layoutXProperty().bind(receiveXBinding);
                    getReceiverUI().layoutYProperty().bind(receiveYBinding);

                    SelectedConnector selConnector
                    = FXConnectorUtil.getSelectedInputConnector(
                            getSender().getNode(), getParent(), type, t);

                    boolean isSameConnection = false;

                    if (selConnector != null
                    && selConnector.getNode() != null
                    && selConnector.getConnector() != null) {

                        Node n = selConnector.getNode();
                        n.toFront();
                        Connector receiverConnector = selConnector.getConnector();

                        isSameConnection = receiverConnector.equals(getReceiver());

                        if (!isSameConnection) {

                            ConnectionResult connResult = controller.connect(
                                    getSender(), receiverConnector);

                            if (connResult.getStatus().isCompatible()) {
                                connectionListener.onCreateNewConnectionReleased(connResult);
                            } else {
                                connectionListener.onConnectionIncompatibleReleased(n);
                            }
                        }

                    }

                    if (!isSameConnection) {

                        // remove error notification etc.
                        if (controller.getConnections(type).contains(connection.getSender(),
                        connection.getReceiver())) {
                            connectionListener.onNoConnection(receiverConnectorUI);
                        }

                        remove();
                        connection.getConnections().remove(connection);
                    } else if (getReceiverShape() instanceof ConnectorShape) {
                        ConnectorShape recConnNode = getReceiverShape();

                        if (getReceiverUI() instanceof Circle) {
                            ((Circle) getReceiverUI()).radiusProperty().unbind();
                            FXConnectorUtil.stopTimeLine();
                            ((Circle) getReceiverUI()).radiusProperty().
                            bind(recConnNode.radiusProperty());
                            initStyle();
                        }
                    }
                });
    }

    @Override
    public final void setSender(Connector n) {
        senderProperty.set(n);
    }

    @Override
    public final void setModel(Connection model) {
        modelProperty.set(model);
    }

    @Override
    public void add() {
        NodeUtil.addToParent(getParent(), connectionPath);
//        VFXNodeUtils.addToParent(getParent(), startConnector);
        NodeUtil.addToParent(getParent(), getReceiverUI());

//        startConnector.toBack();
        getReceiverUI().toFront();
        connectionPath.toBack();
    }

    @Override
    public void remove() {
        getModel().getVisualizationRequest().removeListener(vReqLister);

        if (connectionPath.getParent() == null || getReceiverUI().getParent() == null) {
            return;
        }
        try {
            super.remove();
//            connection.getConnections().remove(connection);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void createIntermediateConnection(ConnectorShape senderNode, ConnectorShape receiverNode, Connection connection) {
        VNode sender = connection.getSender().getNode();
        VNode receiver = connection.getReceiver().getNode();

        throw new UnsupportedOperationException("Cannot visualize connection with different parent flows!");
    }

    public void configureEditCapability(boolean notEditable) {
        Optional<Boolean> disableEditing
                = getModel().getVisualizationRequest().
                get(VisualizationRequest.KEY_DISABLE_EDITING);

        if (disableEditing.isPresent()) {
            notEditable = disableEditing.get();
        }

        senderShape.getNode().setMouseTransparent(notEditable);
        receiverConnectorUI.setMouseTransparent(notEditable);
        connectionPath.setMouseTransparent(notEditable);
    }
}

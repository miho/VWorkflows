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

import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import jfxtras.labs.util.event.MouseControlUtil;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FXNewConnectionSkin extends AbstractFXConnectionSkin {
    private CubicCurveTo curveTo = new CubicCurveTo();
    private ConnectorShape senderConnectorUI;
    private VFlowModel flow;
    private Node lastNode;

    public FXNewConnectionSkin(FXSkinFactory skinFactory,
            Parent parent, Connector sender, VFlow controller, String type) {
        super(skinFactory, parent, controller, type);
        setSender(sender);
        flow = controller.getModel();
    }

    protected void initStyle() {
        connectionPath.getStyleClass().setAll(
            "vnode-new-connection",
            "vnode-new-connection-" + getSender().getType());
        receiverConnectorUI.getStyleClass().setAll(
            "vnode-new-connection-receiver",
            "vnode-new-connection-receiver-" + getSender().getType());

        receiverConnectorUI.setStrokeWidth(3);
        //
    }

    protected void initSenderAndReceiver() {
        receiverConnectorUI = new Circle(15);
        final VNode sender = getSender().getNode();
        final FXFlowNodeSkin senderSkin = (FXFlowNodeSkin) getController().
                getNodeSkinLookup().getById(skinFactory, sender.getId());
        
        senderShape = senderSkin.getConnectorShape(getSender());
        final Node senderNode = senderShape.getNode();

        senderConnectorUI = senderShape;

        receiverConnectorUI.setLayoutX(senderNode.getLayoutX()
                +receiverConnectorUI.getRadius());
        receiverConnectorUI.setLayoutY(senderNode.getLayoutY()
                +receiverConnectorUI.getRadius());
    }

    protected void makeDraggable() {
        connectionPath.toFront();
        receiverConnectorUI.toFront();

        MouseControlUtil.makeDraggable(receiverConnectorUI, (MouseEvent t) -> {
            
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
        }, (MouseEvent event) -> {
            receiverConnectorUI.layoutXProperty().unbind();
            receiverConnectorUI.layoutYProperty().unbind();
        }, true);

        receiverConnectorUI.onMouseReleasedProperty().set(
                (EventHandler<MouseEvent>) (MouseEvent t) -> {
            receiverConnectorUI.toBack();
            connectionPath.toBack();
            
            if (lastNode != null) {
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
            
            if (selConnector != null
                    && selConnector.getNode() != null
                    && selConnector.getConnector() != null) {
                
                Node n = selConnector.getNode();
                
                n.toFront();
                
                Connector receiverConnector = selConnector.getConnector();
                
                ConnectionResult connResult = null;
                
                if (getSender().isInput() && receiverConnector.isOutput()) {
                    connResult = flow.connect(receiverConnector, getSender());
                    connectionListener.
                            onCreateNewConnectionReverseReleased(connResult);
                    
                } else {
                    connResult = flow.connect(getSender(), receiverConnector);
                    connectionListener.onCreateNewConnectionReleased(connResult);
                }
                
                if (!connResult.getStatus().isCompatible()) {
                    connectionListener.onConnectionIncompatibleReleased(n);
                }
            }
            
            remove();
        });
    }

    @Override
    public final void setSender(Connector n) {
        
        if (n==null) {
            throw new IllegalArgumentException("Sender 'null' not supported.");
        }
        
        senderProperty.set(n);
    }

    @Override
    public void setController(VFlow flow) {
        super.setController(flow);
        this.flow = flow.getModel();
    }
}

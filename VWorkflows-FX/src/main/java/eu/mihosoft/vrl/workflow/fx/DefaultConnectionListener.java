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
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;
import javafx.scene.Node;

import java.util.List;

/**
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultConnectionListener implements ConnectionListener {

    private final Node receiverConnectorUI;
    private final FXSkinFactory skinFactory;
    private final VFlow flowController;

    public DefaultConnectionListener(FXSkinFactory skinFactory, VFlow vflow, Node receiverConnectorUI) {
        this.skinFactory = skinFactory;
        this.flowController = vflow;
        this.receiverConnectorUI = receiverConnectorUI;
    }

    @Override
    public void onConnectionCompatible(Node n) {
        FXConnectorUtil.connectAnim(receiverConnectorUI, n);
    }

    @Override
    public void onConnectionCompatibleReleased(Node n) {
        FXConnectorUtil.connectAnim(receiverConnectorUI, n);
    }

    @Override
    public void onConnectionIncompatible() {
        FXConnectorUtil.incompatibleAnim(receiverConnectorUI);
    }

    @Override
    public void onNoConnection(Node n) {

        FXConnectorUtil.unconnectAnim(receiverConnectorUI);
    }

    @Override
    public void onConnectionIncompatibleReleased(Node n) {

        FXConnectorUtil.connnectionIncompatibleAnim(n);
    }

    @Override
    public void onCreateNewConnectionReleased(ConnectionResult connResult) {
        newConnectionAnim(connResult);

        // update connector layout
        if (connResult.getConnection() != null) {
            Connection connection = connResult.getConnection();
            VNode senderNode = connection.getSender().getNode();
            VNode receiverNode = connection.getReceiver().getNode();

            List<VNodeSkin> senderSkins = flowController.getNodeSkinLookup().
                    getById(senderNode.getId());
            for (VNodeSkin skin : senderSkins) {
                FXFlowNodeSkin fxSkin = (FXFlowNodeSkin) skin;
                fxSkin.layoutConnectors();
            }
            List<VNodeSkin> receiverSkins = flowController.getNodeSkinLookup().
                    getById(receiverNode.getId());
            for (VNodeSkin skin : receiverSkins) {
                FXFlowNodeSkin fxSkin = (FXFlowNodeSkin) skin;
                fxSkin.layoutConnectors();
            }
        }

    }

    @Override
    public void onCreateNewConnectionReverseReleased(ConnectionResult connResult) {
        newConnectionReverseAnim(connResult);
    }

    @Override
    public void onRemoveConnectionReleased() {

        FXConnectorUtil.unconnectAnim(receiverConnectorUI);
    }

    private void newConnectionAnim(ConnectionResult connResult) {
        if (connResult.getConnection() != null) {
            FXConnectionSkin connectionSkin
                    = (FXConnectionSkin) flowController.getNodeSkinLookup().getById(
                            skinFactory, connResult.getConnection());
            FXConnectorUtil.connnectionEstablishedAnim(connectionSkin.getReceiverUI());
        }
    }

    private void newConnectionReverseAnim(ConnectionResult connResult) {
        // System.out.println("new-connection anim (reverse)");
        if (connResult.getConnection() != null) {
            FXConnectionSkin connectionSkin
                    = (FXConnectionSkin) flowController.getNodeSkinLookup().getById(
                            skinFactory, connResult.getConnection());

            FXConnectorUtil.connnectionEstablishedAnim(connectionSkin.getSenderShape().getNode());
        }
    }

}

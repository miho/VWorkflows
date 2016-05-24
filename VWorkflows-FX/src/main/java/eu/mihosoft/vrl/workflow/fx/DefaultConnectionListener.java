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

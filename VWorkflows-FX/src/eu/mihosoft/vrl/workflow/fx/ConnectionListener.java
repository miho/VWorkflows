/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.VFlow;
import javafx.scene.Node;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface ConnectionListener {

    void onConnectionCompatible(Node n);

    void onConnectionCompatibleReleased(Node n);

    void onConnectionIncompatible();

    void onConnectionIncompatibleReleased(Node n);

    void onCreateNewConnectionReleased(ConnectionResult connResult);

    void onCreateNewConnectionReverseReleased(ConnectionResult connResult);

    void onNoConnection(Node n);

    void onRemoveConnectionReleased();
    
}

class ConnectionListenerImpl implements ConnectionListener {
    
    private Node receiverConnectorUI;
    private FXSkinFactory skinFactory;
    private VFlow flowController;

    public ConnectionListenerImpl(FXSkinFactory skinFactory, VFlow vflow, Node receiverConnectorUI) {
        this.skinFactory = skinFactory;
        this.flowController = vflow;
        this.receiverConnectorUI = receiverConnectorUI;
    }
    

    @Override
    public void onConnectionCompatible(Node n) {
        System.out.println("connection compatible");
        
        FXConnectorUtil.connectAnim(receiverConnectorUI, n);
    }
    
    @Override
    public void onConnectionCompatibleReleased(Node n) {
        System.out.println("connection compatible");
        
        FXConnectorUtil.connectAnim(receiverConnectorUI, n);
    }
    
    @Override
    public void onConnectionIncompatible() {
        System.out.println("connection incompatible");
        
        FXConnectorUtil.incompatibleAnim(receiverConnectorUI);
    }
    
    @Override
    public void onNoConnection(Node n) {
        System.out.println("no connection");
        
        FXConnectorUtil.unconnectAnim(receiverConnectorUI);
    }
    
    @Override
    public void onConnectionIncompatibleReleased(Node n) {
        System.out.println("connection incompatible");

        FXConnectorUtil.connnectionIncompatibleAnim(n);
    }
    
    @Override
    public void onCreateNewConnectionReleased(ConnectionResult connResult) {
        System.out.println("connection created");
        newConnectionAnim(connResult);
    }
    
    @Override
    public void onCreateNewConnectionReverseReleased(ConnectionResult connResult) {
        System.out.println("connection created (reverse)");
        newConnectionReverseAnim(connResult);
    }
    
    @Override
    public void onRemoveConnectionReleased() {
        System.out.println("remove connection");
        
        FXConnectorUtil.unconnectAnim(receiverConnectorUI);
    }
    
    private void newConnectionAnim(ConnectionResult connResult) {
        System.out.println("new-connection anim");
        if (connResult.getConnection() != null) {
            FXConnectionSkin connectionSkin =
                    (FXConnectionSkin) flowController.getNodeSkinLookup().getById(
                    skinFactory, connResult.getConnection());
            FXConnectorUtil.connnectionEstablishedAnim(connectionSkin.getReceiverUI());
        }
    }

    private void newConnectionReverseAnim(ConnectionResult connResult) {
        // System.out.println("new-connection anim (reverse)");
        if (connResult.getConnection() != null) {
            FXConnectionSkin connectionSkin =
                    (FXConnectionSkin) flowController.getNodeSkinLookup().getById(
                    skinFactory, connResult.getConnection());
  
            FXConnectorUtil.connnectionEstablishedAnim(connectionSkin.getSenderNode());
        }
    }
    
}

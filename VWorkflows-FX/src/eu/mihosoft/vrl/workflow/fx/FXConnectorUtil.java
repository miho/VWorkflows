/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowModelImpl;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FXConnectorUtil {

    private FXConnectorUtil() {
        throw new AssertionError();
    }

    public static SelectedConnector getSelectedOutputConnector(VNode receiverNode, Parent fxParent, String type, MouseEvent t) {
        Node myNode = NodeUtil.getDeepestNode(
                fxParent,
                t.getSceneX(), t.getSceneY(),
                FlowNodeWindow.class, ConnectorCircle.class);
        Connector connector = null;

        if (myNode != null) {
            if (myNode instanceof ConnectorCircle) {
                ConnectorCircle circle = (ConnectorCircle) myNode;
                connector = circle.getConnector();
            } else if (myNode instanceof FlowNodeWindow) {

                final FlowNodeWindow w = (FlowNodeWindow) myNode;

                connector = w.nodeSkinProperty().get().
                        getModel().getMainOutput(type);
            }

            // we don't accept our parent as target
            if (connector != null && connector.getNode() instanceof VFlowModel) {
                VFlowModel model = (VFlowModel) connector.getNode();
                if (model.getNodes().contains(receiverNode)) {
                    myNode = null;
                    connector = null;
                }
            }
        }



        return new SelectedConnector(myNode, connector);
    }

    public static SelectedConnector getSelectedInputConnector(VNode senderNode, Parent fxParent, String type, MouseEvent t) {
        Node myNode = NodeUtil.getDeepestNode(
                fxParent,
                t.getSceneX(), t.getSceneY(),
                ConnectorCircle.class,
                FlowNodeWindow.class);
        Connector connector = null;

        if (myNode != null) {
            if (myNode instanceof ConnectorCircle) {
                ConnectorCircle circle = (ConnectorCircle) myNode;
                connector = circle.getConnector();
            } else if (myNode instanceof FlowNodeWindow) {

                final FlowNodeWindow w = (FlowNodeWindow) myNode;

                connector = w.nodeSkinProperty().get().
                        getModel().getMainInput(type);
            }

            // we don't accept our parent as target
            if (connector != null && connector.getNode() instanceof VFlowModel) {
                VFlowModel model = (VFlowModel) connector.getNode();
                if (model.getNodes().contains(senderNode)) {
                    myNode = null;
                    connector = null;
                }
            }
        }

        return new SelectedConnector(myNode, connector);
    }
}

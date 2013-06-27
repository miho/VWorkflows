/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
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

    public static SelectedConnector getSelectedOutputConnector(Parent parent, String type, MouseEvent t) {
        final Node myNode = NodeUtil.getDeepestNode(
                parent,
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
        }
        return new SelectedConnector(myNode, connector);
    }
    
    public static SelectedConnector getSelectedInputConnector(Parent parent, String type, MouseEvent t) {
        final Node myNode = NodeUtil.getDeepestNode(
                parent,
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
        }
        return new SelectedConnector(myNode, connector);
    }
}

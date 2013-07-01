/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FXConnectorUtil {

    private static Timeline timeline;

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

    public static void unconnectAnim(Node n) {

        if (timeline != null) {
            timeline.stop();
        }

        if (!(n instanceof Circle)) {
            return;
        }

        Circle circle = (Circle) n;

        timeline = new Timeline();
        timeline.setCycleCount(1);
        final KeyValue kv1 = new KeyValue(circle.radiusProperty(), 20);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(250), kv1);
        timeline.getKeyFrames().add(kf1);
        final KeyValue kv2 = new KeyValue(circle.fillProperty(),
                new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.5));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(50), kv2);
        timeline.getKeyFrames().add(kf2);
        final KeyValue kv3 = new KeyValue(circle.strokeProperty(),
                new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
        final KeyFrame kf3 = new KeyFrame(Duration.millis(50), kv3);
        timeline.getKeyFrames().add(kf3);

        timeline.play();

    }

    public static void connectAnim(Node n, Node target) {

        if (timeline != null) {
            timeline.stop();
        }

        if (!(n instanceof Shape)) {
            return;
        }

        Shape shape = (Shape) n;

        timeline = new Timeline();
//        timeline.setAutoReverse(true);
//        timeline.setCycleCount(Timeline.INDEFINITE);

        if (n instanceof Circle && target instanceof Circle) {
            Circle nCircle = (Circle) shape;
            Circle tCircle = (Circle) target;
            final KeyValue kv1 = new KeyValue(nCircle.radiusProperty(), tCircle.getRadius());
            final KeyFrame kf1 = new KeyFrame(Duration.millis(250), kv1);
            timeline.getKeyFrames().add(kf1);
        }

        final KeyValue kv2 = new KeyValue(shape.fillProperty(), new Color(0, 1, 0, 0.80));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(300), kv2);
        timeline.getKeyFrames().add(kf2);
        final KeyValue kv3 = new KeyValue(shape.strokeProperty(), new Color(0, 1, 0, 0.90));
        final KeyFrame kf3 = new KeyFrame(Duration.millis(300), kv3);
        timeline.getKeyFrames().add(kf3);

//        Paint fill = receiverConnector.getFill();
//        Paint stroke = receiverConnector.getStroke();
//
//        final KeyValue kv4 = new KeyValue(receiverConnector.fillProperty(), fill);
//        final KeyFrame kf4 = new KeyFrame(Duration.millis(kf2.getTime().toMillis() + 500), kv4);
//        timeline.getKeyFrames().add(kf4);
//        final KeyValue kv5 = new KeyValue(receiverConnector.strokeProperty(), stroke);
//        final KeyFrame kf5 = new KeyFrame(Duration.millis(kf3.getTime().toMillis() + 500), kv5);
//        timeline.getKeyFrames().add(kf5);


//        final KeyValue kv2 = new KeyValue(receiverConnector.layoutXProperty(), circle.getLayoutX());
//        final KeyFrame kf2 = new KeyFrame(Duration.millis(300), kv2);
//        timeline.getKeyFrames().add(kf2);
//        final KeyValue kv3 = new KeyValue(receiverConnector.layoutYProperty(), circle.getLayoutY());
//        final KeyFrame kf3 = new KeyFrame(Duration.millis(300), kv3);
//        timeline.getKeyFrames().add(kf3);


        timeline.play();
    }

    static void incompatibleAnim(Circle receiverConnector) {

        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline();
//        timeline.setAutoReverse(true);
//        timeline.setCycleCount(Timeline.INDEFINITE);

        final KeyValue kv2 = new KeyValue(receiverConnector.fillProperty(), new Color(1, 0, 0, 0.80));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);
        timeline.getKeyFrames().add(kf2);
        final KeyValue kv3 = new KeyValue(receiverConnector.strokeProperty(), new Color(1, 0, 0, 0.90));
        final KeyFrame kf3 = new KeyFrame(Duration.millis(500), kv3);
        timeline.getKeyFrames().add(kf3);
        timeline.play();
    }
}

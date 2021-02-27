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

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FXConnectorUtil {

    private static Timeline timeline;

    public static void stopTimeLine() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private FXConnectorUtil() {
        throw new AssertionError();
    }

    public static SelectedConnector getSelectedOutputConnector(
            VNode receiverNode, Parent fxParent, String type, MouseEvent t) {
        Node myNode = NodeUtil.getNode(
                fxParent,
                t.getSceneX(), t.getSceneY(),
                FlowNodeWindow.class, ConnectorShape.class);
        Connector connector = null;

        if (myNode != null) {
            if (myNode instanceof ConnectorShape) {
                ConnectorShape circle = (ConnectorShape) myNode;
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

    public static SelectedConnector getSelectedInputConnector(
            VNode senderNode, Parent fxParent, String type, MouseEvent t) {
        Node myNode = NodeUtil.getNode(
                fxParent,
                t.getSceneX(), t.getSceneY(),
                ConnectorShape.class,
                FlowNodeWindow.class);
        Connector connector = null;

        if (myNode != null) {
            if (myNode instanceof ConnectorShape) {
                ConnectorShape circle = (ConnectorShape) myNode;
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

    public static void connnectionEstablishedAnim(Node receiverUI) {

        System.out.println("established");

        if (timeline != null) {
            timeline.stop();
        }

        Timeline timeline = new Timeline();
//        timeline.setAutoReverse(true);
//        timeline.setCycleCount(Timeline.INDEFINITE);

        final Circle connectedShape = new Circle();
        connectedShape.setStrokeWidth(15);
        connectedShape.setFill(Color.TRANSPARENT);

        if (receiverUI instanceof Circle) {
            connectedShape.layoutXProperty().bind(receiverUI.layoutXProperty());
            connectedShape.layoutYProperty().bind(receiverUI.layoutYProperty());
        } else {
            connectedShape.layoutXProperty().bind(receiverUI.layoutXProperty().add(receiverUI.getBoundsInLocal().getWidth() * 0.5));
            connectedShape.layoutYProperty().bind(receiverUI.layoutYProperty().add(receiverUI.getBoundsInLocal().getHeight() * 0.5));
        }

        NodeUtil.addToParent(receiverUI.getParent(), connectedShape);

        final KeyValue kv0 = new KeyValue(connectedShape.radiusProperty(), 0);
        final KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
        timeline.getKeyFrames().add(kf0);
        final KeyValue kv1 = new KeyValue(connectedShape.radiusProperty(), 60);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(500), kv1);
        timeline.getKeyFrames().add(kf1);

        final KeyValue kv2 = new KeyValue(connectedShape.strokeProperty(),
                new Color(0, 1, 0, 1.0));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(0), kv2);
        timeline.getKeyFrames().add(kf2);
        final KeyValue kv3 = new KeyValue(connectedShape.strokeProperty(),
                new Color(0, 1, 0, 0.0));
        final KeyFrame kf3 = new KeyFrame(Duration.millis(400), kv3);
        timeline.getKeyFrames().add(kf3);

        timeline.setOnFinished((ActionEvent t) -> {
            NodeUtil.removeFromParent(connectedShape);
        });

        timeline.play();
    }

    public static void connnectionIncompatibleAnim(Node receiverUI) {
        Timeline timeline = new Timeline();
//        timeline.setAutoReverse(true);
//        timeline.setCycleCount(Timeline.INDEFINITE);

        final Circle connectedShape = new Circle();
        connectedShape.setStrokeWidth(15);
        connectedShape.setFill(Color.TRANSPARENT);

        if (receiverUI instanceof Circle) {
            connectedShape.layoutXProperty().bind(receiverUI.layoutXProperty());
            connectedShape.layoutYProperty().bind(receiverUI.layoutYProperty());
        } else {
            connectedShape.layoutXProperty().bind(receiverUI.layoutXProperty().add(receiverUI.getBoundsInLocal().getWidth() * 0.5));
            connectedShape.layoutYProperty().bind(receiverUI.layoutYProperty().add(receiverUI.getBoundsInLocal().getHeight() * 0.5));
        }

        NodeUtil.addToParent(receiverUI.getParent(), connectedShape);

        final KeyValue kv0 = new KeyValue(connectedShape.radiusProperty(), 0);
        final KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
        timeline.getKeyFrames().add(kf0);
        final KeyValue kv1 = new KeyValue(connectedShape.radiusProperty(), 60);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(500), kv1);
        timeline.getKeyFrames().add(kf1);

        final KeyValue kv2 = new KeyValue(connectedShape.strokeProperty(), new Color(1, 0, 0, 1.0));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(0), kv2);
        timeline.getKeyFrames().add(kf2);
        final KeyValue kv3 = new KeyValue(connectedShape.strokeProperty(), new Color(1, 0, 0, 0.0));
        final KeyFrame kf3 = new KeyFrame(Duration.millis(400), kv3);
        timeline.getKeyFrames().add(kf3);

        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                NodeUtil.removeFromParent(connectedShape);
            }
        });

        timeline.play();
    }

    public static void unconnectAnim(Node n) {

        if (timeline != null) {
            timeline.stop();
        }

        if (!(n instanceof Circle)) {
            return;
        }

        Circle circle = (Circle) n;
        circle.radiusProperty().unbind();

        timeline = new Timeline();
        timeline.setCycleCount(1);
        final KeyValue kv1 = new KeyValue(circle.radiusProperty(), 15);
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

        double targetRadius = 15;

        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            c.radiusProperty().unbind();
        }

        if (target instanceof Circle) {
            targetRadius = ((Circle) target).getRadius();
        } else if (target instanceof ConnectorShape) {
            targetRadius = ((ConnectorShape) target).getRadius();
        }

        if (n instanceof Circle) {
            Circle nCircle = (Circle) shape;

            final KeyValue kv1 = new KeyValue(nCircle.radiusProperty(),
                    targetRadius);
            final KeyFrame kf1 = new KeyFrame(Duration.millis(250), kv1);
            timeline.getKeyFrames().add(kf1);
        }

        final KeyValue kv2 = new KeyValue(shape.fillProperty(),
                new Color(0, 1, 0, 0.80));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(300), kv2);
        timeline.getKeyFrames().add(kf2);
        final KeyValue kv3 = new KeyValue(shape.strokeProperty(),
                new Color(0, 1, 0, 0.90));
        final KeyFrame kf3 = new KeyFrame(Duration.millis(300), kv3);
        timeline.getKeyFrames().add(kf3);

        timeline.play();

    }

    public static void incompatibleAnim(Node receiverConnector) {

        if (!(receiverConnector instanceof Circle)) {
            throw new IllegalArgumentException(
                    "only" + Circle.class
                    + "nodes are supported. Specified: " + receiverConnector);
        }

        Circle circleConnector = (Circle) receiverConnector;

        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline();

        final KeyValue kv2 = new KeyValue(circleConnector.fillProperty(),
                new Color(1, 0, 0, 0.80));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);
        timeline.getKeyFrames().add(kf2);
        final KeyValue kv3 = new KeyValue(circleConnector.strokeProperty(),
                new Color(1, 0, 0, 0.90));
        final KeyFrame kf3 = new KeyFrame(Duration.millis(500), kv3);
        timeline.getKeyFrames().add(kf3);
        timeline.play();

    }
}

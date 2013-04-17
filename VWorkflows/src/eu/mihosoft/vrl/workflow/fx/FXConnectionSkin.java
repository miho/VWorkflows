/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.FlowController;
import eu.mihosoft.vrl.workflow.FlowNode;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import jfxtras.labs.scene.control.window.Window;
import jfxtras.labs.util.event.MouseControlUtil;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXConnectionSkin implements ConnectionSkin<Connection>, FXSkin<Connection, Path> {

    private ObjectProperty<Connector> senderProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Connector> receiverProperty = new SimpleObjectProperty<>();
    private Path connectionPath;
    private LineTo lineTo;
    private MoveTo moveTo;
//    private Shape startConnector;
    private Shape receiverConnector;
    private Window receiverWindow;
    private FlowController controller;
    private Connection connection;
    private ObjectProperty<Connection> modelProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>();
    private String type;
    private Node lastNode;

    public FXConnectionSkin(Parent parent, Connection connection, FlowController flow, String type) {
        setParent(parent);
        this.connection = connection;
        this.controller = flow;
        this.type = type;

//        startConnector = new Circle(20);
        receiverConnector = new Circle(20);

        moveTo = new MoveTo();
        lineTo = new LineTo();
        connectionPath = new Path(moveTo, lineTo);

        init();
    }

    private void init() {

        connectionPath.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.2));
        connectionPath.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
        connectionPath.setStrokeWidth(5);

        receiverConnector.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.2));
        receiverConnector.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
        receiverConnector.setStrokeWidth(3);

        connectionPath.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");
        receiverConnector.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");


        final Connector sender = getController().getSender(connection);
        final Connector receiver = getController().getReceiver(connection);
        
        final FXFlowNodeSkin senderSkin = (FXFlowNodeSkin) getController().getNodeSkinLookup().getById(sender.getParent().getId());
        final FXFlowNodeSkin receiverSkin = (FXFlowNodeSkin) getController().getNodeSkinLookup().getById(receiver.getParent().getId());
        
        final Window senderWindow = senderSkin.getNode();
        receiverWindow = receiverSkin.getNode();
//
//        final FXConnectorSkin senderSkin = (FXConnectorSkin) getController().getSender(connection);
//        final Window senderWindow = senderSkin.getNode();
//
//        final FXConnectorSkin receiverSkin =  (FXConnectorSkin) getController().getReciever(connection);
//        receiverWindow = receiverSkin.getController().get;

        setSender(getController().getSender(connection));
        setReceiver(getController().getReceiver(connection));


        DoubleBinding startXBinding = new DoubleBinding() {
            {
                super.bind(senderWindow.layoutXProperty(), senderWindow.widthProperty());
            }

            @Override
            protected double computeValue() {

                return senderWindow.getLayoutX() + senderWindow.getWidth();

            }
        };

        DoubleBinding startYBinding = new DoubleBinding() {
            {
                super.bind(senderWindow.layoutYProperty(), senderWindow.heightProperty(), receiverWindow.heightProperty());
            }

            @Override
            protected double computeValue() {
                return senderWindow.getLayoutY() + senderWindow.getHeight() / 2;
            }
        };

        final DoubleBinding receiveXBinding = new DoubleBinding() {
            {
                super.bind(receiverWindow.layoutXProperty());
            }

            @Override
            protected double computeValue() {

                Point2D location = NodeUtil.transformCoordinates(
                        receiverWindow.getBoundsInParent().getMinX(),
                        receiverWindow.getBoundsInParent().getMinY(), receiverWindow.getParent(), getParent());

                return location.getX();
            }
        };

        final DoubleBinding receiveYBinding = new DoubleBinding() {
            {
                super.bind(
                        receiverWindow.layoutYProperty(),
                        receiverWindow.heightProperty()
                        );
            }

            @Override
            protected double computeValue() {

                if (receiverWindow.getParent() == getParent()) {
                    return receiverWindow.getLayoutY() + receiverWindow.getHeight() / 2;
                }

                Point2D location = NodeUtil.transformCoordinates(
                        0,
                        receiverWindow.getLayoutY(),
                        receiverWindow.getParent(), getParent());
                
                double height = 
                        receiverWindow.getHeight() 
                        * receiverWindow.getParent().localToSceneTransformProperty().get().getMyy();

                return location.getY() + height /2;
            }
        };

        receiverConnector.layoutXProperty().bind(receiveXBinding);
        receiverConnector.layoutYProperty().bind(receiveYBinding);

        moveTo.xProperty().bind(startXBinding);
        moveTo.yProperty().bind(startYBinding);

        lineTo.xProperty().bind(receiverConnector.layoutXProperty());
        lineTo.yProperty().bind(receiverConnector.layoutYProperty());

        receiverConnector.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                receiverConnector.toFront();
            }
        });

        receiverConnector.onMouseExitedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (!t.isPrimaryButtonDown()) {
//                    receiverConnector.toBack();
                }
            }
        });


        makeDraggable(receiveXBinding, receiveYBinding);

    }

    private void makeDraggable(
            final DoubleBinding receiveXBinding,
            final DoubleBinding receiveYBinding) {

        connectionPath.toFront();
        receiverConnector.toFront();

        MouseControlUtil.makeDraggable(receiverConnector, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

//                Parent root = getParent().getScene().getRoot();

//                // TODO why can root be null?
//                if (root == null) {
//                    return;
//                }

                final Node n = NodeUtil.getDeepestNode(
                        getParent(),
                        t.getSceneX(), t.getSceneY(), ConnectorNode.class);

                if (lastNode != null) {
                    lastNode.setEffect(null);
                    lastNode = null;
                }

                if (n != null) {
                    final ConnectorNode targetConnector = (ConnectorNode) n;

                    ConnectionResult connResult =
                            getSender().getParent().getFlow().tryConnect(
                            getSender(), targetConnector.getConnector());

                    if (connResult.getStatus().isCompatible()) {

                        DropShadow shadow = new DropShadow(20, Color.WHITE);
                        Glow effect = new Glow(0.5);
                        shadow.setInput(effect);
                        targetConnector.setEffect(shadow);

                        receiverConnector.setFill(new Color(220.0 / 255.0, 240.0 / 255.0, 1, 0.6));
                    } else {

                        DropShadow shadow = new DropShadow(20, Color.RED);
                        Glow effect = new Glow(0.8);
                        effect.setInput(shadow);
                        targetConnector.setEffect(effect);

                        receiverConnector.setFill(Color.RED);
                    }

                    lastNode = targetConnector;
                } else {
                    receiverConnector.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.5));
                }
            }
        }, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiverConnector.layoutXProperty().unbind();
                receiverConnector.layoutYProperty().unbind();
            }
        });

        receiverConnector.layoutXProperty().bind(receiveXBinding);
        receiverConnector.layoutYProperty().bind(receiveYBinding);


        receiverConnector.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                receiverConnector.toBack();
                connectionPath.toBack();

                receiverConnector.layoutXProperty().bind(receiveXBinding);
                receiverConnector.layoutYProperty().bind(receiveYBinding);

//                receiverConnector.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent t) {
//                        makeDraggable(receiveXBinding, receiveYBinding);
//                    }
//                });

                if (lastNode != null) {
                    lastNode.setEffect(null);
                    lastNode = null;
                }

                Node n = NodeUtil.getDeepestNode(
                        getParent(),
                        t.getSceneX(), t.getSceneY(), FlowNodeWindow.class);

                if (n != null) {
                    connection.setReceiverId(
                            ((FlowNodeWindow) n).nodeSkinProperty().get().getModel().getId());

                    receiverConnector.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.5));
                    init();

                } else {
                    remove();
                    connection.getConnections().remove(connection);
                }
            }
        });

    }

    @Override
    public Connector getSender() {
        return senderProperty.get();
    }

    @Override
    public final void setSender(Connector n) {
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
        NodeUtil.addToParent(getParent(), receiverConnector);

//        startConnector.toBack();
        receiverConnector.toBack();
        connectionPath.toBack();
    }

    @Override
    public void remove() {
        try {
            NodeUtil.removeFromParent(connectionPath);
            NodeUtil.removeFromParent(receiverConnector);
//            connection.getConnections().remove(connection);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * @return the controller
     */
    @Override
    public FlowController getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    @Override
    public void setController(FlowController controller) {
        this.controller = controller;
    }
}

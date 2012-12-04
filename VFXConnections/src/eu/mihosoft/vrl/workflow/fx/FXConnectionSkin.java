/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.fxwindows.VFXNodeUtils;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Flow;
import eu.mihosoft.vrl.workflow.FlowNode;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXConnectionSkin implements ConnectionSkin<Connection>, FXSkin<Connection, Path> {

    private ObjectProperty<FlowNode> senderProperty = new SimpleObjectProperty<>();
    private ObjectProperty<FlowNode> receiverProperty = new SimpleObjectProperty<>();
    private Path connectionPath;
    private LineTo lineTo;
    private MoveTo moveTo;
    private Shape startConnector;
    private Shape receiverConnector;
    private Flow flow;
    private Connection connection;
    private ObjectProperty<Connection> modelProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>();
    private String type;
    private Node lastNode;

    public FXConnectionSkin(Parent parent, Connection connection, Flow flow, String type) {
        setParent(parent);
        this.connection = connection;
        this.flow = flow;
        this.type = type;

        startConnector = new Circle(10);
        receiverConnector = new Circle(10);

        moveTo = new MoveTo();
        lineTo = new LineTo();
        connectionPath = new Path(moveTo, lineTo);

        init();
    }

    private void init() {
        final FlowNode sender = flow.getSender(connection);
        final FlowNode receiver = flow.getReceiver(connection);

        DoubleBinding startXBinding = new DoubleBinding() {
            {
                super.bind(sender.xProperty(), sender.widthProperty());
            }

            @Override
            protected double computeValue() {
                return sender.getX() + sender.getWidth();
            }
        };

        DoubleBinding startYBinding = new DoubleBinding() {
            {
                super.bind(sender.yProperty(), sender.heightProperty(), receiver.heightProperty());
            }

            @Override
            protected double computeValue() {
                return sender.getY() + sender.getHeight() / 2;
            }
        };

        final DoubleBinding receiveXBinding = new DoubleBinding() {
            {
                super.bind(receiver.xProperty());
            }

            @Override
            protected double computeValue() {
                return receiver.getX();
            }
        };

        final DoubleBinding receiveYBinding = new DoubleBinding() {
            {
                super.bind(receiver.yProperty(), receiver.heightProperty());
            }

            @Override
            protected double computeValue() {
                return receiver.getY() + receiver.getHeight() / 2;
            }
        };

        startConnector.layoutXProperty().bind(startXBinding);
        startConnector.layoutYProperty().bind(startYBinding);


        receiverConnector.layoutXProperty().bind(receiveXBinding);
        receiverConnector.layoutYProperty().bind(receiveYBinding);


        moveTo.xProperty().bind(startXBinding);
        moveTo.yProperty().bind(startYBinding);

        lineTo.xProperty().bind(receiverConnector.layoutXProperty());
        lineTo.yProperty().bind(receiverConnector.layoutYProperty());


        receiverConnector.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                makeDraggable(receiveXBinding, receiveYBinding);
            }
        });

    }

    private void makeDraggable(
            final DoubleBinding receiveXBinding,
            final DoubleBinding receiveYBinding) {

        connectionPath.toFront();
        receiverConnector.toFront();

        DraggingUtil.makeDraggable(receiverConnector, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                final Node n = VFXNodeUtils.getNode(
                        getParent().getScene().getRoot(),
                        t.getSceneX(), t.getSceneY(), FlowNodeWindow.class);

                if (n != null) {
                    final FlowNodeWindow w = (FlowNodeWindow) n;

//                    ColorAdjust colorAdjust = new ColorAdjust();
//                    colorAdjust.setContrast(0.1);
//                    colorAdjust.setHue(-0.1);
//                    colorAdjust.setBrightness(0.1);
//                    colorAdjust.setSaturation(0.2);
//                    w.setEffect(colorAdjust);
                    
                    ColorInput colorEffect = new ColorInput();
                    
                    colorEffect.setPaint(new Color(0,0,1.0,0.9));
                    
                    w.setEffect(new Glow(0.8));

                    receiverConnector.setFill(Color.GREEN);
                    
                    lastNode = w;
                } else {
                    receiverConnector.setFill(Color.BLACK);
                    if (lastNode!=null) {
                        lastNode.setEffect(null);
                        lastNode=null;
                    }
                }
            }
        }, null);


        receiverConnector.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                receiverConnector.toBack();
                connectionPath.toBack();

                receiverConnector.layoutXProperty().bind(receiveXBinding);
                receiverConnector.layoutYProperty().bind(receiveYBinding);

                receiverConnector.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        makeDraggable(receiveXBinding, receiveYBinding);
                    }
                });

                Node n = VFXNodeUtils.getNode(
                        getParent().getScene().getRoot(),
                        t.getSceneX(), t.getSceneY(), FlowNodeWindow.class);

                if (n != null) {
                    connection.setReceiverId(
                            ((FlowNodeWindow) n).nodeSkinProperty().get().getModel().getId());

                    receiverConnector.setFill(Color.BLACK);
                    init();
                    
                    if (lastNode!=null) {
                        lastNode.setEffect(null);
                        lastNode=null;
                    }

                } else {
                    remove();
                }
            }
        });

    }

    @Override
    public FlowNode getSender() {
        return senderProperty.get();
    }

    @Override
    public void setSender(FlowNode n) {
        senderProperty.set(n);
    }

    @Override
    public ObjectProperty<FlowNode> senderProperty() {
        return senderProperty;
    }

    @Override
    public FlowNode getReceiver() {
        return receiverProperty.get();
    }

    @Override
    public void setReceiver(FlowNode n) {
        receiverProperty.set(n);
    }

    @Override
    public ObjectProperty<FlowNode> receiverProperty() {
        return receiverProperty;
    }

    @Override
    public Path getNode() {
        return connectionPath;
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
        VFXNodeUtils.addToParent(getParent(), connectionPath);
        VFXNodeUtils.addToParent(getParent(), startConnector);
        VFXNodeUtils.addToParent(getParent(), receiverConnector);

        startConnector.toBack();
        receiverConnector.toBack();
        connectionPath.toBack();
    }

    @Override
    public void remove() {
        VFXNodeUtils.removeFromParent(connectionPath);
        VFXNodeUtils.removeFromParent(startConnector);
        VFXNodeUtils.removeFromParent(receiverConnector);
        connection.getConnections().remove(connection);
    }
}

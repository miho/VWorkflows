/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
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
import javafx.scene.shape.StrokeLineCap;
import jfxtras.labs.util.event.MouseControlUtil;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXNewConnectionSkin implements ConnectionSkin<Connection>, FXSkin<Connection, Path> {

    private ObjectProperty<Connector> senderProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Connector> receiverProperty = new SimpleObjectProperty<>();
    private Path connectionPath;
    private LineTo lineTo;
    private MoveTo moveTo;
//    private Shape startConnector;
    private Shape receiverConnector;
    private VFlowModel flow;
    private VFlow flowController;
    private ObjectProperty<Connection> modelProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>();
    private String type;
    private Node lastNode;
    private FXSkinFactory skinFactory;

    public FXNewConnectionSkin(FXSkinFactory skinFactory, Parent parent, Connector sender, VFlow controller, String type) {
        this.skinFactory = skinFactory;
        setParent(parent);
        setSender(sender);

        this.flowController = controller;
        this.flow = controller.getModel();
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
        connectionPath.setStrokeLineCap(StrokeLineCap.ROUND);

//        receiverConnector.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.2));
//        receiverConnector.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));

        if (type.equals("control")) {
            receiverConnector.setFill(new Color(1.0, 1.0, 0.0, 0.75));
            receiverConnector.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
        } else if (type.equals("data")) {
            receiverConnector.setFill(new Color(0.1, 0.1, 0.1, 0.5));
            receiverConnector.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
        } else if (type.equals("event")) {
            receiverConnector.setFill(new Color(255.0 / 255.0, 100.0 / 255.0, 1, 0.5));
            receiverConnector.setStroke(new Color(120 / 255.0, 140 / 255.0, 1, 0.42));
        }

        receiverConnector.setStrokeWidth(3);

//        connectionPath.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");
//        receiverConnector.setStyle("-fx-background-color: rgba(120,140,255,0.2);-fx-border-color: rgba(120,140,255,0.42);-fx-border-width: 2;");
//    

        final VNode sender = getSender().getNode();

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
                super.bind(sender.yProperty(), sender.heightProperty());
            }

            @Override
            protected double computeValue() {
                return sender.getY() + sender.getHeight() / 2;
            }
        };

        moveTo.xProperty().bind(startXBinding);
        moveTo.yProperty().bind(startYBinding);

        lineTo.xProperty().bind(receiverConnector.layoutXProperty());
        lineTo.yProperty().bind(receiverConnector.layoutYProperty());

        makeDraggable();

        receiverConnector.setLayoutX(getSender().getNode().getX() + getSender().getNode().getWidth());
        receiverConnector.setLayoutY(getSender().getNode().getY() + getSender().getNode().getHeight() / 2.0);

    }

    private void makeDraggable() {

        connectionPath.toFront();
        receiverConnector.toFront();

        MouseControlUtil.makeDraggable(receiverConnector, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {



                if (lastNode != null) {
                    lastNode.setEffect(null);
                    lastNode = null;
                }


                SelectedConnector selConnector = 
                        FXConnectorUtil.getSelectedInputConnector(getParent(), type,t);



                if (selConnector != null
                        && selConnector.getNode() != null
                        && selConnector.getConnector() != null) {

                    Connector receiverConnector = selConnector.getConnector();
                    Node n = selConnector.getNode();


                    VNode model = selConnector.getConnector().getNode();

//                    // we cannot create a connection from us to us
//                    if (model == getSender()) {
//                        return;
//                    }

                    ConnectionResult connResult =
                            flow.tryConnect(
                            getSender(), receiverConnector);

                    if (connResult.getStatus().isCompatible()) {

                        DropShadow shadow = new DropShadow(20, Color.WHITE);
                        Glow effect = new Glow(0.5);
                        shadow.setInput(effect);
                        n.setEffect(shadow);

//                        receiverConnector.setFill(new Color(220.0 / 255.0, 240.0 / 255.0, 1, 0.6));
                    } else {

                        DropShadow shadow = new DropShadow(20, Color.RED);
                        Glow effect = new Glow(0.8);
                        effect.setInput(shadow);
                        n.setEffect(effect);

//                        receiverConnector.setFill(Color.RED);
                    }

                    lastNode = n;
                } else {
//                    receiverConnector.setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.5));
                }
            }
        }, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiverConnector.layoutXProperty().unbind();
                receiverConnector.layoutYProperty().unbind();
            }
        });

        receiverConnector.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                receiverConnector.toBack();
                connectionPath.toBack();

                if (lastNode != null) {
                    lastNode.setEffect(null);
                    lastNode = null;
                }

//                Node n = NodeUtil.getDeepestNode(
//                        getParent(),
//                        t.getSceneX(), t.getSceneY(), FlowNodeWindow.class, ConnectorCircle.class);

                SelectedConnector selConnector = 
                        FXConnectorUtil.getSelectedInputConnector(getParent(), type,t);



                if (selConnector != null
                        && selConnector.getNode() != null
                        && selConnector.getConnector() != null) {

                    Node n = selConnector.getNode();
                    Connector receiverConnector = selConnector.getConnector();

                    if (n instanceof Shape) {
                        ((Shape) n).setFill(new Color(120.0 / 255.0, 140.0 / 255.0, 1, 0.5));
                    }

                    System.out.println("FX-CONNECT: " + getSender().getId() + " -> " + receiverConnector.getId());

                    flow.connect(getSender(), receiverConnector);
                }

                remove();
            }
        });

    }

   

    public Node getReceiverConnector() {
        return receiverConnector;
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
        receiverConnector.toFront();
        connectionPath.toFront();
    }

    @Override
    public void remove() {
        NodeUtil.removeFromParent(connectionPath);
        NodeUtil.removeFromParent(receiverConnector);
    }

    @Override
    public VFlow getController() {
        return flowController;
    }

    @Override
    public void setController(VFlow flow) {
        this.flowController = flow;
        this.flow = flow.getModel();
    }

    /**
     * @return the skinFactory
     */
    public FXSkinFactory getSkinFactory() {
        return skinFactory;
    }
}

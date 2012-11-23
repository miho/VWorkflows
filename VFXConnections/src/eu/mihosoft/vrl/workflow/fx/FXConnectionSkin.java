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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
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
    private Shape startConnector;
    private Shape receiverConnector;
    private Flow flow;
    private Connection connection;
     private ObjectProperty<Connection> modelProperty = new SimpleObjectProperty<>();
         private ObjectProperty<Parent> parentProperty = new SimpleObjectProperty<>();

    public FXConnectionSkin(Parent parent, Connection connection, Flow flow) {
        setParent(parent);
        this.connection = connection;
        this.flow = flow;
        init();
    }

    private void init() {
        FlowNode sender = flow.getSender(connection);
        FlowNode receiver = flow.getReceiver(connection);

        MoveTo moveTo = new MoveTo();
        moveTo.xProperty().bind(sender.xProperty());
        moveTo.yProperty().bind(sender.yProperty());

        LineTo lineTo = new LineTo();
        lineTo.xProperty().bind(receiver.xProperty());
        lineTo.yProperty().bind(receiver.yProperty());

        connectionPath = new Path(moveTo, lineTo);
        
        startConnector = new Circle(10);
        startConnector.layoutXProperty().bind(moveTo.xProperty());
        startConnector.layoutYProperty().bind(moveTo.yProperty());
        
        receiverConnector = new Circle(10);
        receiverConnector.layoutXProperty().bind(lineTo.xProperty());
        receiverConnector.layoutYProperty().bind(lineTo.yProperty());
        
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
    }
    
    @Override
    public void remove() {
        VFXNodeUtils.removeFromParent(connectionPath);
        VFXNodeUtils.removeFromParent(startConnector);
        VFXNodeUtils.removeFromParent(receiverConnector);
    }
}

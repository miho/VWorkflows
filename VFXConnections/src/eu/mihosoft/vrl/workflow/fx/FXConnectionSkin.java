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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FXConnectionSkin implements ConnectionSkin<Connection>, FXSkin<Connection, Path> {

    private ObjectProperty<FlowNode> senderProperty = new SimpleObjectProperty<>();
    private ObjectProperty<FlowNode> receiverProperty = new SimpleObjectProperty<>();
    private Path connectionPath;
    private Flow flow;
    private Connection connection;

    public FXConnectionSkin(Connection connection, Flow flow) {
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
    public void remove() {
        VFXNodeUtils.removeFromParent(connectionPath);
    }
}

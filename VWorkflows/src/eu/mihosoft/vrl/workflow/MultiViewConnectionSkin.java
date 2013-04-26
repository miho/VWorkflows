/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MultiViewConnectionSkin extends MultiViewSkin<ConnectionSkin<Connection>, Connection> implements ConnectionSkin<Connection>{
    
    private ObjectProperty<VNode> senderProperty = new SimpleObjectProperty<>();
    private ObjectProperty<VNode> receiverProperty = new SimpleObjectProperty<>();
    
    private Connection c;
    private VFlow flow;
    private String type;

    public MultiViewConnectionSkin(Connection c, VFlow flow, String type) {
        this.c = c;
        this.flow = flow;
        this.type = type;
    }   

    @Override
    public VNode getSender() {
        return senderProperty().get();
    }

    @Override
    public void setSender(VNode n) {
        senderProperty().set(n);
    }

    @Override
    public ObjectProperty<VNode> senderProperty() {
        return this.senderProperty;
    }

    @Override
    public VNode getReceiver() {
        return receiverProperty().get();
    }

    @Override
    public void setReceiver(VNode n) {
        receiverProperty().set(n);
    }

    @Override
    public ObjectProperty<VNode> receiverProperty() {
        return this.receiverProperty;
    }
    
}

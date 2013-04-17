/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ConnectionSkin<T extends Connection> extends Skin<Connection> {

    public FlowNode getSender();

    public void setSender(FlowNode n);

    public ObjectProperty<FlowNode> senderProperty();

    public FlowNode getReceiver();

    public void setReceiver(FlowNode n);

    public ObjectProperty<FlowNode> receiverProperty();
}

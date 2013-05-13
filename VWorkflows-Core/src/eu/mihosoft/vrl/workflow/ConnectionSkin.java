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

    public VNode getSender();

    public void setSender(VNode n);

    public ObjectProperty<VNode> senderProperty();

    public VNode getReceiver();

    public void setReceiver(VNode n);

    public ObjectProperty<VNode> receiverProperty();
}

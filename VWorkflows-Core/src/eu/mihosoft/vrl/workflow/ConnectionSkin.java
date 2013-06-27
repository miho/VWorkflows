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

    public Connector getSender();

    public void setSender(Connector n);

    public ObjectProperty<Connector> senderProperty();

    public Connector getReceiver();

    public void setReceiver(Connector n);

    public ObjectProperty<Connector> receiverProperty();
}

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
public interface Connector extends Model {

    public void setValueObject(ConnectorValueObject obj);

    public ConnectorValueObject getValueObject();

    public ObjectProperty<ConnectorValueObject> valueObjectProperty();
}

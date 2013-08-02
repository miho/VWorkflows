/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface Connector extends Model {

    public String getType();

    public boolean isInput();

    public boolean isOutput();

    public String getId();

    public String getLocalId();

    public void setLocalId(String id);

    public VNode getNode();

    public void setValueObject(ValueObject obj);

    public ValueObject getValueObject();

    public ObjectProperty<ValueObject> valueObjectProperty();
}

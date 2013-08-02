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
public interface Skin<T extends Model> {
    public void add();
    public void remove();
    public void setModel(T model);
    public T getModel();
    public ObjectProperty<T> modelProperty();
    public VFlow getController();
    public void setController(VFlow flow);
    public SkinFactory getSkinFactory();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Selectable interface.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Selectable {

    /**
     * Determines whether this object is selected.
     *
     * @return <code>true</code> if this object is
     * selected;<code>false</code>otherwise
     */
    boolean isSelected();

    /**
     * Returns the selection state property.
     *
     * @return selection state property
     */
    ReadOnlyBooleanProperty selectedProperty();

    /**
     * Requests (de-)selection of this object.
     *
     * @param b selection state (true or false)
     * @return <code>true</code> if this object has been selected;
     * <code>false</code> otherwise
     */
    boolean requestSelection(boolean b);

}

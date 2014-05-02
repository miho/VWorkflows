/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Selectable {

    boolean isSelected();

    ReadOnlyBooleanProperty selectedProperty();

    boolean requestSelection(boolean b);
  
}

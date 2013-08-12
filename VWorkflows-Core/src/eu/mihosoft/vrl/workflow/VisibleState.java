/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.BooleanProperty;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface VisibleState {

    public BooleanProperty visibleProperty();

    public boolean isVisible();

    public void setVisible(boolean b);
}

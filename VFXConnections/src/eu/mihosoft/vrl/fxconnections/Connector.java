/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import javafx.beans.property.StringProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connector<T> {
    public StringProperty idProperty();
    public void setId(String id);
    public String getID();
}

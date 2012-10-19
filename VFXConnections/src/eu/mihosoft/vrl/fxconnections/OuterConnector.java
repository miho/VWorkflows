/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class OuterConnector extends Control {

    private StringProperty connectorTypeProperty = new SimpleStringProperty();

    public OuterConnector() {
        //
    }

    public StringProperty connectorTypeProperty() {
        return connectorTypeProperty;
    }

    public void setConnectorType(String type) {
        connectorTypeProperty.set(type);
    }

    public String getConnectorType() {
        return connectorTypeProperty.get();
    }
}

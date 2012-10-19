/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import eu.mihosoft.vrl.fxwindows.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ConnectorPane extends Control {

    public static final String DEFAULT_STYLE_CLASS = "connection-pane";
    
    private ObservableList<OuterConnector> connectors = FXCollections.observableArrayList();

    public ConnectorPane() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected String getUserAgentStylesheet() {
        return Constants.DEFAULT_STYLE;
    }

    /**
     * @return the connectors
     */
    public ObservableList<OuterConnector> getConnectors() {
        return connectors;
    }
}

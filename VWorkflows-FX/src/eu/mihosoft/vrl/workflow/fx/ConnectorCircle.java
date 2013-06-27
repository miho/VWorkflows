/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connector;
import javafx.scene.shape.Circle;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectorCircle extends Circle{
    private Connector connector;

    public ConnectorCircle(Connector connector) {
        this.connector = connector;
    }

    public ConnectorCircle(Connector connector, double radius) {
        super(radius);
        this.connector = connector;
    }
    
    public ConnectorCircle() {
    }
    
    
    /**
     * @return the connector
     */
    public Connector getConnector() {
        return connector;
    }

    /**
     * @param connector the connector to set
     */
    public void setConnector(Connector connector) {
        this.connector = connector;
    }
    
}

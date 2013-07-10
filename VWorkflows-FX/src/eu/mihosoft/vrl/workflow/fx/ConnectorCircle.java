/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import javafx.scene.shape.Circle;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectorCircle extends Circle {

    private Connector connector;
    private VFlow flow;
    private FXSkinFactory skinFactory;
    private FXConnectionSkin connectionSkin;

    public ConnectorCircle(VFlow flow, FXSkinFactory skinFactory, Connector connector) {
        this.connector = connector;
        this.flow = flow;
        this.skinFactory = skinFactory;
    }

    public ConnectorCircle(VFlow flow, FXSkinFactory skinFactory, Connector connector, double radius) {
        super(radius);
        this.connector = connector;
        this.flow = flow;
        this.skinFactory = skinFactory;

        init();
    }

    public ConnectorCircle(VFlow flow, FXSkinFactory skinFactory) {
        this.flow = flow;
        this.skinFactory = skinFactory;
        init();
    }

    private void init() {
        this.getStyleClass().add("connector");
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

    private void moveConnectionReceiverToFront() {
        connectionSkin = null;
        
        if (connector.isInput() && flow.getConnections(connector.getType()).isInputConnected(connector.getId())) {
            for (Connection conn : flow.getConnections(connector.getType()).getConnections()) {
                ConnectionSkin skinI = flow.getNodeSkinLookup().getById(skinFactory, conn);

                if (skinI instanceof FXConnectionSkin) {
                    FXConnectionSkin fxSkin = (FXConnectionSkin) skinI;
                    connectionSkin = fxSkin;
                    connectionSkin.toFront();
                }
            }
        }
    }
    
    @Override
    public void toFront() {
        super.toFront();
        moveConnectionReceiverToFront();
    }
}

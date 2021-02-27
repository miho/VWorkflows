/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
package eu.mihosoft.vrl.workflow.fx;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;


/**
 * Circle node that represents a connector.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ConnectorCircle extends Region implements ConnectorShape {
    
    private Connector connector;
    private final VFlow flow;
    private final FXSkinFactory skinFactory;
    private ConnectionSkin connectionSkin;
    private final DoubleProperty radiusProperty = new SimpleDoubleProperty();
    
    public ConnectorCircle(VFlow flow, FXSkinFactory skinFactory, Connector connector) {
        setConnector(connector);
        this.flow = flow;
        this.skinFactory = skinFactory;
    }
    
    public ConnectorCircle(VFlow flow, FXSkinFactory skinFactory, Connector connector, double radius) {
        radiusProperty.set(radius);
        setConnector(connector);
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
        this.getStyleClass().add("vnode-connector");
        this.setManaged(true);
        setCacheShape(true);
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        this.prefWidthProperty().bind(radiusProperty());
        
        radiusProperty().addListener((ov,oldV,newV)-> {
            resize(newV.doubleValue()*2, newV.doubleValue()*2);
        });
    }

    @Override
    public Connector getConnector() {
        return connector;
    }

    @Override
    public final void setConnector(Connector connector) {
        
        if (getConnector() != null) {
            getStyleClass().remove("vnode-connector-" + getConnector().getType());
        }
        
        this.connector = connector;
        
        if (getConnector() != null) {
            getStyleClass().add("vnode-connector-" + getConnector().getType());
        }
        
    }
    
    private void moveConnectionReceiverToFront() {
        connectionSkin = null;

        if (connector.isInput() && flow.getConnections(connector.getType()).isInputConnected(connector)) {
            for (Connection conn : flow.getConnections(connector.getType()).getConnections()) {
                ConnectionSkin connectionSkin = flow.getNodeSkinLookup().getById(skinFactory, conn);
                if (connectionSkin != null) {
                    this.connectionSkin = connectionSkin;
                    connectionSkin.receiverToFront();
                }
            }
        }
    }
    
    @Override
    public void toFront() {
        super.toFront();
        moveConnectionReceiverToFront();
    }

    @Override
    public DoubleProperty radiusProperty() {
        return radiusProperty;
    }
    
    @Override
    public void setRadius(double radius) {
        radiusProperty().set(radius);
    }
    
    @Override
    public double getRadius() {
        return radiusProperty().get();
    }

    @Override
    public Node getNode() {
        return this;
    }
}

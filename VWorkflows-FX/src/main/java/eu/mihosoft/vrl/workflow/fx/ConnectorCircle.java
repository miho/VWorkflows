/*
 * ConnectorCircle.java
 * 
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VFlow;
import javafx.scene.shape.Circle;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
class ConnectorCircle extends Circle {

    private Connector connector;
    private final VFlow flow;
    private final FXSkinFactory skinFactory;
    private FXConnectionSkin connectionSkin;

    public ConnectorCircle(VFlow flow, FXSkinFactory skinFactory, Connector connector) {
        setConnector(connector);
        this.flow = flow;
        this.skinFactory = skinFactory;
    }

    public ConnectorCircle(VFlow flow, FXSkinFactory skinFactory, Connector connector, double radius) {
        super(radius);
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
    public final void setConnector(Connector connector) {
        
        if (getConnector()!=null) {
            getStyleClass().remove("vnode-connector-"+getConnector().getType());
        }
        
        this.connector = connector;
        
        if (getConnector()!=null) {
            getStyleClass().add("vnode-connector-"+getConnector().getType());
        }
        
    }

    private void moveConnectionReceiverToFront() {
        connectionSkin = null;

        if (connector.isInput() && flow.getConnections(connector.getType()).isInputConnected(connector)) {
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

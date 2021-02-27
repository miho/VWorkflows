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
package eu.mihosoft.vrl.workflow.io;

import eu.mihosoft.vrl.workflow.ClickEvent;
import eu.mihosoft.vrl.workflow.ConnectionEvent;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.MouseButton;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.event.EventHandler;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
class ConnectorIOImpl implements Connector {

    private VNode node;
    private String type;
    private String localId;
    private VisualizationRequest vRequest;
    private boolean input;
    private boolean output;
    private ValueObject vObj;

    public ConnectorIOImpl(VNode node, String type, String localId, boolean input, boolean output) {
        this.type = type;
        this.localId = localId;
        this.node = node;
        this.input = input;
        this.output = output;
    }
    
    public ConnectorIOImpl(Connector c) {
        this(c.getNode(), c.getType(), c.getLocalId(), c.isInput(), c.isOutput());
        this.vObj = c.getValueObject();
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getId() {
        return this.node.getId() + ":" + this.localId;
    }

    @Override
    public String getLocalId() {
        return this.localId;
    }

    @Override
    public void setLocalId(String id) {
        this.localId = id;
    }

    @Override
    public VNode getNode() {
        return this.node;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return this.vRequest;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vRequest = vReq;
    }

    /**
     * @return the input
     */
    @Override
    public boolean isInput() {
        return input;
    }

    /**
     * @return the output
     */
    @Override
    public boolean isOutput() {
        return output;
    }

    @Override
    public void setValueObject(ValueObject obj) {
        this.vObj = obj;
    }

    @Override
    public ValueObject getValueObject() {
        return vObj;
    }

    @Override
    public ObjectProperty<ValueObject> valueObjectProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }

    @Override
    public void addConnectionEventListener(EventHandler<ConnectionEvent> handler) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }

    @Override
    public void removeConnectionEventListener(EventHandler<ConnectionEvent> handler) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }

    @Override
    public void addClickEventListener(EventHandler<ClickEvent> handler) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }

    @Override
    public void removeClickEventListener(EventHandler<ClickEvent> handler) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }

    @Override
    public void click(MouseButton btn, Object event) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isVisualizationRequestInitialized() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMaxNumberOfConnections(int numConnections) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMaxNumberOfConnections() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObjectProperty<Integer> maxNumberOfConnectionsProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

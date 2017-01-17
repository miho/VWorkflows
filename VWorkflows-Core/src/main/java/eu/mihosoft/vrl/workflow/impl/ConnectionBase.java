/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
package eu.mihosoft.vrl.workflow.impl;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

/**
 * This class provides a base implementation of connections.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ConnectionBase implements Connection {
    protected String id;
    protected String type;
    protected ObjectProperty<VisualizationRequest> visualizationRequest;
    protected Connections connections;
    protected Connector sender;
    protected Connector receiver;

    public ConnectionBase(Connections connections, String id, Connector sender, Connector receiver, String type) {
        this.connections = connections;
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        updateConnection();
    }

    protected void updateConnection() {
        if (getSender() != null && getReceiver() != null) {
            if (connections.get(getId(), getSender(), getReceiver()) != null) {
                connections.remove(this);
                connections.add(this);
            }
        }
    }

    @Override
    public void setSender(Connector s) {
        this.sender = s;
        updateConnection();
    }

    @Override
    public void setReceiver(Connector r) {
        this.receiver = r;
        updateConnection();
    }

    @Override
    public void setId(String id) {
        this.id = id;
        updateConnection();
    }

    @Override
    public String toString() {
        return "c: " + getId() + " = s: [" + getSender().getId() + "] -> r: [" + getReceiver().getId() + "]";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return visualizationRequestProperty().getValue();
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        writableVisualizationRequestProperty().set(vReq);
    }

    private ObjectProperty<VisualizationRequest> writableVisualizationRequestProperty() {
        if (visualizationRequest == null) {
            visualizationRequest = new SimpleObjectProperty<>(this, "visualizationRequest", new DefaultVisualizationRequest());
        }

        return visualizationRequest;
    }

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        return writableVisualizationRequestProperty();
    }

    @Override
    public Connections getConnections() {
        return connections;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        if (getSender() != null) {
            hash = 37 * hash + Objects.hashCode(getSender().getId());
        }
        if (getReceiver() != null) {
            hash = 37 * hash + Objects.hashCode(getReceiver().getId());
        }
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass() || !(obj instanceof Connection)) {
            return false;
        }

        Connection other = (Connection) obj;
        if (connectorIdsAreDifferent(sender, other.getSender())) {
            return false;
        }
        if (connectorIdsAreDifferent(receiver, other.getReceiver())) {
            return false;
        }
        if (!Objects.equals(this.id, other.getId())) {
            return false;
        }
        if (!Objects.equals(this.type, other.getType())) {
            return false;
        }
        return true;
    }

    protected boolean connectorIdsAreDifferent(Connector a, Connector b) {
        return a != null && b != null && !Objects.equals(a.getId(), b.getId());
    }

    @Override
    public Connector getSender() {
        return sender;
    }

    @Override
    public Connector getReceiver() {
        return receiver;
    }

    @Override
    public boolean isVisualizationRequestInitialized() {
        return visualizationRequest != null;
    }
}

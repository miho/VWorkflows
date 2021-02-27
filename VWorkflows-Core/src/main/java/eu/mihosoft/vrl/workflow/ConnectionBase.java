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
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

/**
 * This class provides a base implementation of connections.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ConnectionBase implements Connection {

    private String senderId;
    private String receiverId;
    private String id;
    private String type;
    private ObjectProperty<VisualizationRequest> vReqProperty;
    private Connections connections;
    private Connector sender;
    private Connector receiver;

//    private ObjectProperty<Skin> skinProperty = new SimpleObjectProperty<>();
    public ConnectionBase() {
    }

//    public ConnectionBase(Connections connections, String id, String senderId, String receiverId, String type) {
//        this.connections = connections;
//        this.id = id;
//        this.senderId = senderId;
//        this.receiverId = receiverId;
//        this.type = type;
//    }
    public ConnectionBase(Connections connections, String id, Connector sender, Connector receiver, String type) {
        this.connections = connections;
        this.id = id;
        setSender(sender);
        setReceiver(receiver);
        this.type = type;
    }

//    @Override
//    public String getSenderId() {
//        return senderId;
//    }
    @Override
    public void setSender(Connector s) {
        this.senderId = s.getId();
        this.sender = s;

        updateConnection();
    }

    private void updateConnection() {
        if (getSender() != null && getReceiver() != null) {
            if (connections.get(getId(), getSender(), getReceiver()) != null) {
                connections.remove(this);
                connections.add(this);
            }
        }
    }

//    @Override
//    public String getReceiverId() {
//        return receiverId;
//    }
    @Override
    public void setReceiver(Connector r) {
        this.receiverId = r.getId();
        this.receiver = r;

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
    public void setId(String id) {
        this.id = id;

        updateConnection();
    }

    /**
     * @return the vReq
     */
    @Override
    public VisualizationRequest getVisualizationRequest() {
        return visualizationRequestProperty().getValue();
    }

    /**
     * @param vReq the vReq to set
     */
    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        _visualizationRequestProperty().set(vReq);
    }

    private ObjectProperty<VisualizationRequest> _visualizationRequestProperty() {
        if (vReqProperty == null) {
            vReqProperty = new SimpleObjectProperty<>();
            setVisualizationRequest(new VisualizationRequestImpl());
        }

        return vReqProperty;
    }

    @Override
    public ReadOnlyProperty<VisualizationRequest> visualizationRequestProperty() {
        return _visualizationRequestProperty();
    }
//    @Override
//    public void setSkin(Skin<?> skin) {
//        skinProperty.set(skin);
//    }
//
//    @Override
//    public Skin<?> getSkin() {
//        return skinProperty.get();
//    }
//
//    @Override
//    public ObjectProperty<?> skinProperty() {
//        return skinProperty;
//    }

    /**
     * @return the connections
     */
    @Override
    public Connections getConnections() {
        return connections;
    }

    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.senderId);
        hash = 37 * hash + Objects.hashCode(this.receiverId);
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConnectionBase other = (ConnectionBase) obj;
        if (!Objects.equals(this.senderId, other.senderId)) {
            return false;
        }
        if (!Objects.equals(this.receiverId, other.receiverId)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }

    /**
     * @return the sender
     */
    @Override
    public Connector getSender() {
        return sender;
    }

    /**
     * @return the receiver
     */
    @Override
    public Connector getReceiver() {
        return receiver;
    }
    
    @Override
    public boolean isVisualizationRequestInitialized() {
        return vReqProperty != null;
    }

}

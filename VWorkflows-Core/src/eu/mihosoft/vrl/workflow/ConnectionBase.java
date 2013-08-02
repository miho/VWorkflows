/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Objects;


/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
class ConnectionBase implements Connection {

    private String senderId;
    private String receiverId;
    private String id;
    private String type;
    private VisualizationRequest vReq;
    private Connections connections;


//    private ObjectProperty<Skin> skinProperty = new SimpleObjectProperty<>();
    public ConnectionBase() {
    }

    public ConnectionBase(Connections connections, String id, String senderId, String receiverId, String type) {
        this.connections = connections;
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
    }

    @Override
    public String getSenderId() {
        return senderId;
    }

    @Override
    public void setSenderId(String id) {
        this.senderId = id;

        updateConnection();
    }

    private void updateConnection() {
        if (connections.get(getId(), getSenderId(), getReceiverId()) != null) {
            connections.remove(this);
            connections.add(this);
        }
    }

    @Override
    public String getReceiverId() {
        return receiverId;
    }

    @Override
    public void setReceiverId(String id) {
        this.receiverId = id;

        updateConnection();
    }

    @Override
    public String toString() {
        return "c: " + getId() + " = s: [" + getSenderId() + "] -> r: [" + getReceiverId() + "]";
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
        return vReq;
    }

    /**
     * @param vReq the vReq to set
     */
    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vReq = vReq;
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

    
}

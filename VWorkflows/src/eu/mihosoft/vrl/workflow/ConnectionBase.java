/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectionBase implements Connection {

    private String senderId;
    private String receiverId;
    private String id;
    private String type;
    private ObjectProperty<VisualizationRequest> vReq = new SimpleObjectProperty<>();
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
        return vReq.get();
    }

    /**
     * @param vReq the vReq to set
     */
    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vReq.set(vReq);
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
    public ObjectProperty<VisualizationRequest> visualizationRequestProperty() {
        return this.vReq;
    }

}

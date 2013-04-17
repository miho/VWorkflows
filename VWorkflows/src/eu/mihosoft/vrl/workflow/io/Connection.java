/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.io;

import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.VisualizationRequest;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Connection {
    
    private String id;
    private String senderId;
    private String receiverId;
    private String type;
    
    private VisualizationRequest vReq;

    public Connection() {
    }

    public Connection(String id, String senderId, String receiverId, String type, VisualizationRequest vReq) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.vReq = vReq;
    }
    

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the receiverId
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * @param receiverId the receiverId to set
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return the vReq
     */
    public VisualizationRequest getVReq() {
        return vReq;
    }

    /**
     * @param vReq the vReq to set
     */
    public void setVReq(VisualizationRequest vReq) {
        this.vReq = vReq;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}

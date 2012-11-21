/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ConnectionBase implements Connection{
    
    private String senderId;
    private String receiverId;
    private String id;

    public ConnectionBase() {
    }

    public ConnectionBase(String id, String senderId, String receiverId) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    

    @Override
    public String getSenderId() {
        return senderId;
    }

    @Override
    public void setSenderId(String id) {
        this.senderId = id;
    }

    @Override
    public String getReceiverId() {
        return receiverId;
    }

    @Override
    public void setReceiverId(String id) {
        this.receiverId = id;
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
    }
}

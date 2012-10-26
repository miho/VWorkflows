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

    public ConnectionBase() {
    }

    public ConnectionBase(String senderId, String receiverId) {
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
        return "s: [" + getSenderId() + "] -> r: [" + getReceiverId() + "]";
    }
}

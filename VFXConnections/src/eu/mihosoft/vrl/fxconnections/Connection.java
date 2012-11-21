/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connection extends Model{
    public String getSenderId();
    public void setSenderId(String id);
    public String getReceiverId();
    public void setReceiverId(String id);
    public String getId();
    public void setId(String id);
    
}

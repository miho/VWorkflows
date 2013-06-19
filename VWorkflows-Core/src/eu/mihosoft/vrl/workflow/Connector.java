/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Connector extends Model{

    public String getType();

    public String getId();

    public String getLocalId();

    public void setLocalId(String id);

    public VNode getNode();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Flow {

    public ConnectionResult tryConnect(FlowNode s, FlowNode r);
    
    public ConnectionResult connect(FlowNode s, FlowNode r);

    public FlowNode remove(FlowNode n);
    
    public Iterable<FlowNode> getNodes();
}

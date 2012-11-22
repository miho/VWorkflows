/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Flow {

    public ConnectionResult tryConnect(FlowNode s, FlowNode r);
    
    public ConnectionResult connect(FlowNode s, FlowNode r);

    public FlowNode remove(FlowNode n);
    
    public ObservableList<FlowNode> getNodes();
    
    public FlowNode getSender(Connection c);
    public FlowNode getReceiver(Connection c);
    
    public Connections getConnections();
}

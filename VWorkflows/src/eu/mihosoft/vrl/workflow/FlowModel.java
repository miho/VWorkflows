/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Collection;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface FlowModel extends Model{

    public ConnectionResult tryConnect(FlowNode s, FlowNode r, String flowType);
    
    public ConnectionResult connect(FlowNode s, FlowNode r, String flowType);

    public FlowNode remove(FlowNode n);
    
    public ObservableList<FlowNode> getNodes();
    
    public FlowNode getSender(Connection c);
    public FlowNode getReceiver(Connection c);
    
    public void addConnections(Connections connections, String flowType);
    public Connections getConnections(String flowType);
    public ObservableMap<String,Connections> getAllConnections();
    
    public void setFlowNodeClass(Class<? extends FlowNode> cls);
    
    public Class<? extends FlowNode> getFlowNodeClass();
    
    public FlowNode newNode(ValueObject obj);
    
    public FlowNode newNode();
    
    public FlowFlowNode newFlowNode(ValueObject obj);
    
    public FlowFlowNode newFlowNode();

}

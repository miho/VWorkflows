/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import com.sun.javafx.collections.UnmodifiableObservableMap;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
interface FlowModel extends Model, VisibleState{

    public ConnectionResult tryConnect(VNode s, VNode r, String flowType);
    
    public ConnectionResult connect(VNode s, VNode r, String flowType);

    public VNode remove(VNode n);
    
    public void clear();
    
    public ObservableList<VNode> getNodes();
    
    public VNode getSender(Connection c);
    public VNode getReceiver(Connection c);
    
    public void addConnections(Connections connections, String flowType);
    public Connections getConnections(String flowType);
    public UnmodifiableObservableMap<String,Connections> getAllConnections();
    
    public void setFlowNodeClass(Class<? extends VNode> cls);
    
    public Class<? extends VNode> getFlowNodeClass();
    
    public void setIdGenerator(IdGenerator generator);
    public IdGenerator getIdGenerator();
    
    public void setNodeLookup(NodeLookup nodeLookup);
    public NodeLookup getNodeLookup();


}

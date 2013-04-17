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

    public ConnectionResult tryConnect(Connector s, Connector r);
    
    public ConnectionResult connect(Connector s, Connector r);

    public FlowNode remove(FlowNode n);
    
    public void clear();
    
    public ObservableList<FlowNode> getNodes();
    
    public Connector getSender(Connection c);
    public Connector getReceiver(Connection c);
    
    public void addConnections(Connections connections, String flowType);
    public Connections getConnections(String flowType);
    public UnmodifiableObservableMap<String,Connections> getAllConnections();
    
    public void setFlowNodeClass(Class<? extends FlowNode> cls);
    
    public Class<? extends FlowNode> getFlowNodeClass();
    
    public void setIdGenerator(IdGenerator generator);
    public IdGenerator getIdGenerator();
    
    public void setNodeLookup(NodeLookup nodeLookup);
    public NodeLookup getNodeLookup();


}

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

    public ConnectionResult tryConnect(FlowNode s, FlowNode r, String flowType);
    
    public ConnectionResult connect(FlowNode s, FlowNode r, String flowType);

    public FlowNode remove(FlowNode n);
    
    public void clear();
    
    public ObservableList<FlowNode> getNodes();
    
    public FlowNode getSender(Connection c);
    public FlowNode getReceiver(Connection c);
    
    public void addConnections(Connections connections, String flowType);
    public Connections getConnections(String flowType);
    public UnmodifiableObservableMap<String,Connections> getAllConnections();
    
    public void setFlowNodeClass(Class<? extends FlowNode> cls);
    
    public Class<? extends FlowNode> getFlowNodeClass();
    
    public void setIdGenerator(IdGenerator generator);
    public IdGenerator getIdGenerator();


}

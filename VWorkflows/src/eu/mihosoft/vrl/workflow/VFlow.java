/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Collection;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface VFlow {

    public void setModel(VFlowModel flow);

    public void setNodeLookup(NodeLookup nodeLookup);

    public NodeLookup getNodeLookup();

    public VFlowModel getModel();

    public ObjectProperty modelProperty();

    public ConnectionResult tryConnect(VNode s, VNode r, String flowType);

    public ConnectionResult connect(VNode s, VNode r, String flowType);

    public VNode remove(VNode n);

    public ObservableList<VNode> getNodes();

    public VNode getSender(Connection c);

    public VNode getReceiver(Connection c);

    public void addConnections(Connections connections, String flowType);

    public Connections getConnections(String flowType);

    public void setFlowNodeClass(Class<? extends VNode> cls);

    public Class<? extends VNode> getFlowNodeClass();

    public VNode newNode(ValueObject obj);

    public VNode newNode();

    public VFlow newSubFlow(ValueObject obj);

    public VFlow newSubFlow();

    public Collection<VFlow> getSubControllers();

    public void setSkinFactory(SkinFactory<? extends ConnectionSkin, ? extends FlowNodeSkin> skinFactory);

    public void setIdGenerator(IdGenerator generator);

    public IdGenerator getIdGenerator();

    public FlowNodeSkin getNodeSkinById(String id);
    
    public FlowNodeSkinLookup getNodeSkinLookup();
    public void setNodeSkinLookup(FlowNodeSkinLookup skinLookup);
}

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
public interface FlowController {

    public void setModel(FlowFlowNode flow);

    public void setNodeLookup(NodeLookup nodeLookup);

    public NodeLookup getNodeLookup();

    public FlowFlowNode getModel();

    public ObjectProperty modelProperty();

    public ConnectionResult tryConnect(FlowNode s, FlowNode r, String flowType);

    public ConnectionResult connect(FlowNode s, FlowNode r, String flowType);

    public FlowNode remove(FlowNode n);

    public ObservableList<FlowNode> getNodes();

    public FlowNode getSender(Connection c);

    public FlowNode getReceiver(Connection c);

    public void addConnections(Connections connections, String flowType);

    public Connections getConnections(String flowType);

    public void setFlowNodeClass(Class<? extends FlowNode> cls);

    public Class<? extends FlowNode> getFlowNodeClass();

    public FlowNode newNode(NodeValueObject obj);

    public FlowNode newNode();

    public FlowController newSubFlow(NodeValueObject obj);

    public FlowController newSubFlow();

    public Collection<FlowController> getSubControllers();

    public void setSkinFactory(SkinFactory<? extends ConnectionSkin, ? extends FlowNodeSkin> skinFactory);

    public void setIdGenerator(IdGenerator generator);

    public IdGenerator getIdGenerator();

    public FlowNodeSkin getNodeSkinById(String id);
    
    public FlowNodeSkinLookup getNodeSkinLookup();
    public void setNodeSkinLookup(FlowNodeSkinLookup skinLookup);
}

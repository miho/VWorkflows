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
public final class DefaultWorkflow implements WorkFlow {

    private ControlFlow controlFlow = new ControlFlowImpl();
//    private DataFlow dataFlow = new DataFlow();
    private ObjectProperty<FlowNodeSkinFactory> flowNodeSkinFactory;
    private ObjectProperty<ConnectionSkinFactory> connectionSkinFactory;

    public DefaultWorkflow(FlowNodeSkinFactory flowNodeSkinFactory, ConnectionSkinFactory connectionSkinFactory) {
        setNodeSkinFactory(flowNodeSkinFactory);
        setConnectionSkinFactory(connectionSkinFactory);
    }

    @Override
    public DataFlow getDataFlow() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ControlFlow getControlFlow() {
        return controlFlow;
    }

    @Override
    public final void setNodeSkinFactory(FlowNodeSkinFactory factory) {
        this.flowNodeSkinFactory = factory;
        this.controlFlow.setNodeSkinFactory(factory);
    }

    @Override
    public final void setConnectionSkinFactory(ConnectionSkinFactory factory) {
        this.connectionSkinFactory = factory;
        this.controlFlow.setConnectionSkinFactory(factory);
    }

    @Override
    public FlowNode remove(FlowNode n) {
        FlowNode result = nodes.remove(n.getId());
        observableNodes.remove(n);

        removeNodeSkin(n);

        Collection<Connection> connectionsToRemove =
                getConnections().getAllWith(n.getId());

        for (Connection c : connectionsToRemove) {
            getConnections().remove(c);
            removeConnectionSkin(c);
        }

        return result;
    }
}

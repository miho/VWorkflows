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
public class DefaultWorkflow implements Workflow {

    private ControlFlow controlFlow = new ControlFlowImpl();
//    private DataFlow dataFlow = new DataFlow();
    private FlowNodeSkinFactory flowNodeSkinFactory;
    private ConnectionSkinFactory connectionSkinFactory;

    public DefaultWorkflow(FlowNodeSkinFactory flowNodeSkinFactory, ConnectionSkinFactory connectionSkinFactory) {
        this.flowNodeSkinFactory = flowNodeSkinFactory;
        this.connectionSkinFactory = connectionSkinFactory;
    }
    

    @Override
    public DataFlow getDataFlow() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ControlFlow getControlFlow() {
        return new ControlFlow() {

            @Override
            public FlowNode newNode() {
                FlowNode n = controlFlow.newNode();
                flowNodeSkinFactory.createSkin(n).add();
                
                return n;
            }

            @Override
            public ConnectionResult tryConnect(FlowNode s, FlowNode r) {
                return controlFlow.tryConnect(s, r);
            }

            @Override
            public ConnectionResult connect(FlowNode s, FlowNode r) {
                ConnectionResult result = controlFlow.connect(s, r);
                
                if (result.getStatus().isCompatible()) {
                    connectionSkinFactory.createSkin(result.getConnection(), controlFlow).add();
                }
                
                return result;
            }

            @Override
            public FlowNode remove(FlowNode n) {
                return controlFlow.remove(n);
            }

            @Override
            public ObservableList<FlowNode> getNodes() {
                return controlFlow.getNodes();
            }

            @Override
            public FlowNode getSender(Connection c) {
                return controlFlow.getSender(c);
            }

            @Override
            public FlowNode getReceiver(Connection c) {
                return controlFlow.getReceiver(c);
            }

            @Override
            public Connections getConnections() {
                return controlFlow.getConnections();
            }

            @Override
            public void setFlowNodeClass(Class<? extends FlowNode> cls) {
                controlFlow.setFlowNodeClass(cls);
            }

            @Override
            public Class<? extends FlowNode> getFlowNodeClass() {
                return controlFlow.getFlowNodeClass();
            }

            @Override
            public FlowNode newNode(ValueObject obj) {
                FlowNode n = controlFlow.newNode();
                
                flowNodeSkinFactory.createSkin(n).add();
                
                return n;
            }
        };
    }

    @Override
    public void setFlowNodeSkinFactory(FlowNodeSkinFactory factory) {
        this.flowNodeSkinFactory = factory;
    }

    @Override
    public void setConnectionSkinFactory(ConnectionSkinFactory factory) {
        this.connectionSkinFactory = factory;
    }
}

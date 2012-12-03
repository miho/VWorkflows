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
public final class DefaultWorkflow extends FlowBase {

//    private ControlFlow controlFlow = new ControlFlowImpl();
////    private DataFlow dataFlow = new DataFlow();
//    private FlowNodeSkinFactory flowNodeSkinFactory;
//    private ConnectionSkinFactory connectionSkinFactory;
    

    public DefaultWorkflow(FlowNodeSkinFactory flowNodeSkinFactory,
            ConnectionSkinFactory connectionSkinFactory) {
        setNodeSkinFactory(flowNodeSkinFactory);
        setConnectionSkinFactory(connectionSkinFactory);
        
        addConnections(VConnections.newConnections(), "control");
    }
    

//    @Override
//    public DataFlow getDataFlow() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public ControlFlow getControlFlow() {
//        return controlFlow;
//    }

//    @Override
//    public final void setNodeSkinFactory(FlowNodeSkinFactory factory) {
//        this.flowNodeSkinFactory = factory;
//        this.controlFlow.setNodeSkinFactory(factory);
//    }
//
//    @Override
//    public final void setConnectionSkinFactory(ConnectionSkinFactory factory) {
//        this.connectionSkinFactory = factory;
//        this.controlFlow.setConnectionSkinFactory(factory);
//    }
}

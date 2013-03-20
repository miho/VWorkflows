/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class DefaultWorkflow extends FlowControllerImpl {

    public DefaultWorkflow(FlowNodeSkinFactory flowNodeSkinFactory,
            ConnectionSkinFactory connectionSkinFactory) {
        
        super(flowNodeSkinFactory, connectionSkinFactory);
        
        setModel(new FlowFlowNodeImpl(null));
        
        addConnections(VConnections.newConnections(), "control");
        addConnections(VConnections.newConnections(), "data");
        addConnections(VConnections.newConnections(), "event");
    }
    
}

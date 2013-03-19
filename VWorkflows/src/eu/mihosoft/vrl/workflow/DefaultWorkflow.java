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

    public DefaultWorkflow(FlowModel model, FlowNodeSkinFactory flowNodeSkinFactory,
            ConnectionSkinFactory connectionSkinFactory) {
        
        super(flowNodeSkinFactory, connectionSkinFactory);
        
        setModel(model);
        
        addConnections(VConnections.newConnections(), "control");
        addConnections(VConnections.newConnections(), "data");
        addConnections(VConnections.newConnections(), "event");
    }
    
}

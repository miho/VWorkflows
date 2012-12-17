/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class DefaultWorkflow extends FlowBase {

    public DefaultWorkflow(FlowNodeSkinFactory flowNodeSkinFactory,
            ConnectionSkinFactory connectionSkinFactory) {
        setNodeSkinFactory(flowNodeSkinFactory);
        setConnectionSkinFactory(connectionSkinFactory);
        
        addConnections(VConnections.newConnections(), "control");
        addConnections(VConnections.newConnections(), "data");
        addConnections(VConnections.newConnections(), "event");
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import eu.mihosoft.vrl.workflow.io.Node;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowFactory {

    public static FlowController newFlow() {
        FlowController flow = new FlowControllerImpl(null);

        return flow;
    }

    public static FlowController newFlow(
            SkinFactory<? extends ConnectionSkin,? extends FlowNodeSkin> skinFactory) {
        FlowController flow = new FlowControllerImpl(skinFactory);

        return flow;
    }
    
    public static FlowFlowNode newFlowModel() {
        FlowFlowNode result = new FlowFlowNodeImpl(null);
        result.setId("ROOT");
        return result;
    }
    
    public static int numNodes() {
        return FlowNodeBase.numInstances;
    }
    
}

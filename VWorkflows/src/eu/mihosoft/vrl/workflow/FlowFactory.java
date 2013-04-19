/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import eu.mihosoft.vrl.workflow.io.PersistentNode;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowFactory {

    public static VFlow newFlow() {
        VFlow flow = new VFlowImpl(null);

        VFlowModel model = FlowFactory.newFlowModel();

        return flow;
    }

    public static VFlow newFlow(
            SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory) {
        VFlow flow = new VFlowImpl(skinFactory);

        VFlowModel model = FlowFactory.newFlowModel();

        return flow;
    }

    public static VFlowModel newFlowModel() {
        VFlowModel result = new VFlowModelImpl(null);
        result.setId("ROOT");
        return result;
    }

    public static int numNodes() {
        return VNodeImpl.numInstances;
    }
}

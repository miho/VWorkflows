/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowFactory {

    public static VFlow newFlow() {
        VFlow flow = new FlowControllerImpl(null);

        flow.setModel(FlowFactory.newFlowModel());

        return flow;
    }

    public static VFlow newFlow(
            SkinFactory<? extends ConnectionSkin, ? extends FlowNodeSkin> skinFactory) {
        VFlow flow = new FlowControllerImpl(skinFactory);

        flow.setModel(FlowFactory.newFlowModel());

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

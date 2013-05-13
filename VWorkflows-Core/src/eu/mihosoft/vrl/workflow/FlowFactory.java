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
        VFlow flow = new VFlowImpl();

        VFlowModel model = FlowFactory.newFlowModel();
        flow.setModel(model);

        return flow;
    }

    public static VFlow newFlow(
            SkinFactory<? extends ConnectionSkin, ? extends VNodeSkin> skinFactory) {
        VFlow flow = new VFlowImpl(skinFactory);

        VFlowModel model = FlowFactory.newFlowModel();
        flow.setModel(model);

        return flow;
    }

    public static VFlowModel newFlowModel() {
        VFlowModel result = new VFlowModelImpl(null);
        result.setId("ROOT");
        return result;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Playground {

    public void play() {

        Flow<FlowNode> flow = new ControlFlow();

        FlowNode n1 = new FlowNodeBase();
        FXFlowNodeSkin n1Skin = new FXFlowNodeSkin(n1, flow);

        FlowNode n2 = new FlowNodeBase();
        FXFlowNodeSkin n2Skin = new FXFlowNodeSkin(n2, flow);

        flow.connect(n1, n2);
    }
}

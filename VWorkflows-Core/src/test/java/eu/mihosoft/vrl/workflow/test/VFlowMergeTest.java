/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.test;

import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VFlowMergeTest {

    @Ignore("Not Ready to Run")
    public void testAdd() {
        VFlow parent = FlowFactory.newFlow();
        addNodesToFlow(parent.newSubFlow(), 3, "control", "data");
        VFlow subflow = FlowFactory.newFlow();
        addNodesToFlow(subflow, 3, "control", "data");
//        parent.add(subflow);

        VFlow expectedResult = FlowFactory.newFlow();
        addNodesToFlow(expectedResult, 3, "control", "data");
        addNodesToFlow(expectedResult.newSubFlow(), 3, "control", "data");
        addNodesToFlow(expectedResult.newSubFlow(), 3, "control", "data");
    }

    private void addNodesToFlow(VFlow flow, int numNodes, String... types) {
        VNode prevNode = null;
        for (int i = 0; i < numNodes; i++) {
            VNode node = flow.newNode();

            for (String type : types) {
                node.setMainInput(node.addInput(type));
                node.setMainOutput(node.addOutput(type));

                if (prevNode != null) {
                    flow.connect(prevNode, node, type);
                }
            }

            prevNode = node;
        }
    }
}

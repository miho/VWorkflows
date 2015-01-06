/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FlowUtil {

    /**
     * Creates a flow with specified width and depth.
     *
     * @param workflow parent workflow
     * @param depth flow depth (number of nested nodes)
     * @param width flow width (number of nodes per layer)
     */
    public static void createFlow(VFlow workflow, int depth, int width) {

        // stop if we reached deppest layer
        if (depth < 1) {
            return;
        }

        String[] connectionTypes = {"control", "data", "event"};

        List<VNode> prevNodes = new ArrayList<>();

        // create nodes in current layer
        for (int i = 0; i < width; i++) {

            VNode n;

            // every second node shall be a subflow
            if (i % 2 == 0) {
                // create subflow
                VFlow subFlow = workflow.newSubFlow();
                n = subFlow.getModel();
                createFlow(subFlow, depth - 1, width);
            } else {
                //create leaf node
                n = workflow.newNode();
            }

            n.setTitle("Node " + i);

            // every third node shall have the same connection type
            // colors for "control", "data" and "event" are currently hardcoded
            // in skin. This will change!
            String type = connectionTypes[i % connectionTypes.length];

            n.addInput(type);
            n.addOutput(type);

            // specify node size
            n.setWidth(300);
            n.setHeight(200);

            // gap between nodes
            int gap = 30;

            int numNodesPerRow = 3;

            // specify node position (we use grid layout)
            n.setX(gap + (i % numNodesPerRow) * (n.getWidth() + gap));
            n.setY(gap + (i / numNodesPerRow) * (n.getHeight() + gap));

            if (i >= connectionTypes.length) {

                workflow.connect(prevNodes.get(0).getOutputs().get(0),
                        n.getInputs().get(0));

                prevNodes.remove(0);
            }

            prevNodes.add(n);
        }
    }
}

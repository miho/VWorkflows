/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.worflow.layout;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class LayoutFactory {

    public static Layout newDefaultLayout() {
        return new DefaultLayoutImpl();
    }
}

class DefaultLayoutImpl implements Layout {

    @Override
    public void doLayout(VFlow flow) {

        Map<Integer, List<VNode>> nodes = new HashMap<>();

        Set<VNode> processed = new HashSet<>();

        double maxWidth = 0;
        double maxHeight = 0;

        double xGap = 50;
        double yGap = 50;

        // find senders
        for (VNode n : flow.getNodes()) {
            boolean noInput = !flow.getConnections("control").isInputConnected(n.getId());
//            boolean output = flow.getConnections("control").isOutputConnected(n.getId());

            if (noInput) {
                addNodeToLayer(nodes, n, 0);

                maxWidth = Math.max(maxWidth, n.getWidth());
                maxHeight = Math.max(maxHeight, n.getHeight());
            }
        }

        int layer = 0;

        List<VNode> layerOfNodes = getLayer(nodes, layer);

        while (!layerOfNodes.isEmpty()) {

            layer++;

            // compute layers
            for (VNode n : layerOfNodes) {
                for (Connection c : flow.getConnections("control").getAllWith(n.getId())) {
                    VNode r = flow.getNodeLookup().getById(c.getReceiverId());
                    if (!processed.contains(r)) {
                        addNodeToLayer(nodes, r, layer);
                        processed.add(r);

                        maxWidth = Math.max(maxWidth, r.getWidth());
                        maxHeight = Math.max(maxHeight, r.getHeight());
                    }
                }
            }

            layerOfNodes = getLayer(nodes, layer);
        }

        double layerX = 10;

        for (int i = 0; i <= layer; i++) {

            double layerY = 10;

            for (VNode n : getLayer(nodes, i)) {

                double yOffset = 0;

                for (Connection c : flow.getConnections("control").getAllWith(n.getId())) {
                    yOffset = flow.getNodeLookup().getById(c.getSenderId()).getY();
                }


                layerY = Math.max(yOffset, layerY);

                n.setX(layerX);
                n.setY(layerY);

                layerY += maxHeight + yGap;
            }

            layerX += maxWidth + xGap;
        }

        for (VFlow subFlow : flow.getSubControllers()) {
            doLayout(subFlow);
        }


    }

    private void addNodeToLayer(Map<Integer, List<VNode>> nodes, VNode n, int layer) {
        getLayer(nodes, layer).add(n);
    }

    private List<VNode> getLayer(Map<Integer, List<VNode>> nodes, int layer) {
        if (nodes.get(layer) == null) {
            nodes.put(layer, new ArrayList<VNode>());
        }

        return nodes.get(layer);
    }
}

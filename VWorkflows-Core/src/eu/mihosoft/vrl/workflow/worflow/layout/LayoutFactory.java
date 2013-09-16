/*
 * LayoutFactory.java
 * 
 * Copyright 2012-2013 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
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

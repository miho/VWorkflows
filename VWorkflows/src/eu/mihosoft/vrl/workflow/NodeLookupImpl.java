/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class NodeLookupImpl implements NodeLookup {

    private FlowFlowNode root;

    public NodeLookupImpl(FlowFlowNode root) {
        this.root = root;
    }

    @Override
    public FlowNode getNodeById(String globalId) {
        return getNodeByGlobalId(root, globalId);
    }

    private FlowNode getNodeByGlobalId(FlowFlowNode parent, String id) {

        List<FlowFlowNode> subFlows = new ArrayList<>();

        for (FlowNode n : parent.getNodes()) {
            if (n.getId().equals(id)) {
                return n;
            }

            if (n instanceof FlowFlowNode) {
                subFlows.add((FlowFlowNode) n);
            }
        }

        for (FlowFlowNode subFlow : subFlows) {
            return getNodeByGlobalId(subFlow, id);
        }

        return null;
    }
}

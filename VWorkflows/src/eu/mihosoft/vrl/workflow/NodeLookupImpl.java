/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

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
    public FlowNode getById(String globalId) {

        FlowNode result = getNodeByGlobalId(root, globalId);

        return result;
    }

    private FlowNode getNodeByGlobalId(FlowModel parent, String id) {

        for (FlowNode n : parent.getNodes()) {
            if (n.getId().equals(id)) {
                return n;
            }

            if (n instanceof FlowModel) {
               FlowNode result = getNodeByGlobalId((FlowModel)n, id);
               if (result!=null) {
                   return result;
               }
            }
        }


        return null;
    }
}

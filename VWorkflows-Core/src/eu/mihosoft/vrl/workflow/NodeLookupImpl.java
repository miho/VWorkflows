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

    private VFlowModel root;

    public NodeLookupImpl(VFlowModel root) {
        this.root = root;
    }

    @Override
    public VNode getById(String globalId) {

        VNode result = getNodeByGlobalId(root, globalId);

        return result;
    }

    private VNode getNodeByGlobalId(FlowModel parent, String id) {

        for (VNode n : parent.getNodes()) {
            if (n.getId().equals(id)) {
                return n;
            }

            if (n instanceof FlowModel) {
               VNode result = getNodeByGlobalId((FlowModel)n, id);
               if (result!=null) {
                   return result;
               }
            }
        }


        return null;
    }
}

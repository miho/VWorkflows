/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FlowNodeSkinLookupImpl implements FlowNodeSkinLookup {

    private FlowController root;

    public FlowNodeSkinLookupImpl(FlowController root) {
        this.root = root;
    }

    @Override
    public FlowNodeSkin getById(String globalId) {

        FlowNodeSkin result = getNodeByGlobalId(root, globalId);

        return result;
    }

    private FlowNodeSkin getNodeByGlobalId(FlowController parent, String id) {

        FlowNodeSkin s = parent.getNodeSkinById(id);

        if (s != null) {
            return s;
        }

        for (FlowController c : parent.getSubControllers()) {
            s = getNodeByGlobalId(c, id);

            if (s != null) {
                return s;
            }
        }

        return null;
    }
}

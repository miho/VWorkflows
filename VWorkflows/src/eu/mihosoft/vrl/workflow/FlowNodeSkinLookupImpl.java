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

    private VFlow root;

    public FlowNodeSkinLookupImpl(VFlow root) {
        this.root = root;
    }

    @Override
    public VNodeSkin getById(String globalId) {

        VNodeSkin result = getNodeByGlobalId(root, globalId);

        return result;
    }

    private VNodeSkin getNodeByGlobalId(VFlow parent, String id) {

        VNodeSkin s = parent.getNodeSkinById(id);

        if (s != null) {
            return s;
        }

        for (VFlow c : parent.getSubControllers()) {
            s = getNodeByGlobalId(c, id);

            if (s != null) {
                return s;
            }
        }

        return null;
    }
}

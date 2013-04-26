/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.List;

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
    public List<VNodeSkin> getById(String globalId) {

        List<VNodeSkin> result = getNodeByGlobalId(root, globalId);

        return result;
    }

    private List<VNodeSkin> getNodeByGlobalId(VFlow parent, String id) {

        List<VNodeSkin> s = parent.getNodeSkinsById(id);

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

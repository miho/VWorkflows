/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FlowNodeSkinLookupImpl implements FlowNodeSkinLookup{
    
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

//        for (FlowNode n : parent.getNodes()) {
//            if (n.getId().equals(id)) {
//                return n;
//            }
//
//            if (n instanceof FlowModel) {
//               FlowNode result = getNodeByGlobalId((FlowModel)n, id);
//               if (result!=null) {
//                   return result;
//               }
//            }
//        }

        for (FlowController c : parent.getSubControllers()) {
//            c.
        }

        return null;
    }
    
}

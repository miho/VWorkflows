/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Collection;
import java.util.List;
import javax.naming.OperationNotSupportedException;

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

    private VNodeSkin getNodeByGlobalId(SkinFactory skinFactory, VFlow parent, String id) {

        // find flow that contains the requested node
        VFlow flow = getFlowThatContains(parent, id);
//        
//        System.out.println("found flow: " + flow.getModel().getId());

        for (SkinFactory sF : flow.getSkinFactories()) {

            if (getRootSkinFactoryOf(skinFactory) == getRootSkinFactoryOf(sF)) {
                List<VNodeSkin> s2 = flow.getNodeSkinsById(id);
                return getBySkinFactory(sF, s2);
            }
        }
//
//        System.out.println(" --> nothing found :(");

        return null;
    }

    private SkinFactory getRootSkinFactoryOf(SkinFactory skinFactory) {
        // find root parent factory
        SkinFactory tmpFactory = skinFactory;
        SkinFactory parentFactory = skinFactory;

        while (tmpFactory != null) {
            tmpFactory = tmpFactory.getParent();
            if (tmpFactory != null) {
                parentFactory = tmpFactory;
            }
        }

        return parentFactory;
    }

    private VFlow getFlowThatContains(VFlow parent, String id) {

        // check if current controller contains node with specified id
        for (VNode n : parent.getNodes()) {
            if (n.getId().equals(id)) {
                return parent;
            }
        }

        Collection<VFlow> subflows = parent.getSubControllers();

        for (VFlow vFlow : subflows) {
            VFlow result = getFlowThatContains(vFlow, id);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private VNodeSkin getBySkinFactory(SkinFactory skinFactory, List<VNodeSkin> candidates) {

        for (VNodeSkin vNodeSkin : candidates) {

            if (vNodeSkin.getSkinFactory() == skinFactory) {
                return vNodeSkin;
            }
        }

        return null;
    }

    @Override
    public VNodeSkin getById(SkinFactory skinFactory, String globalId) {

        // support for connector ids, we wan't to return node skin if connector
        // id is given
        globalId = globalId.split(":")[0];

        VNodeSkin result = getNodeByGlobalId(skinFactory, root, globalId);

//        if (result != null) {
//            System.out.println("getById(): " + result);
//        } else {
//            System.out.println("NOT FOUND: getById(): " + null);
//        }

        return result;
    }

    @Override
    public ConnectionSkin<?> getById(SkinFactory skinFactory, Connection c) {

        String sender = c.getSenderId();
        String receiver = c.getReceiverId();

        VFlowModel senderFlow = root.getNodeLookup().getById(sender.split(":")[0]).getFlow();
        VFlowModel receiverFlow = root.getNodeLookup().getById(receiver.split(":")[0]).getFlow();

        if (senderFlow != receiverFlow) {
            throw new UnsupportedOperationException(
                    "Only skins for connections that share the same parent can be searched");
        }

        VFlow flow = root.getFlowById(senderFlow.getId());

        if (!(flow instanceof VFlowImpl)) {
            System.err.println("flow: " + flow);
            throw new UnsupportedOperationException(
                    "Unsupported flow class '"
                    + flow.getClass()
                    + "', should implement '" + VFlowImpl.class + "'");
        }

        VFlowImpl flowImpl = (VFlowImpl) flow;

        ConnectionSkin<Connection> skin = flowImpl.getConnectionSkinMap(skinFactory).get(
                VFlowImpl.connectionId(c));

        return skin;
    }
}

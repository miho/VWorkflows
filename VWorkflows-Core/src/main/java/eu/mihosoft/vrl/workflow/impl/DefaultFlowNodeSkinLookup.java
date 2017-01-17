/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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
package eu.mihosoft.vrl.workflow.impl;

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VFlowModel;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.skin.ConnectionSkin;
import eu.mihosoft.vrl.workflow.skin.FlowNodeSkinLookup;
import eu.mihosoft.vrl.workflow.skin.SkinFactory;
import eu.mihosoft.vrl.workflow.skin.VNodeSkin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static eu.mihosoft.vrl.workflow.util.ConnectionUtils.connectionId;

/**
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultFlowNodeSkinLookup implements FlowNodeSkinLookup {
    protected final VFlow root;

    public DefaultFlowNodeSkinLookup(VFlow root) {
        this.root = root;
    }

    @Override
    public List<VNodeSkin> getById(String globalId) {
        return getNodeByGlobalId(root, globalId);
    }

    protected List<VNodeSkin> getNodeByGlobalId(VFlow parent, String id) {
        VFlow flow = getFlowThatContains(parent, id);

        if (flow != null) {
            return flow.getNodeSkinsById(id);
        } else {
            return new ArrayList<>();
        }
    }

    protected VNodeSkin getNodeByGlobalId(SkinFactory skinFactory, VFlow parent, String id) {
        //        System.out.println("id: " + id);
        // find flow that contains the requested node
        VFlow flow;

        if (parent.getModel().getId().equals(id)) {
            flow = parent;
        } else {
            flow = getFlowThatContains(parent, id);
        }

        if (flow == null) {
            return null;
        }
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

    protected SkinFactory getRootSkinFactoryOf(SkinFactory skinFactory) {
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

    protected VFlow getFlowThatContains(VFlow parent, String id) {
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

    protected VNodeSkin getBySkinFactory(SkinFactory skinFactory, List<VNodeSkin> candidates) {
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
        return getNodeByGlobalId(skinFactory, root, resolveId(globalId));
    }

    protected String resolveId(String encodedId) {
        return encodedId.split(":c:")[0];
    }

    @Override
    public ConnectionSkin getById(SkinFactory skinFactory, Connection c) {
        String sender = c.getSender().getId();
        String receiver = c.getReceiver().getId();

        VFlowModel senderFlow = root.getNodeLookup().getById(resolveId(sender)).getFlow();
        VFlowModel receiverFlow = root.getNodeLookup().getById(resolveId(receiver)).getFlow();

        if (senderFlow != receiverFlow) {
            throw new UnsupportedOperationException("Only skins for connections that share the same parent can be searched");
        }

        VFlow flow = root.getFlowById(senderFlow.getId());
        ConnectionSkin skin = flow.getConnectionSkinMap(skinFactory).get(connectionId(c));
        return skin;
    }
}
